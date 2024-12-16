package com.example.demo.controller;

import com.example.demo.demo.Device;
import com.example.demo.demo.License;
import com.example.demo.demo.ApplicationUser;
import com.example.demo.demo.Ticket;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.impl.DeviceServiceImpl;
import com.example.demo.service.impl.LicenseHistoryServiceImpl;
import com.example.demo.service.impl.LicenseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/activation")
public class ActivationController {

    private final LicenseServiceImpl licenseService;
    private final DeviceServiceImpl deviceService;
    private final LicenseHistoryServiceImpl licenseHistoryService;

    @Autowired
    public ActivationController(LicenseServiceImpl licenseService, DeviceServiceImpl deviceService, com.example.demo.service.impl.LicenseHistoryServiceImpl licenseHistoryService) {
        this.licenseService = licenseService;
        this.deviceService = deviceService;
        this.licenseHistoryService = licenseHistoryService;
    }

    @Autowired
    private UserRepository userRepository; // Внедрите UserRepository

    @PostMapping
    public ResponseEntity<?> activateLicense(@RequestBody Map<String, String> request) {
        String activationCode = request.get("activationCode");
        Device device = getDeviceFromRequest(request);
        ApplicationUser user = getUserFromRequest(request);

        Optional<License> optionalLicense = licenseService.findLicenseByCode(activationCode);

        if (optionalLicense.isPresent()) {
            License license = optionalLicense.get();

            if (licenseService.validateActivation(license, device, user)) {
                deviceService.createDeviceLicense(license, device);
                licenseService.updateLicense(license);
                // Изменен вызов метода
                licenseHistoryService.recordLicenseChange(license, "Activated", "success");

                Ticket ticket = licenseService.generateTicket(license, device);

                Map<String, Object> response = new HashMap<>();
                response.put("status", "success");
                response.put("ticket", ticket);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Активация невозможна"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", "error", "message", "Лицензия не найдена"));
        }
    }

    //Вспомогательный метод для получения информации об устройстве из запроса
    private Device getDeviceFromRequest(Map<String, String> request) {
        Device device = new Device();
        device.setName(request.get("deviceName"));
        device.setMacAddress(request.get("macAddress"));

        return device;
    }


    private ApplicationUser getUserFromRequest(Map<String, String> request) {
        String login = request.get("login");
        return userRepository.findByLogin(login).orElse(null);
    }
}