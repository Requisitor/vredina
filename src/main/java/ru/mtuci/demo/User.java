package ru.mtuci.demo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String login;
    private String passwordHash;
    private String email;
    private String role;

    @OneToMany(mappedBy = "user")
    private List<Device> Device;

    @OneToMany(mappedBy = "user")
    private List<License> License;

    @OneToMany(mappedBy = "user")
    private List<LicenseHistory> LicenseHistory;


    // Getters and setters
}