package com.example.demo.repository;

import com.example.demo.demo.License;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.demo.LicenseHistory;

import java.util.List;

public interface LicenseHistoryRepository extends JpaRepository<LicenseHistory, Long> {
    List<LicenseHistory> findByLicenseId(Long licenseId);

    void deleteAllByLicense(License license);


    List<LicenseHistory> findByLicense(com.example.demo.demo.License license);

    List<LicenseHistory> findByUser(com.example.demo.demo.ApplicationUser user);

    List<LicenseHistory> findByStatus(String status);

}