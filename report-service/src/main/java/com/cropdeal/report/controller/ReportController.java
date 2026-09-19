package com.cropdeal.report.controller;

import com.cropdeal.report.dto.AdminReportResponse;
import com.cropdeal.report.dto.DealerPurchaseReportResponse;
import com.cropdeal.report.dto.FarmerSalesReportResponse;
import com.cropdeal.report.dto.InvoiceResponse;
import com.cropdeal.report.dto.OrderReportResponse;
import com.cropdeal.report.dto.PaymentReportResponse;
import com.cropdeal.report.dto.ReceiptResponse;
import com.cropdeal.report.service.ReportService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/farmer/{farmerId}/sales")
    public ResponseEntity<FarmerSalesReportResponse> getFarmerSalesReport(@PathVariable Long farmerId) {

        return ResponseEntity.ok(reportService.getFarmerSalesReport(farmerId));
    }
    @GetMapping("/dealer/{dealerId}/purchases")
    public ResponseEntity<DealerPurchaseReportResponse> getDealerPurchaseReport(@PathVariable Long dealerId) {

        return ResponseEntity.ok(reportService.getDealerPurchaseReport(dealerId));
    }
    @GetMapping("/orders")
    public ResponseEntity<OrderReportResponse> getOrderReport() {

        return ResponseEntity.ok(reportService.getOrderReport());
    }
    @GetMapping("/payments")
    public ResponseEntity<PaymentReportResponse> getPaymentReport() {

        return ResponseEntity.ok(reportService.getPaymentReport());
    }
    @GetMapping("/admin")
    public ResponseEntity<AdminReportResponse> getAdminReport() {

        return ResponseEntity.ok(reportService.getAdminReport());
    }
    @GetMapping("/invoice/{orderId}")
    public ResponseEntity<InvoiceResponse> getInvoice(@PathVariable Long orderId) {

        return ResponseEntity.ok(reportService.getInvoice(orderId));
    }
    @GetMapping("/receipt/{paymentId}")
    public ResponseEntity<ReceiptResponse> getReceipt(@PathVariable Long paymentId) {

        return ResponseEntity.ok(reportService.getReceipt(paymentId));
    }
    @GetMapping("/orders/excel")
    public ResponseEntity<byte[]> exportOrderReport() {

        byte[] excel = reportService.exportOrderReport();

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=order-report.xlsx")
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(excel);
    }
}