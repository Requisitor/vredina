package com.example.demo.service.impl;

import com.example.demo.demo.Device;
import com.example.demo.demo.License;
import com.example.demo.repository.DeviceRepository;
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

    public Device findDeviceById(Long id) {
        Optional<Device> optionalDevice = deviceRepository.findById(id);
        return optionalDevice.orElse(null); // Или бросьте исключение
    }

    @Transactional
    public Device saveDevice(Device device) {
        // Валидация данных (например, проверка на null или пустые значения)
        if (device == null || device.getName() == null || device.getName().isEmpty() || device.getMacAddress() == null || device.getMacAddress().isEmpty()) {
            throw new IllegalArgumentException("Некорректные данные устройства.");
        }
        return deviceRepository.save(device);
    }

    public Device getDeviceById(Long id) {
        // Обработка случая, когда устройство не найдено
        Optional<Device> optionalDevice = deviceRepository.findById(id);
        return optionalDevice.orElse(null); // Возвращает null, если устройство не найдено
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
        // Валидация данных (аналогично saveDevice)
        if (device == null || device.getId() == null || device.getName() == null || device.getName().isEmpty() || device.getMacAddress() == null || device.getMacAddress().isEmpty()) {
            throw new IllegalArgumentException("Некорректные данные устройства.");
        }

        // Проверка на существование устройства перед обновлением
        if (!deviceRepository.existsById(device.getId())) {
            throw new IllegalArgumentException("Устройство с ID " + device.getId() + " не найдено.");
        }
        return deviceRepository.save(device);
    }

    @Transactional
    public Device createDeviceLicense(License license, Device device) {
        // Привязываем лицензию к устройству
        device.setLicense(license);
        // Сохраняем устройство и возвращаем его
        return deviceRepository.save(device);
    }
}