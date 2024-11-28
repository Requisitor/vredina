package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.demo.Device;

public interface DeviceRepository extends JpaRepository<Device, Long> {
}