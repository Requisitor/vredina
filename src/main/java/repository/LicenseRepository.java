package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.demo.License;  // Изменено на License с заглавной буквы

public interface LicenseRepository extends JpaRepository<License, Long> {
}