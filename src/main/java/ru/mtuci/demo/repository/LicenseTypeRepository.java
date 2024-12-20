package ru.mtuci.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.demo.demo.LicenseType;  // Изменено на LicenseType с заглавной буквы

import java.util.Optional;

public interface LicenseTypeRepository extends JpaRepository<LicenseType, Long> {
    Optional<LicenseType> findByName(String name);

}