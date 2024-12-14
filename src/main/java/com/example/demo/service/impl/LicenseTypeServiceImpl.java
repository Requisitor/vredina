package com.example.demo.service.impl;

import com.example.demo.demo.LicenseType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LicenseTypeServiceImpl {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public LicenseType save(LicenseType licenseType) {
        if (licenseType.getId() == null) {
            entityManager.persist(licenseType);
            //  Flush the entity manager to ensure the ID is generated and assigned
            entityManager.flush();
        } else {
            entityManager.merge(licenseType);
        }
        return licenseType;
    }

    @Transactional
    public void delete(Long id) {
        LicenseType licenseType = entityManager.find(LicenseType.class, id);

        if (licenseType != null) {
            entityManager.remove(licenseType);
        } else {
            throw new IllegalArgumentException("License type with ID " + id + " not found.");
        }
    }

    public LicenseType findById(Long id) {
        return entityManager.find(LicenseType.class, id);
    }

    public LicenseType getLicenseTypeById(Long licenseTypeId) {
        return entityManager.find(LicenseType.class, licenseTypeId);
    }


    public List<LicenseType> findAll() {
        TypedQuery<LicenseType> query = entityManager.createQuery("SELECT lt FROM LicenseType lt", LicenseType.class);
        return query.getResultList();
    }


    public Optional<LicenseType> findByName(String name) {
        TypedQuery<LicenseType> query = entityManager.createQuery("SELECT lt FROM LicenseType lt WHERE lt.name = :name", LicenseType.class);
        query.setParameter("name", name);
        try {
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }


    @Transactional
    public void updateDefaultDuration(Long id, Integer newDuration) {
        LicenseType licenseType = entityManager.find(LicenseType.class, id);

        if (licenseType != null) {
            licenseType.setDefaultDuration(newDuration);
            entityManager.merge(licenseType);
        } else {
            throw new IllegalArgumentException("License type with ID " + id + " not found.");
        }
    }
}
