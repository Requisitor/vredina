package ru.mtuci.demo.repository;

import ru.mtuci.demo.demo.ApplicationUser;
import ru.mtuci.demo.demo.License;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.demo.demo.LicenseHistory;

import java.util.List;

public interface LicenseHistoryRepository extends JpaRepository<LicenseHistory, Long> {
    List<LicenseHistory> findByLicenseId(Long licenseId);

    void deleteAllByLicense(License license);


    List<LicenseHistory> findByLicense(License license);

    List<LicenseHistory> findByUser(ApplicationUser user);

    List<LicenseHistory> findByStatus(String status);

}