package com.example.demo.controller;

import com.example.demo.demo.DeviceLicense;
import com.example.demo.service.impl.DeviceLicenseServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/device-licenses")
public class DeviceLicenseController {

    private final DeviceLicenseServiceImpl deviceLicenseService;

    public DeviceLicenseController(DeviceLicenseServiceImpl deviceLicenseService) {
        this.deviceLicenseService = deviceLicenseService;
    }

    @GetMapping
    public List<DeviceLicense> getAllDeviceLicenses() {
        return deviceLicenseService.getAllDeviceLicenses();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceLicense> getDeviceLicenseById(@PathVariable Long id) {
        DeviceLicense deviceLicense = deviceLicenseService.getDeviceLicenseById(id);
        return ResponseEntity.ok(deviceLicense);
    }

    @PostMapping
    public ResponseEntity<DeviceLicense> createDeviceLicense(@RequestBody DeviceLicense deviceLicense) {
        DeviceLicense createdDeviceLicense = deviceLicenseService.createDeviceLicense(deviceLicense);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDeviceLicense);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceLicense> updateDeviceLicense(@PathVariable Long id, @RequestBody DeviceLicense deviceLicense) {
        DeviceLicense updatedDeviceLicense = deviceLicenseService.updateDeviceLicense(id, deviceLicense);
        return ResponseEntity.ok(updatedDeviceLicense);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeviceLicense(@PathVariable Long id) {
        deviceLicenseService.deleteDeviceLicense(id);
        return ResponseEntity.noContent().build();
    }
}