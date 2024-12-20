package com.example.demo.repository;

import com.example.demo.demo.ApplicationUser;
import com.example.demo.demo.LicenseType;
import com.example.demo.demo.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.demo.License;  // Изменено на License с заглавной буквы

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