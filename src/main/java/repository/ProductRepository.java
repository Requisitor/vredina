package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.demo.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}