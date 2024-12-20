package ru.mtuci.demo.controller;

import ru.mtuci.demo.demo.Device;
import ru.mtuci.demo.demo.DeviceLicense;
import ru.mtuci.demo.demo.License;
import ru.mtuci.demo.service.impl.DeviceLicenseServiceImpl;
import ru.mtuci.demo.service.impl.LicenseServiceImpl;
import ru.mtuci.demo.service.impl.DeviceServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

//TODO: 1. Это служебная таблица. Её нельзя модифицировать извне

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
            Optional<License> optionalLicense = Optional.ofNullable(licenseService.findLicenseById(licenseId));
            return optionalLicense.orElse(null); // Возвращаем null, если лицензия не найдена
        } catch (NullPointerException | NumberFormatException e) {
            return null;
        }
    }

    private Device extractDeviceFromRequest(Map<String, Object> request) {
        try {
            Long deviceId = Long.parseLong(request.get("deviceId").toString());
            Optional<Device> optionalDevice = deviceService.findDeviceById(deviceId);
            return optionalDevice.orElse(null); // Возвращаем null, если устройство не найдено
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

    //Создание новых записей запрещено.
    @PostMapping
    public ResponseEntity<String> createDeviceLicense(@RequestBody Map<String, Object> request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Modification of this table is not allowed.");
    }

    // Обновление существующих записей запрещено.
    @PutMapping("/{id}")
    public ResponseEntity<String> updateDeviceLicense(@PathVariable Long id, @RequestBody DeviceLicense deviceLicense) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Modification of this table is not allowed.");
    }

    //Удаление записей запрещено.
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDeviceLicense(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Modification of this table is not allowed.");
    }
}