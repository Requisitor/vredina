package com.example.demo.controller;

import com.example.demo.demo.License;
import com.example.demo.demo.LicenseDto;
import com.example.demo.service.impl.LicenseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/licenses")
public class LicenseController {

    private static final Logger logger = LoggerFactory.getLogger(LicenseController.class);

    @Autowired
    private LicenseServiceImpl licenseService;

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<License>> getAllLicenses() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("GET /licenses - User: {}", authentication.getName());

        List<License> licenses = licenseService.getAllLicenses();
        return new ResponseEntity<>(licenses, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<License> createLicense(@RequestBody LicenseDto licenseDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("POST /licenses - User: {}, Data: {}", authentication.getName(), licenseDto);

        License license = licenseService.createLicense(
                licenseDto.getProductId(),
                licenseDto.getOwnerId(),
                licenseDto.getLicenseTypeId(),
                licenseDto.getParameters()
        );
        return new ResponseEntity<>(license, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<License> getLicenseById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("GET /licenses/{} - User: {}", id, authentication.getName());

        License license = licenseService.getLicenseById(id);
        if (license != null) {
            return new ResponseEntity<>(license, HttpStatus.OK);
        } else {
            logger.warn("GET /licenses/{} - License not found", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<License> updateLicense(@PathVariable Long id, @RequestBody Map<String, Object> parameters) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("PUT /licenses/{} - User: {}, Data: {}", id, authentication.getName(), parameters);

        License license = licenseService.updateLicense(id, parameters);
        if (license != null) {
            return new ResponseEntity<>(license, HttpStatus.OK);
        } else {
            logger.warn("PUT /licenses/{} - License not found", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteLicense(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("DELETE /licenses/{} - User: {}", id, authentication.getName());

        licenseService.deleteLicense(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/extend")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<License> extendLicense(@PathVariable Long id, @RequestParam int extensionPeriodInDays) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("PUT /licenses/{}/extend - User: {}, Extension Days: {}", id, authentication.getName(), extensionPeriodInDays);

        try {
            License extendedLicense = licenseService.extendLicense(id, extensionPeriodInDays);
            return new ResponseEntity<>(extendedLicense, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            logger.error("PUT /licenses/{}/extend - Error: {}", id, e.getMessage());
            return new ResponseEntity<>(e.getStatusCode());
        }
    }
}