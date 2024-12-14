package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.demo.LicenseType;  // Изменено на LicenseType с заглавной буквы

public interface LicenseTypeRepository extends JpaRepository<LicenseType, Long> {
}