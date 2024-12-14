package com.example.demo.controller;


import com.example.demo.demo.LicenseType;
import com.example.demo.service.impl.LicenseTypeServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/license-types")
public class LicenseTypeController {

    private final LicenseTypeServiceImpl licenseTypeService;

    public LicenseTypeController(LicenseTypeServiceImpl licenseTypeService) {
        this.licenseTypeService = licenseTypeService;
    }

    @PostMapping
    public ResponseEntity<LicenseType> createLicenseType(@RequestBody LicenseType licenseType) {
        LicenseType createdType = licenseTypeService.save(licenseType);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdType);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LicenseType> getLicenseTypeById(@PathVariable Long id) {
        LicenseType licenseType = licenseTypeService.findById(id);
        if (licenseType != null) {
            return ResponseEntity.ok(licenseType);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public List<LicenseType> getAllLicenseTypes() {
        return licenseTypeService.findAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<LicenseType> updateLicenseType(@PathVariable Long id, @RequestBody LicenseType licenseType) {
        licenseType.setId(id); // Important: set the ID for update
        try {
            LicenseType updatedType = licenseTypeService.save(licenseType);
            return ResponseEntity.ok(updatedType);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLicenseType(@PathVariable Long id) {
        try {
            licenseTypeService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<LicenseType> getLicenseTypeByName(@PathVariable String name) {
        return licenseTypeService.findByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/duration")
    public ResponseEntity<LicenseType> updateDefaultDuration(
            @PathVariable Long id,
            @RequestParam Integer newDuration) {
        try {
            licenseTypeService.updateDefaultDuration(id, newDuration);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
