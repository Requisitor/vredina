package com.example.demo.demo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String login;
    @JsonIgnore
    private String password;
    @JsonIgnore
    private String email;

    @Enumerated(EnumType.STRING)
    private ApplicationRole role;

    public ApplicationUser(String email, String password, ApplicationRole role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }
}


    /*public boolean isActive() {
        return false;
    }

    public boolean isPresent() {
        return false;
    }

    public Optional<ResponseEntity<Object>> map(Object o) {
        return null;
    }

    public ApplicationUser orElseThrow(Object userNotFound) {
        return null;
    }
}*/