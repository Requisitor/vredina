package com.example.demo.service.impl;

import com.example.demo.demo.*;
import com.example.demo.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class LicenseServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(LicenseServiceImpl.class);

    private final LicenseRepository licenseRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final LicenseTypeRepository licenseTypeRepository;
    private final LicenseHistoryRepository licenseHistoryRepository;
    private final AuthenticationServiceImpl authenticationService;

    @Value("${digital-signature.secret-key}")
    private String secretKey;


    @Autowired
    public LicenseServiceImpl(LicenseRepository licenseRepository,
                              ProductRepository productRepository,
                              UserRepository userRepository,
                              LicenseTypeRepository licenseTypeRepository,
                              LicenseHistoryRepository licenseHistoryRepository,
                              AuthenticationServiceImpl authenticationService) {
        this.licenseRepository = licenseRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.licenseTypeRepository = licenseTypeRepository;
        this.licenseHistoryRepository = licenseHistoryRepository;
        this.authenticationService = authenticationService;
    }

    public License findLicenseById(Long id) {
        logger.info("Finding license by ID: {}", id);
        Optional<License> optionalLicense = licenseRepository.findById(id);
        License license = optionalLicense.orElse(null);
        if (license != null) {
            logger.info("License found: {}", license);
        } else {
            logger.warn("License with ID {} not found.", id);
        }
        return license;
    }

    public Optional<License> findLicenseByCode(String activationCode) {
        logger.info("Finding license by activation code: {}", activationCode);
        Optional<License> license = licenseRepository.findByActivationCode(activationCode);
        if (license.isPresent()) {
            logger.info("License found: {}", license.get());
        } else {
            logger.warn("License with activation code {} not found.", activationCode);
        }
        return license;
    }

    public boolean validateActivation(License license, Device device, ApplicationUser user) {
        logger.info("Validating activation for license: {}, device: {}, user: {}", license.getId(), device.getId(), user.getId());
        if (license.getEndingDate().before(new Date())) {
            logger.warn("License {} has expired.", license.getId());
            return false;
        }
        if (!license.getUser().equals(user)) {
            logger.warn("License {} does not belong to user {}.", license.getId(), user.getId());
            return false;
        }
        if (license.getDeviceLicenses().size() >= license.getDeviceCount()) {
            logger.warn("License {} has reached device limit.", license.getId());
            return false;
        }
        logger.info("License {} is valid for activation.", license.getId());
        return true;
    }

    @Transactional
    public void updateLicense(License license) {
        logger.info("Updating license: {}", license.getId());
        licenseRepository.save(license);
        logger.info("License {} updated successfully.", license.getId());
    }

    public Ticket generateTicket(License license, Device device) {
        logger.info("Generating ticket for license: {}, device: {}", license.getId(), device.getId());
        LocalDateTime serverTime = LocalDateTime.now(ZoneId.of("UTC"));
        LocalDateTime firstActivationDate = LocalDateTime.now(ZoneId.of("UTC"));
        LocalDateTime endingDate = firstActivationDate.plusDays(license.getLicenseType().getDuration());

        // Устанавливаем время жизни тикета в 30 минут (30 * 60 секунд)
        long ticketLifetime = 30 * 60;

        // Создаем данные для подписи
        String dataToSign = license.getId() + ":" + device.getId() + ":" + firstActivationDate + ":" + endingDate;

        // Генерируем цифровую подпись
        String digitalSignature = null;
        try {
            digitalSignature = generateDigitalSignature(dataToSign);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            logger.error("Error generating digital signature: ", e);
            throw new RuntimeException("Error generating digital signature", e);
        }

        Ticket ticket = new Ticket(ticketLifetime, // Передаем время жизни тикета
                firstActivationDate,
                endingDate,
                license.getOwner().getId(),
                device.getId(),
                digitalSignature);
        logger.info("Ticket generated: {}", ticket);
        return ticket;
    }

    public List<License> getAllLicenses() {
        logger.info("Fetching all licenses.");
        List<License> licenses = licenseRepository.findAll();
        logger.info("Found {} licenses.", licenses.size());
        return licenses;
    }

    @Transactional
    public License createLicense(Long productId, Long ownerId, Long licenseTypeId, Map<String, Object> parameters) {
        logger.info("Creating license for product: {}, owner: {}, license type: {}, parameters: {}", productId, ownerId, licenseTypeId, parameters);
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
        logger.info("License created with ID: {}", license.getId());

        recordLicenseChange(license, "Создана", "Created", currentUser);

        return license;
    }

    public License getLicenseById(Long id) {
        logger.info("Fetching license by ID: {}", id);
        Optional<License> license = licenseRepository.findById(id);
        if (license.isPresent()) {
            logger.info("License found: {}", license.get());
            return license.get();
        } else {
            logger.warn("License with ID {} not found.", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "License not found");
        }
    }

    @Transactional
    public License updateLicense(Long id, Map<String, Object> parameters) {
        logger.info("Updating license with ID: {}, parameters: {}", id, parameters);
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
            logger.info("License with ID {} updated successfully.", id);
        }
        return license;
    }

    @Transactional
    public void deleteLicense(Long id) {
        logger.info("Deleting license with ID: {}", id);
        License license = getLicenseById(id);
        if (license != null) {
            // Сначала удаляем связанные записи LicenseHistory
            licenseHistoryRepository.deleteAllByLicense(license);
            // Теперь можно безопасно удалить саму лицензию
            licenseRepository.delete(license);
            logger.info("License with ID {} deleted successfully.", id);
        } else {
            logger.warn("License with ID {} not found for deletion.", id);
        }
    }

    public void recordLicenseChange(License license, String status, String description, ApplicationUser user) {
        logger.info("Recording license change for license: {}, status: {}, description: {}, user: {}", license.getId(), status, description, user.getId());
        LicenseHistory historyEntry = new LicenseHistory();
        historyEntry.setLicense(license);
        historyEntry.setStatus(status);
        historyEntry.setDescription(description);
        historyEntry.setChangeDate(LocalDateTime.now());
        historyEntry.setUser(user);
        licenseHistoryRepository.save(historyEntry);
        logger.info("License change recorded successfully: {}", historyEntry);
    }

    @Transactional
    public License extendLicense(Long licenseId, int extensionPeriodInDays) {
        logger.info("Extending license with ID: {}, by {} days", licenseId, extensionPeriodInDays);
        License license = getLicenseById(licenseId);
        if (license == null) {
            logger.warn("License with ID {} not found for extension.", licenseId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "License not found");
        }

        // Получаем текущего пользователя с помощью AuthenticationService
        ApplicationUser currentUser = authenticationService.getCurrentUser();

        // Проверяем, имеет ли текущий пользователь право продлевать лицензию для кого угодно
            // Проверяем, является ли текущий пользователь владельцем лицензии
            if (!currentUser.getId().equals(license.getUser().getId())) {
                logger.warn("User with email {} does not have permission to extend license with ID {}", currentUser.getEmail(), licenseId);
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to extend this license");
            }


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
        logger.info("License with ID {} extended successfully.", licenseId);
        return license;
    }



    private String generateActivationCode() {
        String activationCode = UUID.randomUUID().toString();
        logger.info("Generated activation code: {}", activationCode);
        return activationCode;
    }

    private Date calculateExpirationDate(License license) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.YEAR, 1);
        Date expirationDate = cal.getTime();
        logger.info("Calculated expiration date: {}", expirationDate);
        return expirationDate;
    }

    private Product getProductById(Long productId) {
        logger.info("Fetching product by ID: {}", productId);
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            logger.info("Product found: {}", product.get());
            return product.get();
        } else {
            logger.warn("Product with ID {} not found.", productId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
    }

    private ApplicationUser getUserById(Long userId) {
        logger.info("Fetching user by ID: {}", userId);
        Optional<ApplicationUser> user = userRepository.findById(userId);
        if (user.isPresent()) {
            logger.info("User found: {}", user.get());
            return user.get();
        } else {
            logger.warn("User with ID {} not found.", userId);
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Пользователь с ID " + userId + " не найден"
            );
        }
    }

    private LicenseType getLicenseTypeById(Long licenseTypeId) {
        logger.info("Fetching license type by ID: {}", licenseTypeId);
        Optional<LicenseType> licenseType = licenseTypeRepository.findById(licenseTypeId);
        if (licenseType.isPresent()) {
            logger.info("License type found: {}", licenseType.get());
            return licenseType.get();
        } else {
            logger.warn("License type with ID {} not found.", licenseTypeId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "License type not found");
        }
    }

    public String generateDigitalSignature(String data) throws NoSuchAlgorithmException, InvalidKeyException {
        // Create a Mac instance for the HMAC-SHA256 algorithm
        Mac mac = Mac.getInstance("HmacSHA256");

        // Initialize the Mac instance with the secret key
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
        mac.init(secretKeySpec);

        // Generate the digital signature
        byte[] digitalSignature = mac.doFinal(data.getBytes());

        // Encode the digital signature to a base64 string
        return Base64.getEncoder().encodeToString(digitalSignature);
    }
}