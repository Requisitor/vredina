package com.example.demo.demo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Setter
@Getter
@Entity
public class License {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private ApplicationUser user;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private LicenseType licenseType;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private ApplicationUser owner;

    private Date firstActivationDate;
    private Date endingDate;
    private Boolean blocked;
    private Integer deviceCount;
    private Long duration;
    private String description;
    // <-- Добавлено
    @Setter
    private String activationCode;

    @OneToMany(mappedBy = "license")
    private List<DeviceLicense> deviceLicenses;

    @OneToMany(mappedBy = "license")
    private List<LicenseHistory> licenseHistory;


    public License() {

    }

    public boolean getDevice() {
        return false;
    }

    public void setParameters(Map<String, Object> parameters) {
    }

    public void setCreationDate(Date date) {
    }

    public void setExpirationDate(Date date) {
    }

    public void setDevice(boolean b) {
    }

    public String generateCode() {
        this.code = UUID.randomUUID().toString();
        return this.code;
    }
}