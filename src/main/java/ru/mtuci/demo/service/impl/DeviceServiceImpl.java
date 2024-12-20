package ru.mtuci.demo.service.impl;

import ru.mtuci.demo.demo.Device;
import ru.mtuci.demo.demo.License;
import ru.mtuci.demo.repository.DeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceServiceImpl {

    private final DeviceRepository deviceRepository;

    public DeviceServiceImpl(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public Optional<Device> findDeviceById(Long id) {
        return deviceRepository.findById(id);
    }

    @Transactional
    public Device saveDevice(Device device) {
        // Валидация данных
        if (device == null || device.getName() == null || device.getName().isEmpty() || device.getMacAddress() == null || device.getMacAddress().isEmpty()) {
            throw new IllegalArgumentException("Некорректные данные устройства.");
        }
        return deviceRepository.save(device);
    }

    public Optional<Device> getDeviceById(Long id) {
        return deviceRepository.findById(id);
    }

    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    @Transactional
    public void deleteDevice(Long id) {
        // Проверка на существование устройства перед удалением
        if (!deviceRepository.existsById(id)) {
            throw new IllegalArgumentException("Устройство с ID " + id + " не найдено.");
        }
        deviceRepository.deleteById(id);
    }

    @Transactional
    public Device updateDevice(Device device) {
        // Валидация данных
        if (device == null || device.getId() == null || device.getName() == null || device.getName().isEmpty() || device.getMacAddress() == null || device.getMacAddress().isEmpty()) {
            throw new IllegalArgumentException("Некорректные данные устройства.");
        }

        // Проверка на существование устройства перед обновлением  (можно убрать, если  save  сделает merge)
        if (!deviceRepository.existsById(device.getId())) {
            throw new IllegalArgumentException("Устройство с ID " + device.getId() + " не найдено.");
        }
        return deviceRepository.save(device);
    }

    @Transactional
    public Device createDeviceLicense(License license, Device device) {
        device.setLicense(license);
        return deviceRepository.save(device);
    }

    public Optional<Device> findByMacAddress(String macAddress) {
        return deviceRepository.findByMacAddress(macAddress);
    }
}