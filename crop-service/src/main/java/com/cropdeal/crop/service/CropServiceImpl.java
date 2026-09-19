package com.cropdeal.crop.service;

import java.time.LocalDateTime;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

import com.cropdeal.crop.client.UserClient;
import com.cropdeal.crop.client.UserServiceClient;
import com.cropdeal.crop.dto.CropRequest;
import com.cropdeal.crop.dto.CropResponse;
import com.cropdeal.crop.dto.UserResponse;
import com.cropdeal.crop.entity.Crop;
import com.cropdeal.crop.entity.CropStatus;
import com.cropdeal.crop.exception.CropNotFoundException;
import com.cropdeal.crop.messaging.CropEventProducer;
import com.cropdeal.crop.messaging.CropPublishedEvent;
import com.cropdeal.crop.repository.CropRepository;
import com.cropdeal.crop.security.SecurityUtils;

@Service
public class CropServiceImpl implements CropService {
	
	private final CropRepository cropRepository;
	private final UserServiceClient userServiceClient;
	private final CropEventProducer cropEventProducer;
	
	public CropServiceImpl(CropRepository cropRepository,
            UserServiceClient userServiceClient,
            CropEventProducer cropEventProducer) {

this.cropRepository = cropRepository;
this.userServiceClient = userServiceClient;
this.cropEventProducer = cropEventProducer;
}
	
	@Override
	public CropResponse createCrop(CropRequest request) {
		
		UserResponse user = userServiceClient.getFarmer(request.getFarmerId());

        if (!SecurityUtils.getCurrentAuthUserId().equals(user.getAuthUserId())) {
            throw new AccessDeniedException("You can publish crops only for your own farmer profile");
        }

		if (!"FARMER".equalsIgnoreCase(user.getRole())) {
		    throw new IllegalArgumentException("Only farmers can publish crops");
		}

		if (!user.isActive()) {
		    throw new IllegalArgumentException("Farmer account is not active");
		}
		
		Crop crop = new Crop();
		crop.setFarmerId(request.getFarmerId());
        crop.setCategory(request.getCategory());
        crop.setCropName(request.getCropName());
        crop.setGrade(request.getGrade().toUpperCase());
        crop.setQuantity(request.getQuantity());
        crop.setExpectedPrice(request.getExpectedPrice());
        crop.setLocation(request.getLocation());
        crop.setStatus(CropStatus.AVAILABLE);
        crop.setPublishedAt(LocalDateTime.now());
        crop.setUpdatedAt(LocalDateTime.now());
        
        Crop savedCrop = cropRepository.save(crop);

        CropPublishedEvent event = new CropPublishedEvent();

        event.setCropId(savedCrop.getId());
        event.setFarmerId(savedCrop.getFarmerId());
        event.setCropName(savedCrop.getCropName());
        event.setCategory(savedCrop.getCategory());
        event.setGrade(savedCrop.getGrade());
        event.setLocation(savedCrop.getLocation());
        event.setQuantity(savedCrop.getQuantity());
        event.setExpectedPrice(savedCrop.getExpectedPrice());

        cropEventProducer.publishCropCreated(event);

        return mapToResponse(savedCrop);
		
	}
	@Override
	public CropResponse getCropById(Long id) {
	 Crop crop = cropRepository.findById(id)
             .orElseThrow(() ->new CropNotFoundException("Crop not found with id: " + id));
	 return mapToResponse(crop);
	 }
	@Override
	public List<CropResponse> getAllCrops(){
		return cropRepository.findAll()
				.stream()
				.map(this::mapToResponse)
				.toList();
	}
	@Override
    public List<CropResponse> getCropsByCategory(String category) {

        return cropRepository.findByCategoryIgnoreCase(category)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<CropResponse> searchCropsByName(String cropName) {

        return cropRepository
                .findByCropNameContainingIgnoreCase(cropName)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<CropResponse> filterCrops(String name, String grade, String location) {

        return cropRepository.findAll()
                .stream()
                .filter(crop -> name == null || name.isBlank() ||
                        crop.getCropName().toLowerCase().contains(name.toLowerCase()))
                .filter(crop -> grade == null || grade.isBlank() ||
                        (crop.getGrade() != null && crop.getGrade().equalsIgnoreCase(grade)))
                .filter(crop -> location == null || location.isBlank() ||
                        crop.getLocation().equalsIgnoreCase(location))
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<CropResponse> getCropsByLocation(String location) {

        return cropRepository.findByLocationIgnoreCase(location)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<CropResponse> getCropsByStatus(CropStatus status) {

        return cropRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<CropResponse> getCropsByFarmerId(Long farmerId) {

        return cropRepository.findByFarmerId(farmerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    @Override
    public CropResponse updateCrop(Long id, CropRequest request) {
    	
    	Crop crop = cropRepository.findById(id).orElseThrow(() -> new CropNotFoundException("Crop not found with id:" + id));
        UserResponse user = userServiceClient.getFarmer(crop.getFarmerId());

        if (!SecurityUtils.getCurrentAuthUserId().equals(user.getAuthUserId())) {
            throw new AccessDeniedException("You can update only your own crops");
        }

    	if (!"FARMER".equalsIgnoreCase(user.getRole())) {
    	    throw new IllegalArgumentException("Only farmers can update crops");
    	}

    	if (!user.isActive()) {
    	    throw new IllegalArgumentException("Farmer account is not active");
    	}

        if (request.getFarmerId() != null && !crop.getFarmerId().equals(request.getFarmerId())) {
            throw new IllegalArgumentException("Crop owner cannot be changed");
        }
    	crop.setCategory(request.getCategory());
        crop.setCropName(request.getCropName());
        crop.setGrade(request.getGrade().toUpperCase());
        crop.setQuantity(request.getQuantity());
        crop.setExpectedPrice(request.getExpectedPrice());
        crop.setLocation(request.getLocation());
        crop.setUpdatedAt(LocalDateTime.now());
        
        Crop savedCrop = cropRepository.save(crop);
        return mapToResponse(savedCrop);
    }
    @Override
    public CropResponse updateCropStatus(Long id, CropStatus status) {

        Crop crop = cropRepository.findById(id)
                .orElseThrow(() -> new CropNotFoundException("Crop not found with id: " + id));

        UserResponse user = userServiceClient.getFarmer(crop.getFarmerId());

        if (!SecurityUtils.getCurrentAuthUserId().equals(user.getAuthUserId())) {
            throw new AccessDeniedException("You can update status only for your own crops");
        }

        if (status != CropStatus.AVAILABLE && status != CropStatus.INACTIVE) {
            throw new IllegalArgumentException("Farmer can set crop status only to AVAILABLE or INACTIVE");
        }

        if (crop.getStatus() == CropStatus.SOLD || crop.getStatus() == CropStatus.EXPIRED) {
            throw new IllegalArgumentException("Sold or expired crops cannot be made available again");
        }

        crop.setStatus(status);
        crop.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(cropRepository.save(crop));
    }

    @Override
    public void deleteCrop(Long id) {
        Crop crop = cropRepository.findById(id)
                .orElseThrow(() -> new CropNotFoundException("Crop not found with id: " + id));

        UserResponse user = userServiceClient.getFarmer(crop.getFarmerId());
        
        if (!SecurityUtils.getCurrentAuthUserId().equals(user.getAuthUserId())) {
            throw new AccessDeniedException("You can delete only your own crops");
        }

        cropRepository.delete(crop);
    }
    @Override
    public CropResponse reduceQuantity(Long cropId, Double quantity) {

        Crop crop = cropRepository.findById(cropId)
                .orElseThrow(() -> new CropNotFoundException("Crop not found"));

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        if (crop.getQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient crop quantity");
        }

        double remainingQuantity = crop.getQuantity() - quantity;

        crop.setQuantity(remainingQuantity);
        crop.setUpdatedAt(LocalDateTime.now());

        if (remainingQuantity == 0) {
            crop.setStatus(CropStatus.SOLD);
        }

        Crop savedCrop = cropRepository.save(crop);

        return mapToResponse(savedCrop);
    }
	private CropResponse mapToResponse(Crop crop) {

        CropResponse response = new CropResponse();

        response.setId(crop.getId());
        response.setFarmerId(crop.getFarmerId());
        response.setCategory(crop.getCategory());
        response.setCropName(crop.getCropName());
        response.setGrade(crop.getGrade());
        response.setQuantity(crop.getQuantity());
        response.setExpectedPrice(crop.getExpectedPrice());
        response.setLocation(crop.getLocation());
        response.setStatus(crop.getStatus());
        response.setPublishedAt(crop.getPublishedAt());
        response.setUpdatedAt(crop.getUpdatedAt());

        return response;
    }
	
	 
	
	

}
