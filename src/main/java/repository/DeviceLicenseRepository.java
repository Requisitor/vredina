package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.demo.DeviceLicense;

public interface DeviceLicenseRepository extends JpaRepository<DeviceLicense, Long> {
}