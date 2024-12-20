package com.example.demo.repository;

import com.example.demo.demo.Device;
import com.example.demo.demo.License;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.demo.DeviceLicense;

import java.util.List;

public interface DeviceLicenseRepository extends JpaRepository<DeviceLicense, Long> {
    List<DeviceLicense> findByLicenseId(Long licenseId);
    boolean existsByLicenseAndDevice(License license, Device device);
}