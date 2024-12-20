package com.example.demo.controller;

import com.example.demo.demo.Product;
import com.example.demo.service.impl.LicenseServiceImpl;
import com.example.demo.service.impl.ProductServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductServiceImpl productService;
    private final LicenseServiceImpl licenseService;

    public ProductController(ProductServiceImpl productService, LicenseServiceImpl licenseService) {
        this.productService = productService;
        this.licenseService = licenseService;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')") // Доступ для роли USER
    public List<Product> getAllProducts() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')") // Доступ для роли USER
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Доступ только для роли ADMIN
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Доступ только для роли ADMIN
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        if (!productService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        product.setId(id);
        Product updatedProduct = productService.save(product);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (!productService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        // Находим лицензии, связанные с продуктом
        List<com.example.demo.demo.License> licenses = licenseService.findLicensesByProduct(id);

        // Удаляем связанные лицензии
        for (com.example.demo.demo.License license : licenses) {
            licenseService.deleteLicense(license.getId());
        }

        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/search")
    @PreAuthorize("hasRole('USER')") // Доступ для роли USER
    public ResponseEntity<List<Product>> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isBlocked
    ) {
        List<Product> products;
        if (name != null) {
            products = productService.findByName(name);
        } else if (isBlocked != null) {
            products = productService.findByIsBlocked(isBlocked);
        } else {
            products = productService.findAll();
        }
        return ResponseEntity.ok(products);
    }
}