package com.cropdeal.crop.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.cropdeal.crop.entity.Crop;
import com.cropdeal.crop.entity.CropStatus;

import java.util.List;

public interface CropRepository extends JpaRepository<Crop, Long> {

    List<Crop> findByCategoryIgnoreCase(String category);

    List<Crop> findByCropNameContainingIgnoreCase(String cropName);

    List<Crop> findByLocationIgnoreCase(String location);

    List<Crop> findByStatus(CropStatus status);

    List<Crop> findByFarmerId(Long farmerId);
}