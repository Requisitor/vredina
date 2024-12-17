package com.example.demo.demo;

import com.example.demo.repository.UserRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

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