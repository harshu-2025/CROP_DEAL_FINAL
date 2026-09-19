package com.cropdeal.crop.service;

import java.util.List;

import com.cropdeal.crop.dto.CropRequest;
import com.cropdeal.crop.dto.CropResponse;
import com.cropdeal.crop.entity.CropStatus;

public interface CropService {

    CropResponse createCrop(CropRequest request);

    CropResponse getCropById(Long id);

    List<CropResponse> getAllCrops();

    List<CropResponse> getCropsByCategory(String category);

    List<CropResponse> searchCropsByName(String cropName);

    List<CropResponse> filterCrops(String name, String grade, String location);

    List<CropResponse> getCropsByLocation(String location);

    List<CropResponse> getCropsByStatus(CropStatus status);

    List<CropResponse> getCropsByFarmerId(Long farmerId);

    CropResponse updateCrop(Long id, CropRequest request);

    CropResponse updateCropStatus(Long id, CropStatus status);

    void deleteCrop(Long id);
    
    CropResponse reduceQuantity(Long cropId, Double quantity);
    CropResponse restoreQuantity(Long cropId, Double quantity);
}