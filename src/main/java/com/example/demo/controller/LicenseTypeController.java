package com.example.demo.controller;

import com.example.demo.demo.LicenseType;
import com.example.demo.service.impl.LicenseTypeServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ADMIN')") // Только ADMIN может создавать
    public ResponseEntity<LicenseType> createLicenseType(@RequestBody LicenseType licenseType) {
        LicenseType createdType = licenseTypeService.save(licenseType);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdType);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')") // ADMIN и USER могут читать
    public ResponseEntity<LicenseType> getLicenseTypeById(@PathVariable Long id) {
        LicenseType licenseType = licenseTypeService.findById(id);
        if (licenseType != null) {
            return ResponseEntity.ok(licenseType);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')") // ADMIN и USER могут читать все записи
    public List<LicenseType> getAllLicenseTypes() {
        return licenseTypeService.findAll();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Только ADMIN может обновлять
    public ResponseEntity<LicenseType> updateLicenseType(@PathVariable Long id, @RequestBody LicenseType licenseType) {
        licenseType.setId(id);
        try {
            LicenseType updatedType = licenseTypeService.save(licenseType);
            return ResponseEntity.ok(updatedType);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Только ADMIN может удалять
    public ResponseEntity<Void> deleteLicenseType(@PathVariable Long id) {
        try {
            licenseTypeService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')") // ADMIN и USER могут искать по имени
    public ResponseEntity<LicenseType> getLicenseTypeByName(@PathVariable String name) {
        return licenseTypeService.findByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/duration")
    @PreAuthorize("hasRole('ADMIN')") // Только ADMIN может изменять продолжительность
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