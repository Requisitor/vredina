package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import com.example.demo.demo.ApplicationUser;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Long> {
    Optional<ApplicationUser> findByEmail(String email);
    Optional<ApplicationUser> findByLogin(String login);


}