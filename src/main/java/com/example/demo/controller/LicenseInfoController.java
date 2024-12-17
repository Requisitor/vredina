package com.example.demo.controller;

import com.example.demo.demo.Device;
import com.example.demo.demo.License;
import com.example.demo.demo.Ticket;
import com.example.demo.service.impl.DeviceServiceImpl;
import com.example.demo.service.impl.LicenseServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/licenses")
public class LicenseInfoController {

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

        // Извлекаем deviceId из тела запроса
        Long deviceId = request.containsKey("deviceId") ? Long.valueOf(request.get("deviceId").toString()) : null;

        if (deviceId == null) {
            return ResponseEntity.badRequest().body("Параметр 'deviceId' обязателен.");
        }

        // Пытаемся найти устройство по ID
        Device device = deviceService.findDeviceById(deviceId);

        if (device == null) {
            // Если устройство не найдено, возвращаем ошибку
            return ResponseEntity.status(404).body("Устройство с ID " + deviceId + " не найдено.");
        }

        // Проверяем активные лицензии для устройства
        List<License> activeLicenses = licenseService.getAllLicenses(); // Можно уточнить метод для фильтрации лицензий по устройству

        if (activeLicenses.isEmpty()) {
            return ResponseEntity.status(404).body("Активные лицензии для устройства не найдены.");
        }

        // Генерируем тикет для лицензии и устройства
        License license = activeLicenses.get(0); // Допустим, берем первую найденную лицензию
        Ticket ticket = licenseService.generateTicket(license, device);

        // Возвращаем тикет пользователю
        return ResponseEntity.ok(ticket);
    }
}