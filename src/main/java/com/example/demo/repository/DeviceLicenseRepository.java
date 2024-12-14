package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.demo.DeviceLicense;

import java.util.List;

public interface DeviceLicenseRepository extends JpaRepository<DeviceLicense, Long> {
    List<DeviceLicense> findByLicenseId(Long licenseId);
}