package ru.mtuci.demo.service.impl;

import ru.mtuci.demo.demo.Device;
import ru.mtuci.demo.demo.License;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.demo.repository.DeviceLicenseRepository;
import ru.mtuci.demo.demo.DeviceLicense;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceLicenseServiceImpl {

    private final DeviceLicenseRepository deviceLicenseRepository;

    public List<DeviceLicense> getAllDeviceLicenses() {
        return deviceLicenseRepository.findAll();
    }

    public DeviceLicense getDeviceLicenseById(Long id) {
        return deviceLicenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DeviceLicense not found with id: " + id));
    }

    public DeviceLicense createDeviceLicense(License license, Device device) {
        DeviceLicense deviceLicense = new DeviceLicense();
        deviceLicense.setLicense(license);
        deviceLicense.setDevice(device);
        deviceLicense.setActivationDate(new Date());
        return deviceLicenseRepository.save(deviceLicense);
    }

    public DeviceLicense updateDeviceLicense(Long id, DeviceLicense updatedDeviceLicense) {
        DeviceLicense deviceLicense = getDeviceLicenseById(id);
        deviceLicense.setLicense(updatedDeviceLicense.getLicense());
        deviceLicense.setDevice(updatedDeviceLicense.getDevice());
        deviceLicense.setActivationDate(updatedDeviceLicense.getActivationDate());
        return deviceLicenseRepository.save(deviceLicense);
    }

    public void deleteDeviceLicense(Long id) {
        deviceLicenseRepository.deleteById(id);
    }

    public boolean isLicenseActivatedOnDevice(License license, Device device) {
        // Предполагая, что у вас есть DeviceLicenseRepository и сущность DeviceLicense
        return deviceLicenseRepository.existsByLicenseAndDevice(license, device);
    }
}