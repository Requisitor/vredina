package com.example.demo.service.impl;

import com.example.demo.demo.ApplicationUser;
import com.example.demo.demo.License;
import com.example.demo.demo.LicenseHistory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class LicenseHistoryServiceImpl {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public LicenseHistory save(LicenseHistory licenseHistory) {
        if (licenseHistory.getId() == null) {
            entityManager.persist(licenseHistory);
        } else {
            entityManager.merge(licenseHistory);
        }
        return licenseHistory;
    }

    @Transactional(readOnly = true)
    public List<LicenseHistory> getAll() {
        TypedQuery<LicenseHistory> query = entityManager.createQuery("SELECT lh FROM LicenseHistory lh", LicenseHistory.class);
        return query.getResultList();
    }

    @Transactional(readOnly = true)
    public Optional<LicenseHistory> getById(Long id) {
        return Optional.ofNullable(entityManager.find(LicenseHistory.class, id));
    }

    @Transactional
    public void delete(LicenseHistory licenseHistory) {
        Objects.requireNonNull(licenseHistory, "LicenseHistory cannot be null");
        if (entityManager.contains(licenseHistory)) {
            entityManager.remove(licenseHistory);
        } else {
            LicenseHistory attached = entityManager.find(LicenseHistory.class, licenseHistory.getId());
            if (attached != null) {
                entityManager.remove(attached);
            } else {
                throw new IllegalArgumentException("LicenseHistory with id " + licenseHistory.getId() + " not found");
            }
        }
    }

    @Transactional(readOnly = true)
    public List<LicenseHistory> getHistoryByLicense(License license) {
        TypedQuery<LicenseHistory> query = entityManager.createQuery("SELECT lh FROM LicenseHistory lh WHERE lh.license = :license", LicenseHistory.class);
        query.setParameter("license", license);
        return query.getResultList();
    }

    @Transactional(readOnly = true)
    public List<LicenseHistory> getHistoryByUser(ApplicationUser user) {
        TypedQuery<LicenseHistory> query = entityManager.createQuery("SELECT lh FROM LicenseHistory lh WHERE lh.user = :user", LicenseHistory.class);
        query.setParameter("user", user);
        return query.getResultList();
    }

    @Transactional(readOnly = true)
    public List<LicenseHistory> getHistoryByStatus(String status) {
        TypedQuery<LicenseHistory> query = entityManager.createQuery("SELECT lh FROM LicenseHistory lh WHERE lh.status = :status", LicenseHistory.class);
        query.setParameter("status", status);
        return query.getResultList();
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

    public void recordLicenseChange(License license, ApplicationUser user, String activated, String success) {
    }

    public List<LicenseHistory> getHistoryByUser(Optional<LicenseHistory> user) {
        return null;
    }
}
