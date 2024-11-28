package ru.mtuci.demo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

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

    @OneToMany(mappedBy = "license")
    private List<DeviceLicense> deviceLicenses;

    @OneToMany(mappedBy = "license")
    private List<LicenseHistory> licenseHistory;

}