package com.example.demo.controller;

import com.example.demo.demo.License;
import com.example.demo.demo.LicenseDto;
import com.example.demo.service.impl.LicenseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/licenses")
public class LicenseController {

    @Autowired
    private LicenseServiceImpl licenseService;

    @GetMapping
    public ResponseEntity<List<License>> getAllLicenses() {
        List<License> licenses = licenseService.getAllLicenses();
        return new ResponseEntity<>(licenses, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<License> createLicense(@RequestBody LicenseDto licenseDto) { // <- Добавлена аннотация @RequestBody
        License license = licenseService.createLicense(
                licenseDto.getProductId(),
                licenseDto.getOwnerId(),
                licenseDto.getLicenseTypeId(),
                licenseDto.getParameters()
        );
        return new ResponseEntity<>(license, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<License> getLicenseById(@PathVariable Long id) {
        License license = licenseService.getLicenseById(id);
        if (license != null) {
            return new ResponseEntity<>(license, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<License> updateLicense(@PathVariable Long id, @RequestBody Map<String, Object> parameters) {
        License license = licenseService.updateLicense(id, parameters);
        if (license != null) {
            return new ResponseEntity<>(license, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLicense(@PathVariable Long id) {
        licenseService.deleteLicense(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}