package com.example.demo.controller;


import com.example.demo.demo.ApplicationRole;
import com.example.demo.demo.ApplicationUser;
import com.example.demo.demo.License;
import com.example.demo.demo.LicenseHistory;
import com.example.demo.service.impl.LicenseHistoryServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/license-histories")
public class LicenseHistoryController {

    private final LicenseHistoryServiceImpl licenseHistoryService;
    private LicenseHistoryServiceImpl applicationUserService;

    public LicenseHistoryController(LicenseHistoryServiceImpl licenseHistoryService) {
        this.licenseHistoryService = licenseHistoryService;
    }

    @PostMapping
    public ResponseEntity<LicenseHistory> createLicenseHistory(@RequestBody LicenseHistory licenseHistory) {
        LicenseHistory createdHistory = licenseHistoryService.save(licenseHistory);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdHistory);
    }

    @GetMapping
    public List<LicenseHistory> getAllLicenseHistories() {
        return licenseHistoryService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LicenseHistory> getLicenseHistoryById(@PathVariable Long id) {
        return licenseHistoryService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLicenseHistory(@PathVariable Long id) {
        try {
            LicenseHistory licenseHistoryToDelete = new LicenseHistory();
            licenseHistoryToDelete.setId(id);
            licenseHistoryService.delete(licenseHistoryToDelete);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/license/{licenseId}")
    public List<LicenseHistory> getHistoryByLicense(@PathVariable Long licenseId) {
        // Предполагается, что у вас есть сервис для получения License по ID
        License license = new License(); // Замените на получение реального объекта License
        license.setId(licenseId);
        return licenseHistoryService.getHistoryByLicense(license);
    }

    // Дополнительные методы контроллера (примеры):

    @GetMapping("/user/{userId}")
    public List<LicenseHistory> getHistoryByUser(@PathVariable Long userId) {
        // Аналогично getHistoryByLicense, предполагается наличие сервиса для получения ApplicationUser
        Optional<LicenseHistory> user = applicationUserService.getById(userId);
        if (user == null) {
            return List.of(); // Или обработайте случай, когда пользователь не найден
        }
        return licenseHistoryService.getHistoryByUser(user);
    }
    @GetMapping("/status/{status}")
    public List<LicenseHistory> getHistoryByStatus(@PathVariable String status) {
        return licenseHistoryService.getHistoryByStatus(status);
    }
}