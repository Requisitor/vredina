package com.example.demo.service.impl;

import com.example.demo.demo.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class LicenseServiceImpl {

    private final LicenseRepository licenseRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final LicenseTypeRepository licenseTypeRepository;
    private final LicenseHistoryRepository licenseHistoryRepository;

    @Autowired
    public LicenseServiceImpl(LicenseRepository licenseRepository,
                              ProductRepository productRepository,
                              UserRepository userRepository,
                              LicenseTypeRepository licenseTypeRepository,
                              LicenseHistoryRepository licenseHistoryRepository) {
        this.licenseRepository = licenseRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.licenseTypeRepository = licenseTypeRepository;
        this.licenseHistoryRepository = licenseHistoryRepository;
    }

    public License findLicenseById(Long id) {
        Optional<License> optionalLicense = licenseRepository.findById(id);
        return optionalLicense.orElse(null); // Или бросьте исключение, если это более подходящее поведение
    }

    public Optional<License> findLicenseByCode(String activationCode) {
        return licenseRepository.findByActivationCode(activationCode);
    }

    public boolean validateActivation(License license, Device device, ApplicationUser user) {
        if (license.getEndingDate().before(new Date())) {
            return false;
        }
        if (!license.getOwner().equals(user)) {
            return false;
        }
        if (license.getDeviceLicenses().size() >= license.getDeviceCount()) {
            return false;
        }
        return true;
    }

    @Transactional
    public void updateLicense(License license) {
        licenseRepository.save(license);
    }

    public Ticket generateTicket(License license, Device device) {
        LocalDateTime serverTime = LocalDateTime.now(ZoneId.of("UTC"));
        LocalDateTime firstActivationDate = LocalDateTime.now(ZoneId.of("UTC"));
        LocalDateTime endingDate = firstActivationDate.plusDays(license.getLicenseType().getDuration());

        Ticket ticket = new Ticket(license.getLicenseType().getDuration() * 24 * 60 * 60,
                firstActivationDate,
                endingDate,
                license.getOwner().getId(),
                device.getId());
        return ticket;
    }

    public List<License> getAllLicenses() {
        return licenseRepository.findAll();
    }

    @Transactional
    public License createLicense(Long productId, Long ownerId, Long licenseTypeId, Map<String, Object> parameters) {
        Product product = getProductById(productId);
        ApplicationUser owner = getUserById(ownerId);
        LicenseType licenseType = getLicenseTypeById(licenseTypeId);

        // Получаем текущего пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        ApplicationUser currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        License license = new License();
        license.setProduct(product);
        license.setOwner(owner);
        license.setLicenseType(licenseType);
        license.setParameters(parameters);
        license.setUser(currentUser); // Устанавливаем пользователя

        // Другие настройки лицензии
        license.setFirstActivationDate(new Date());
        license.setEndingDate(calculateExpirationDate(license));
        license.setBlocked(false);
        license.setDeviceCount(parameters.containsKey("deviceCount") ?
                Integer.parseInt(parameters.get("deviceCount").toString()) : 1);
        license.setDuration((long) (parameters.containsKey("duration") ?
                Integer.parseInt(parameters.get("duration").toString()) : 365));
        license.setDescription(parameters.containsKey("description") ?
                parameters.get("description").toString() : "Standart License");
        license.setDeviceLicenses(new ArrayList<>());
        license.setDevice(parameters.containsKey("device") ?
                Boolean.parseBoolean(parameters.get("device").toString()) : false);

        license.setCode(license.generateCode());
        license.setActivationCode(generateActivationCode());
        license.setCreationDate(new Date());
        license.setExpirationDate(calculateExpirationDate(license));

        // Сохраняем лицензию
        license = licenseRepository.save(license);

        recordLicenseChange(license, "Создана", "Created", currentUser);

        return license;
    }

    public License getLicenseById(Long id) {
        Optional<License> license = licenseRepository.findById(id);
        if (license.isPresent()) {
            return license.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "License not found");
        }
    }

    @Transactional
    public License updateLicense(Long id, Map<String, Object> parameters) {
        License license = getLicenseById(id);
        if (license != null) {
            license.setParameters(parameters);
            license = licenseRepository.save(license);

            // Fetch the current user here
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();
            ApplicationUser currentUser = userRepository.findByEmail(currentUsername)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            recordLicenseChange(license, "Обновлена", "Описание", currentUser);
        }
        return license;
    }

    @Transactional
    public void deleteLicense(Long id) {
        License license = getLicenseById(id);
        if (license != null) {
            // Сначала удаляем связанные записи LicenseHistory
            licenseHistoryRepository.deleteAllByLicense(license);
            // Теперь можно безопасно удалить саму лицензию
            licenseRepository.delete(license);
        }
    }

    public void recordLicenseChange(License license, String status, String description, ApplicationUser user) {
        LicenseHistory historyEntry = new LicenseHistory();
        historyEntry.setLicense(license);
        historyEntry.setStatus(status);
        historyEntry.setDescription(description);
        historyEntry.setChangeDate(LocalDateTime.now());
        historyEntry.setUser(user);
        licenseHistoryRepository.save(historyEntry);
    }

    @Transactional
    public License extendLicense(Long licenseId, int extensionPeriodInDays) {
        License license = getLicenseById(licenseId);
        if (license == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "License not found");
        }

        // Получаем текущего пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        ApplicationUser currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Продлеваем лицензию
        Calendar cal = Calendar.getInstance();
        cal.setTime(license.getEndingDate());
        cal.add(Calendar.DAY_OF_YEAR, extensionPeriodInDays);
        license.setEndingDate(cal.getTime());

        // Обновляем duration
        license.setDuration(license.getDuration() + extensionPeriodInDays);

        // Сохраняем изменения
        license = licenseRepository.save(license);

        // Записываем изменения в историю
        recordLicenseChange(license, "Продлена", "Продлена на " + extensionPeriodInDays + " дней", currentUser);

        return license;
    }


    private String generateActivationCode() {
        return UUID.randomUUID().toString();
    }

    private Date calculateExpirationDate(License license) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.YEAR, 1);
        return cal.getTime();
    }

    private Product getProductById(Long productId) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            return product.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
    }

    private ApplicationUser getUserById(Long userId) {
        Optional<ApplicationUser> user = userRepository.findById(userId);
        return user.orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Пользователь с ID " + userId + " не найден"
        ));
    }

    private LicenseType getLicenseTypeById(Long licenseTypeId) {
        Optional<LicenseType> licenseType = licenseTypeRepository.findById(licenseTypeId);
        if (licenseType.isPresent()) {
            return licenseType.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "License type not found");
        }
    }
}