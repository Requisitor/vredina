package ru.mtuci.demo.demo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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