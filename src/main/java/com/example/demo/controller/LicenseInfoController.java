package com.example.demo.controller;

import com.example.demo.demo.Device;
import com.example.demo.demo.License;
import com.example.demo.demo.Ticket;
import com.example.demo.service.impl.DeviceServiceImpl;
import com.example.demo.service.impl.LicenseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/licenses")
public class LicenseInfoController {

    private static final Logger logger = LoggerFactory.getLogger(LicenseInfoController.class);

    private final DeviceServiceImpl deviceService;
    private final LicenseServiceImpl licenseService;

    public LicenseInfoController(DeviceServiceImpl deviceService, LicenseServiceImpl licenseService) {
        this.deviceService = deviceService;
        this.licenseService = licenseService;
    }

    @PostMapping("/info")
    public ResponseEntity<?> getLicenseInfo(@RequestBody Map<String, Object> request) {
        // Получаем текущего аутентифицированного пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        logger.info("User {} requested license info.", currentUsername);

        // Извлекаем deviceId из тела запроса
        Long deviceId = request.containsKey("deviceId") ? Long.valueOf(request.get("deviceId").toString()) : null;
        logger.debug("Request body: {}", request);

        if (deviceId == null) {
            logger.warn("User {} sent request without deviceId.", currentUsername);
            return ResponseEntity.badRequest().body("Параметр 'deviceId' обязателен.");
        }
        logger.debug("Received deviceId: {}", deviceId);

        // Пытаемся найти устройство по ID
        Optional<Device> device = deviceService.findDeviceById(deviceId);

        if (device.isEmpty()) {
            logger.warn("Device with ID {} not found.", deviceId);
            return ResponseEntity.status(404).body("Устройство с ID " + deviceId + " не найдено.");
        }
        logger.debug("Device found: {}", device.get());

        // Проверяем активные лицензии для устройства
        List<License> activeLicenses = licenseService.getAllLicenses(); // Можно уточнить метод для фильтрации лицензий по устройству
        logger.debug("Active licenses: {}", activeLicenses);

        if (activeLicenses.isEmpty()) {
            logger.warn("No active licenses found for device ID {}.", deviceId);
            return ResponseEntity.status(404).body("Активные лицензии для устройства не найдены.");
        }

        // Генерируем тикет для лицензии и устройства
        License license = activeLicenses.get(0); // Допустим, берем первую найденную лицензию
        Ticket ticket = licenseService.generateTicket(license, device.orElse(null));
        logger.info("Generated ticket for device ID {}: {}", deviceId, ticket);

        // Возвращаем тикет пользователю
        return ResponseEntity.ok(ticket);
    }
}