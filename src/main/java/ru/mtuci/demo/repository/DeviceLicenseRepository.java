package ru.mtuci.demo.repository;

import ru.mtuci.demo.demo.Device;
import ru.mtuci.demo.demo.License;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.demo.demo.DeviceLicense;

import java.util.List;

public interface DeviceLicenseRepository extends JpaRepository<DeviceLicense, Long> {
    List<DeviceLicense> findByLicenseId(Long licenseId);
    boolean existsByLicenseAndDevice(License license, Device device);
}