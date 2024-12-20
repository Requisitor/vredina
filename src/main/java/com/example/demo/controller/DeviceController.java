package com.example.demo.controller;

import com.example.demo.demo.ApplicationUser;
import com.example.demo.demo.Device;
import com.example.demo.demo.DeviceDto;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.impl.DeviceServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceServiceImpl deviceService;
    private final UserRepository userRepository;

    public DeviceController(DeviceServiceImpl deviceService, UserRepository userRepository) {
        this.deviceService = deviceService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<Device> createDevice(@RequestBody DeviceDto deviceDto) {
        ApplicationUser user = deviceDto.getUser();

        if (user == null || user.getId() == null) {
            return ResponseEntity.badRequest().body(null); // Или более подробное сообщение об ошибке
        }

        // Находим пользователя в базе.  Если пользователь не найден, возвращаем ошибку
        Optional<ApplicationUser> existingUser = userRepository.findById(user.getId());
        if (existingUser.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Device device = new Device();
        device.setName(deviceDto.getName());
        device.setMacAddress(deviceDto.getMacAddress());
        device.setUser(existingUser.get()); // Используем найденного пользователя

        Device createdDevice = deviceService.saveDevice(device);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDevice);

    }

    @GetMapping("/{id}")
    public ResponseEntity<Device> getDeviceById(@PathVariable Long id) {
        Optional<Device> optionalDevice = deviceService.getDeviceById(id);

        if (optionalDevice.isPresent()) {
            Device device = optionalDevice.get();
            return ResponseEntity.ok(device);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public List<Device> getAllDevices() {
        return deviceService.getAllDevices();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Device> updateDevice(@PathVariable Long id, @RequestBody DeviceDto deviceDto) {
        //  Находим устройство для обновления
        Optional<Device> existingDevice = deviceService.findDeviceById(id);

        if (existingDevice.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Device deviceToUpdate = existingDevice.get();
        // Обновляем только необходимые поля
        deviceToUpdate.setName(deviceDto.getName());
        deviceToUpdate.setMacAddress(deviceDto.getMacAddress());

        // Если нужно обновить пользователя, сначала находим его по id
        if (deviceDto.getUser() != null && deviceDto.getUser().getId() != null) {
            Optional<ApplicationUser> existingUser = userRepository.findById(deviceDto.getUser().getId());
            existingUser.ifPresent(deviceToUpdate::setUser); // Устанавливаем нового пользователя, если он найден
        }

        Device updatedDevice = deviceService.updateDevice(deviceToUpdate);
        return ResponseEntity.ok(updatedDevice);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
        try {
            deviceService.deleteDevice(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}