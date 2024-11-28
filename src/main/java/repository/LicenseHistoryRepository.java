package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.demo.LicenseHistory;

public interface LicenseHistoryRepository extends JpaRepository<LicenseHistory, Long> {
}
