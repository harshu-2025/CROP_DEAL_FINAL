package com.cropdeal.wallet.service;

import com.cropdeal.wallet.client.UserClient;
import com.cropdeal.wallet.dto.*;
import com.cropdeal.wallet.entity.DealerWallet;
import com.cropdeal.wallet.entity.FarmerWallet;
import com.cropdeal.wallet.entity.WalletTransaction;
import com.cropdeal.wallet.entity.WalletTransactionType;
import com.cropdeal.wallet.repository.DealerWalletRepository;
import com.cropdeal.wallet.repository.FarmerWalletRepository;
import com.cropdeal.wallet.repository.WalletTransactionRepository;

import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import com.cropdeal.wallet.security.SecurityUtils;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.UUID;

@Service
public class WalletServiceImpl implements WalletService {

    private final DealerWalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final UserClient userClient;
    private final FarmerWalletRepository farmerWalletRepository;

    public WalletServiceImpl(DealerWalletRepository walletRepository,
                             WalletTransactionRepository transactionRepository,
                             UserClient userClient,FarmerWalletRepository farmerWalletRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.userClient = userClient;
        this.farmerWalletRepository=farmerWalletRepository;
    }

    @Override
    public WalletResponse creditWallet(Long dealerId, WalletCreditRequest request) {

        UserResponse dealer = userClient.getUserById(dealerId);

        if (!"DEALER".equalsIgnoreCase(dealer.getRole())) {
            throw new IllegalArgumentException("User is not a dealer");
        }

        if (!dealer.isActive()) {
            throw new IllegalArgumentException("Dealer account is inactive");
        }

        if (!SecurityUtils.getCurrentAuthUserId().equals(dealer.getAuthUserId())) {
            throw new AccessDeniedException("You can credit only your own dealer wallet");
        }

        DealerWallet wallet = walletRepository.findByDealerId(dealerId)
                .orElseGet(() -> {
                    DealerWallet newWallet = new DealerWallet();
                    newWallet.setDealerId(dealerId);
                    newWallet.setAvailableBalance(0.0);
                    newWallet.setBlockedBalance(0.0);
                    newWallet.setEscrowBalance(0.0);
                    newWallet.setCreatedAt(LocalDateTime.now());
                    newWallet.setUpdatedAt(LocalDateTime.now());
                    return newWallet;
                });

        wallet.setAvailableBalance(
                wallet.getAvailableBalance() + request.getAmount()
        );

        wallet.setUpdatedAt(LocalDateTime.now());

        DealerWallet savedWallet = walletRepository.save(wallet);

        WalletTransaction transaction = new WalletTransaction();

        WalletTransactionType creditType = request.getPaymentMethod() == BankTransferMethod.IMPS
                ? WalletTransactionType.IMPS_CREDIT
                : WalletTransactionType.NEFT_CREDIT;

        String transactionReference = request.getPaymentMethod().name()
                + "-" + Year.now().getValue()
                + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        transaction.setWalletId(savedWallet.getId());
        transaction.setDealerId(dealerId);
        transaction.setTransactionType(creditType);
        transaction.setAmount(request.getAmount());
        transaction.setReference(transactionReference);
        transaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);

        return mapToResponse(savedWallet);
    }

    @Override
    public WalletResponse getWalletByDealerId(Long dealerId) {

        ensureDealerOwnerOrAdmin(dealerId);

        DealerWallet wallet = walletRepository.findByDealerId(dealerId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Wallet not found")
                );

        return mapToResponse(wallet);
    }

    @Override
    public WalletResponse reserveFunds(Long dealerId, ReserveFundsRequest request) {

        ensureDealerOwnerOrAdmin(dealerId);

        DealerWallet wallet = walletRepository.findByDealerId(dealerId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Wallet not found")
                );

        if (wallet.getAvailableBalance() < request.getAmount()) {
            throw new IllegalArgumentException("Insufficient wallet balance");
        }

        wallet.setAvailableBalance(
                wallet.getAvailableBalance() - request.getAmount()
        );

        wallet.setBlockedBalance(
                wallet.getBlockedBalance() + request.getAmount()
        );

        wallet.setUpdatedAt(LocalDateTime.now());

        DealerWallet savedWallet = walletRepository.save(wallet);

        WalletTransaction transaction = new WalletTransaction();

        transaction.setWalletId(savedWallet.getId());
        transaction.setDealerId(dealerId);
        transaction.setTransactionType(WalletTransactionType.BID_RESERVED);
        transaction.setAmount(request.getAmount());
        transaction.setReference(request.getReference());
        transaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);

        return mapToResponse(savedWallet);
    }

    @Override
    public WalletResponse releaseFunds(Long dealerId, ReleaseFundsRequest request) {

        ensureDealerOwnerOrAdmin(dealerId);

        DealerWallet wallet = walletRepository.findByDealerId(dealerId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Wallet not found")
                );

        if (wallet.getBlockedBalance() < request.getAmount()) {
            throw new IllegalArgumentException("Insufficient blocked balance");
        }

        wallet.setBlockedBalance(
                wallet.getBlockedBalance() - request.getAmount()
        );

        wallet.setAvailableBalance(
                wallet.getAvailableBalance() + request.getAmount()
        );

        wallet.setUpdatedAt(LocalDateTime.now());

        DealerWallet savedWallet = walletRepository.save(wallet);

        WalletTransaction transaction = new WalletTransaction();

        transaction.setWalletId(savedWallet.getId());
        transaction.setDealerId(dealerId);
        transaction.setTransactionType(WalletTransactionType.BID_RELEASED);
        transaction.setAmount(request.getAmount());
        transaction.setReference(request.getReference());
        transaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);

        return mapToResponse(savedWallet);
    }

    private WalletResponse mapToResponse(DealerWallet wallet) {

        WalletResponse response = new WalletResponse();

        response.setId(wallet.getId());
        response.setDealerId(wallet.getDealerId());
        response.setAvailableBalance(wallet.getAvailableBalance());
        response.setBlockedBalance(wallet.getBlockedBalance());
        response.setEscrowBalance(wallet.getEscrowBalance() == null ? 0.0 : wallet.getEscrowBalance());

        return response;
    }
    @Override
    public List<WalletTransactionResponse> getTransactionsByDealer(Long dealerId) {

        ensureDealerOwnerOrAdmin(dealerId);

        return transactionRepository.findByDealerId(dealerId)
                .stream()
                .map(this::mapTransactionToResponse)
                .toList();
    }
    @Override
    public WalletResponse moveToEscrow(Long dealerId, ReserveFundsRequest request) {

        DealerWallet wallet = walletRepository.findByDealerId(dealerId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        if (wallet.getBlockedBalance() < request.getAmount()) {
            throw new IllegalArgumentException("Insufficient blocked balance");
        }

        wallet.setBlockedBalance(
                wallet.getBlockedBalance() - request.getAmount()
        );

        double escrowBalance = wallet.getEscrowBalance() == null ? 0.0 : wallet.getEscrowBalance();
        wallet.setEscrowBalance(escrowBalance + request.getAmount());

        wallet.setUpdatedAt(LocalDateTime.now());

        DealerWallet savedWallet = walletRepository.save(wallet);

        WalletTransaction transaction = new WalletTransaction();
        transaction.setWalletId(savedWallet.getId());
        transaction.setDealerId(dealerId);
        transaction.setTransactionType(WalletTransactionType.ESCROW_TRANSFER);
        transaction.setAmount(request.getAmount());
        transaction.setReference(request.getReference());
        transaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);

        return mapToResponse(savedWallet);
    }
    @Override
    public WalletResponse returnEscrowToBlocked(Long dealerId, ReserveFundsRequest request) {

        DealerWallet wallet = walletRepository.findByDealerId(dealerId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        double escrowBalance = wallet.getEscrowBalance() == null ? 0.0 : wallet.getEscrowBalance();
        if (escrowBalance < request.getAmount()) {
            throw new IllegalArgumentException("Insufficient escrow balance");
        }

        wallet.setEscrowBalance(escrowBalance - request.getAmount());
        wallet.setBlockedBalance(wallet.getBlockedBalance() + request.getAmount());
        wallet.setUpdatedAt(LocalDateTime.now());

        DealerWallet savedWallet = walletRepository.save(wallet);

        WalletTransaction transaction = new WalletTransaction();
        transaction.setWalletId(savedWallet.getId());
        transaction.setDealerId(dealerId);
        transaction.setTransactionType(WalletTransactionType.REFUND);
        transaction.setAmount(request.getAmount());
        transaction.setReference(request.getReference());
        transaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);

        return mapToResponse(savedWallet);
    }

    @Override
    public FarmerWalletResponse getFarmerWallet(Long farmerId) {

        ensureFarmerOwnerOrAdmin(farmerId);

        FarmerWallet wallet = farmerWalletRepository.findByFarmerId(farmerId)
                .orElseThrow(() -> new IllegalArgumentException("Farmer wallet not found"));

        return mapFarmerWalletToResponse(wallet);
    }
    
    @Override
    public FarmerWalletResponse releaseEscrow(Long dealerId, EscrowReleaseRequest request) {

        DealerWallet dealerWallet = walletRepository.findByDealerId(dealerId)
                .orElseThrow(() -> new IllegalArgumentException("Dealer wallet not found"));

        double escrowBalance = dealerWallet.getEscrowBalance() == null ? 0.0 : dealerWallet.getEscrowBalance();
        if (escrowBalance < request.getAmount()) {
            throw new IllegalArgumentException("Insufficient escrow balance");
        }

        UserResponse farmer = userClient.getUserById(request.getFarmerId());

        if (!"FARMER".equalsIgnoreCase(farmer.getRole())) {
            throw new IllegalArgumentException("User is not a farmer");
        }

        if (!farmer.isActive()) {
            throw new IllegalArgumentException("Farmer account is inactive");
        }

        dealerWallet.setEscrowBalance(escrowBalance - request.getAmount());

        dealerWallet.setUpdatedAt(LocalDateTime.now());

        walletRepository.save(dealerWallet);

        FarmerWallet farmerWallet = farmerWalletRepository.findByFarmerId(request.getFarmerId())
                .orElseGet(() -> {
                    FarmerWallet wallet = new FarmerWallet();
                    wallet.setFarmerId(request.getFarmerId());
                    wallet.setBalance(0.0);
                    wallet.setCreatedAt(LocalDateTime.now());
                    wallet.setUpdatedAt(LocalDateTime.now());
                    return wallet;
                });

        farmerWallet.setBalance(
                farmerWallet.getBalance() + request.getAmount()
        );

        farmerWallet.setUpdatedAt(LocalDateTime.now());

        FarmerWallet savedFarmerWallet = farmerWalletRepository.save(farmerWallet);

        WalletTransaction transaction = new WalletTransaction();
        transaction.setWalletId(dealerWallet.getId());
        transaction.setDealerId(dealerId);
        transaction.setTransactionType(WalletTransactionType.ESCROW_RELEASED);
        transaction.setAmount(request.getAmount());
        transaction.setReference(request.getReference());
        transaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);

        return mapFarmerWalletToResponse(savedFarmerWallet);
    }
    private void ensureDealerOwnerOrAdmin(Long dealerId) {
        if (SecurityUtils.hasRole("ADMIN")) {
            return;
        }

        UserResponse dealer = userClient.getUserById(dealerId);
        if (SecurityUtils.getCurrentAuthUserId() == null ||
                !SecurityUtils.getCurrentAuthUserId().equals(dealer.getAuthUserId())) {
            throw new AccessDeniedException("You can access only your own dealer wallet");
        }
    }

    private void ensureFarmerOwnerOrAdmin(Long farmerId) {
        if (SecurityUtils.hasRole("ADMIN")) {
            return;
        }

        UserResponse farmer = userClient.getUserById(farmerId);
        if (SecurityUtils.getCurrentAuthUserId() == null ||
                !SecurityUtils.getCurrentAuthUserId().equals(farmer.getAuthUserId())) {
            throw new AccessDeniedException("You can access only your own farmer wallet");
        }
    }

    private WalletTransactionResponse mapTransactionToResponse(
            WalletTransaction transaction) {

        WalletTransactionResponse response =
                new WalletTransactionResponse();

        response.setId(transaction.getId());
        response.setWalletId(transaction.getWalletId());
        response.setDealerId(transaction.getDealerId());
        response.setTransactionType(transaction.getTransactionType());
        response.setAmount(transaction.getAmount());
        response.setReference(transaction.getReference());
        response.setCreatedAt(transaction.getCreatedAt());
        

        return response;
    }
    private FarmerWalletResponse mapFarmerWalletToResponse(FarmerWallet wallet) {

        FarmerWalletResponse response = new FarmerWalletResponse();
        response.setId(wallet.getId());
        response.setFarmerId(wallet.getFarmerId());
        response.setBalance(wallet.getBalance());

        return response;
    }
}
