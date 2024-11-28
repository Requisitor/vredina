package ru.mtuci.demo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
public class LicenseType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer defaultDuration;

    private String description;

    @OneToMany(mappedBy = "LicenseType")
    private List<License> license;

}