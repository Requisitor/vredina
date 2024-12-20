package ru.mtuci.demo.demo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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

    @ManyToOne(fetch = FetchType.EAGER, optional = false) // optional = false обязательно
    @JoinColumn(name = "type_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE) // Добавлено каскадное удаление
    private LicenseType licenseType;


    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
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

    @OneToMany(mappedBy = "license", cascade = CascadeType.REMOVE)
    private List<DeviceLicense> deviceLicenses;

    @OneToMany(mappedBy = "license", cascade = CascadeType.REMOVE)
    private List<LicenseHistory> licenseHistory;



    public License() {

    }

    public boolean getDevice() {
        return true;
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

    public License orElseThrow(Object licenseNotFound) {
        if (this == null) {
            if (licenseNotFound instanceof Throwable) {
                throw (RuntimeException) licenseNotFound;
            } else if (licenseNotFound instanceof String) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, (String) licenseNotFound);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "License not found");
            }
        }
        return this;
    }

}