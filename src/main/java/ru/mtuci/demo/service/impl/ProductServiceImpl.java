package ru.mtuci.demo.service.impl;

import ru.mtuci.demo.demo.Product;
import ru.mtuci.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    public List<Product> findAll() {
        return productRepository.findAll();
    }


    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }


    public Product save(Product product) {
        return productRepository.save(product);
    }


    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }


    public List<Product> findByName(String name) {
        return productRepository.findByName(name);
    }


    public List<Product> findByIsBlocked(Boolean isBlocked) {
        return productRepository.findByIsBlocked(isBlocked);
    }

    }
