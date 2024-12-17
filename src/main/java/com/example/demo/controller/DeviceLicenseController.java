package com.example.demo.controller;

import com.example.demo.demo.Device;
import com.example.demo.demo.DeviceLicense;
import com.example.demo.demo.License;
import com.example.demo.service.impl.DeviceLicenseServiceImpl;
import com.example.demo.service.impl.LicenseServiceImpl;
import com.example.demo.service.impl.DeviceServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/device-licenses")
public class DeviceLicenseController {

    private final DeviceLicenseServiceImpl deviceLicenseService;
    private final LicenseServiceImpl licenseService; // Внедрите LicenseService
    private final DeviceServiceImpl deviceService; // Внедрите DeviceService

    public DeviceLicenseController(DeviceLicenseServiceImpl deviceLicenseService, LicenseServiceImpl licenseService, DeviceServiceImpl deviceService) {
        this.deviceLicenseService = deviceLicenseService;
        this.licenseService = licenseService;
        this.deviceService = deviceService;
    }

    private License extractLicenseFromRequest(Map<String, Object> request) {
        try {
            Long licenseId = Long.parseLong(request.get("licenseId").toString());
            return licenseService.findLicenseById(licenseId); // Используйте внедренный licenseService
        } catch (NullPointerException | NumberFormatException e) {
            return null;
        }
    }

    private Device extractDeviceFromRequest(Map<String, Object> request) {
        try {
            Long deviceId = Long.parseLong(request.get("deviceId").toString());
            return deviceService.findDeviceById(deviceId); // Используйте внедренный deviceService
        } catch (NullPointerException | NumberFormatException e) {
            return null;
        }
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
    public ResponseEntity<DeviceLicense> createDeviceLicense(@RequestBody Map<String, Object> request) {
        License license = extractLicenseFromRequest(request);
        Device device = extractDeviceFromRequest(request);

        // Проверка на null важна!
        if (license == null || device == null) {
            return ResponseEntity.badRequest().body(null); // Или более информативное сообщение об ошибке
        }


        DeviceLicense createdDeviceLicense = deviceLicenseService.createDeviceLicense(license, device);
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