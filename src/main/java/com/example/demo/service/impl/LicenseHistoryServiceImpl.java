package com.example.demo.service.impl;

import com.example.demo.demo.ApplicationUser;
import com.example.demo.demo.License;
import com.example.demo.demo.LicenseHistory;
import com.example.demo.repository.LicenseHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LicenseHistoryServiceImpl {

    private final LicenseHistoryRepository licenseHistoryRepository;

    public LicenseHistoryServiceImpl(LicenseHistoryRepository licenseHistoryRepository) {
        this.licenseHistoryRepository = licenseHistoryRepository;
    }

    @Transactional
    public LicenseHistory save(LicenseHistory licenseHistory) {
        return licenseHistoryRepository.save(licenseHistory);
    }

    @Transactional(readOnly = true)
    public List<LicenseHistory> getAll() {
        return licenseHistoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<LicenseHistory> getById(Long id) {
        return licenseHistoryRepository.findById(id);
    }

    @Transactional
    public void delete(LicenseHistory licenseHistory) {
        licenseHistoryRepository.delete(licenseHistory);
    }

    @Transactional(readOnly = true)
    public List<LicenseHistory> getHistoryByLicense(License license) {
        return licenseHistoryRepository.findByLicense(license);
    }

    @Transactional(readOnly = true)
    public List<LicenseHistory> getHistoryByUser(ApplicationUser user) {
        return licenseHistoryRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public List<LicenseHistory> getHistoryByStatus(String status) {
        return licenseHistoryRepository.findByStatus(status);
    }

    @Transactional
    public void recordLicenseChange(License license, String status, String description) {
        LicenseHistory historyEntry = new LicenseHistory();
        historyEntry.setLicense(license);
        historyEntry.setStatus(status);
        historyEntry.setDescription(description);
        historyEntry.setChangeDate(LocalDateTime.now());
        save(historyEntry);
    }
}