package com.cropdeal.crop.controller;


import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import com.cropdeal.crop.dto.CropRequest;
import com.cropdeal.crop.dto.CropResponse;
import com.cropdeal.crop.entity.CropStatus;
import com.cropdeal.crop.service.CropService;

import java.util.List;

@RestController
@RequestMapping("/crops")
public class CropController {

    @Value("${internal.api.key}")
    private String internalApiKey;

    private final CropService cropService;

    public CropController(CropService cropService) {
        this.cropService = cropService;
    }

    @PostMapping
    public ResponseEntity<CropResponse> createCrop(
            @Valid @RequestBody CropRequest request) {

        CropResponse response = cropService.createCrop(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CropResponse> getCropById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                cropService.getCropById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<CropResponse>> getAllCrops() {

        return ResponseEntity.ok(
                cropService.getAllCrops()
        );
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<CropResponse>> getCropsByCategory(
            @PathVariable String category) {

        return ResponseEntity.ok(
                cropService.getCropsByCategory(category)
        );
    }

    @GetMapping("/search")
    public ResponseEntity<List<CropResponse>> searchCropsByName(
            @RequestParam String name) {

        return ResponseEntity.ok(
                cropService.searchCropsByName(name)
        );
    }

    @GetMapping("/filter")
    public ResponseEntity<List<CropResponse>> filterCrops(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String location) {

        return ResponseEntity.ok(cropService.filterCrops(name, grade, location));
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<List<CropResponse>> getCropsByLocation(
            @PathVariable String location) {

        return ResponseEntity.ok(
                cropService.getCropsByLocation(location)
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<CropResponse>> getCropsByStatus(
            @PathVariable CropStatus status) {

        return ResponseEntity.ok(
                cropService.getCropsByStatus(status)
        );
    }

    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<List<CropResponse>> getCropsByFarmerId(
            @PathVariable Long farmerId) {

        return ResponseEntity.ok(
                cropService.getCropsByFarmerId(farmerId)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<CropResponse> updateCrop(
            @PathVariable Long id,
            @Valid @RequestBody CropRequest request) {

        return ResponseEntity.ok(
                cropService.updateCrop(id, request)
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CropResponse> updateCropStatus(
            @PathVariable Long id,
            @RequestParam CropStatus status) {

        return ResponseEntity.ok(cropService.updateCropStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCrop(
            @PathVariable Long id) {

        cropService.deleteCrop(id);

        return ResponseEntity.noContent().build();
    }
    @RequestMapping(value = "/{id}/reduce-quantity", method = {RequestMethod.PATCH, RequestMethod.PUT})
    public ResponseEntity<CropResponse> reduceQuantity(
            @PathVariable Long id,
            @RequestParam Double quantity,
            @RequestHeader(value = "X-Internal-Key", required = false) String internalKey) {

        if (!internalApiKey.equals(internalKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(
                cropService.reduceQuantity(id, quantity)
        );
    }
}