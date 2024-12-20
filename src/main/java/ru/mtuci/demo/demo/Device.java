package ru.mtuci.demo.demo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Entity
@Getter
@Setter
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Column(unique = true)
    private String macAddress;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private ApplicationUser user;

    @OneToMany(mappedBy = "device")
    private List<DeviceLicense> deviceLicense;

    public void setLicense(License license) {
    }
}