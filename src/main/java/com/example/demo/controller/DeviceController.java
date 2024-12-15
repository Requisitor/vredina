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

@RestController
@RequestMapping("/api/devices") // Добавляем @RequestMapping на уровне класса
public class DeviceController {

    private final DeviceServiceImpl deviceService;
    private final UserRepository userRepository;

    public DeviceController(DeviceServiceImpl deviceService, UserRepository userRepository) {
        this.deviceService = deviceService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<Device> createDevice(@RequestBody DeviceDto deviceDto) {
        try {
            // Получаем пользователя из DTO
            ApplicationUser user = deviceDto.getUser();

            // Проверяем, передан ли пользователь
            if (user == null) {
                throw new IllegalArgumentException("User is required");
            }

            // Сохраняем пользователя (если нужно)
            user = userRepository.save(user);

            // Создаем устройство и связываем с пользователем
            Device device = new Device();
            device.setName(deviceDto.getName());
            device.setMacAddress(deviceDto.getMacAddress());
            device.setUser(user);

            Device createdDevice = deviceService.saveDevice(device);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDevice);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{id}") // Добавляем @GetMapping и корректный путь
    public ResponseEntity<Device> getDeviceById(@PathVariable Long id) {
        Device device = deviceService.getDeviceById(id);
        if (device != null) {
            return ResponseEntity.ok(device);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public List<Device> getAllDevices() {
        return deviceService.getAllDevices();
    }

    @PutMapping("/{id}") // Используем @PutMapping для обновления
    public ResponseEntity<Device> updateDevice(@PathVariable Long id, @RequestBody Device device) {
        try {
            device.setId(id); // Устанавливаем ID из пути запроса
            Device updatedDevice = deviceService.updateDevice(device);
            return ResponseEntity.ok(updatedDevice);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}") // Используем @DeleteMapping для удаления
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
        try {
            deviceService.deleteDevice(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}