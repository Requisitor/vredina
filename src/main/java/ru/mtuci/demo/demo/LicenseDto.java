package ru.mtuci.demo.demo;

import ru.mtuci.demo.repository.UserRepository;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

@Setter
@Getter

public class LicenseDto {

    private UserRepository userRepository;

    private Long id;
    private Long productId;
    private Long ownerId;
    private Long licenseTypeId;
    private Map<String, Object> parameters;
    private String activationCode;
    private Date creationDate;
    private Date expirationDate;
    @Setter
    @Getter
    private String license;


}