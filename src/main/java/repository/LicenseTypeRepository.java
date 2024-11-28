package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.demo.LicenseType;  // Изменено на LicenseType с заглавной буквы

public interface LicenseTypeRepository extends JpaRepository<LicenseType, Long> {
}