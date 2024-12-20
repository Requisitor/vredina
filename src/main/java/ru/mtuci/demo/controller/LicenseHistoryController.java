package ru.mtuci.demo.controller;

import ru.mtuci.demo.demo.ApplicationUser;
import ru.mtuci.demo.demo.License;
import ru.mtuci.demo.demo.LicenseHistory;
import ru.mtuci.demo.service.impl.LicenseServiceImpl;
import ru.mtuci.demo.service.impl.LicenseHistoryServiceImpl;
import ru.mtuci.demo.service.impl.UserDetailsServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

//TODO: 1. Вносить изменения в записи аудита извне нельзя, можно только читать

@RestController
@RequestMapping("/api/license-histories")
public class LicenseHistoryController {

    private final LicenseHistoryServiceImpl licenseHistoryService;
    private final UserDetailsServiceImpl userDetailsService;
    private final LicenseServiceImpl licenseService;

    public LicenseHistoryController(LicenseHistoryServiceImpl licenseHistoryService, UserDetailsServiceImpl userDetailsService, LicenseServiceImpl licenseService) {
        this.licenseHistoryService = licenseHistoryService;
        this.userDetailsService = userDetailsService;
        this.licenseService = licenseService;
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

    @GetMapping("/license/{licenseId}")
    public List<LicenseHistory> getHistoryByLicense(@PathVariable Long licenseId) {
        License license = licenseService.getLicenseById(licenseId)
                .orElseThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "License not found"));
        return licenseHistoryService.getHistoryByLicense(license);
    }

    @GetMapping("/user/{userId}")
    public List<LicenseHistory> getHistoryByUser(@PathVariable Long userId) {
        ApplicationUser user = (ApplicationUser) userDetailsService.loadUserById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return licenseHistoryService.getHistoryByUser(user);
    }

    @GetMapping("/status/{status}")
    public List<LicenseHistory> getHistoryByStatus(@PathVariable String status) {
        return licenseHistoryService.getHistoryByStatus(status);
    }
}