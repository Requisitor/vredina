package ru.mtuci.demo.repository;

import ru.mtuci.demo.demo.ApplicationUser;
import ru.mtuci.demo.demo.LicenseType;
import ru.mtuci.demo.demo.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.demo.demo.License;  // Изменено на License с заглавной буквы

import java.util.List;
import java.util.Optional;

public interface LicenseRepository extends JpaRepository<License, Long> {
    List<License> findByUser(ApplicationUser user);

    List<License> findByProduct(Product product);

    List<License> findByLicenseType(LicenseType licenseType);

    List<License> findByOwner(ApplicationUser owner);

    Optional<License> findByActivationCode(String activationCode);

    List<License> findByProductId(Long productId);

}