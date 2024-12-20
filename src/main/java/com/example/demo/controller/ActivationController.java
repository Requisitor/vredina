package com.example.demo.controller;

import com.example.demo.demo.*;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/activation")
public class ActivationController {

    private static final Logger logger = LoggerFactory.getLogger(ActivationController.class);

    private final LicenseServiceImpl licenseService;
    private final DeviceServiceImpl deviceService;
    private final LicenseHistoryServiceImpl licenseHistoryService;
    private final UserRepository userRepository;
    private final DeviceLicenseServiceImpl deviceLicenseService;
    private final AuthenticationServiceImpl authenticationService;


    @Autowired
    public ActivationController(LicenseServiceImpl licenseService,
                                DeviceServiceImpl deviceService,
                                LicenseHistoryServiceImpl licenseHistoryService,
                                UserRepository userRepository,
                                DeviceLicenseServiceImpl deviceLicenseService,
                                AuthenticationServiceImpl authenticationService) {
        this.licenseService = licenseService;
        this.deviceService = deviceService;
        this.licenseHistoryService = licenseHistoryService;
        this.userRepository = userRepository;
        this.deviceLicenseService = deviceLicenseService;
        this.authenticationService = authenticationService;
    }

    @PostMapping
    public ResponseEntity<?> activateLicense(@RequestBody ActivationRequestDto request) {
        logger.info("Received activation request: {}", request);

        String activationCode = request.getActivationCode();
        Device device = getDeviceFromRequest(request);
        ApplicationUser user = getUserFromRequest(request);

        logger.info("Attempting to find license with code: {}", activationCode);
        Optional<License> optionalLicense = licenseService.findLicenseByCode(activationCode);

        if (optionalLicense.isPresent()) {
            License license = optionalLicense.get();
            logger.info("License found: {}", license);

            // Проверяем, активирована ли уже лицензия на данном устройстве
            logger.info("Checking if license is already activated on device with mac address: {}", device.getMacAddress());
            Optional<Device> existingDevice = deviceService.findByMacAddress(device.getMacAddress());

            if (existingDevice.isPresent() && deviceLicenseService.isLicenseActivatedOnDevice(license, existingDevice.get())) {
                logger.warn("License already activated on this device. Device: {}", existingDevice.get());
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Лицензия уже активирована на этом устройстве"));
            }

            // Проверяем, имеет ли текущий пользователь право активировать лицензию для другого пользователя
            ApplicationUser currentUser = authenticationService.getCurrentUser();
            if (!currentUser.getId().equals(user.getId())) {
                logger.warn("User with email {} does not have permission to activate license for user with email {}", currentUser.getEmail(), user.getEmail());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("status", "error", "message", "You do not have permission to activate this license"));
            }

            if (licenseService.validateActivation(license, device, user)) {
                logger.info("License activation is valid.");
                // Если устройство существует, используем его, иначе сохраняем новое
                Device savedDevice = existingDevice.orElseGet(() -> {
                    logger.info("Device not found, saving new device: {}", device);
                    return deviceService.saveDevice(device);
                });

                // Устанавливаем даты только при первой активации
                if (license.getFirstActivationDate() == null) {
                    LocalDateTime now = LocalDateTime.now();
                    // Используем duration для расчета даты окончания
                    long durationInDays = license.getDuration();
                    LocalDateTime expirationDateTime = now.plusDays(durationInDays);
                    license.setFirstActivationDate(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()));
                    license.setExpirationDate(Date.from(expirationDateTime.atZone(ZoneId.systemDefault()).toInstant()));
                    logger.info("Setting first activation and expiration dates for license: {}", license);
                }

                deviceLicenseService.createDeviceLicense(license, savedDevice);
                logger.info("Created device license for device: {} and license: {}", savedDevice, license);
                licenseService.updateLicense(license);
                logger.info("Updated license: {}", license);
                licenseHistoryService.recordLicenseChange(license, "Activated", "success", user);
                logger.info("Recorded license activation history for license: {} by user: {}", license, user);

                Ticket ticket = licenseService.generateTicket(license, savedDevice);
                logger.info("Generated ticket for license: {} and device: {}", license, savedDevice);

                Map<String, Object> response = new HashMap<>();
                response.put("status", "success");
                response.put("ticket", ticket);
                logger.info("Activation successful. Response: {}", response);
                return ResponseEntity.ok(response);
            } else {
                logger.warn("License activation is not valid.");
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Активация невозможна"));
            }
        } else {
            logger.warn("License not found with code: {}", activationCode);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", "error", "message", "Лицензия не найдена"));
        }
    }

    private Device getDeviceFromRequest(ActivationRequestDto request) {
        Device device = new Device();
        device.setName(request.getDeviceName());
        device.setMacAddress(request.getMacAddress());
        logger.debug("Device created from request: {}", device);
        return device;
    }

    private ApplicationUser getUserFromRequest(ActivationRequestDto request) {
        String login = request.getLogin();
        logger.debug("Attempting to find user with login: {}", login);
        ApplicationUser user = userRepository.findByLogin(login).orElse(null);
        if (user != null) {
            logger.debug("User found: {}", user);
        } else {
            logger.debug("User not found with login: {}", login);
        }
        return user;
    }

}