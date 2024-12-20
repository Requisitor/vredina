package ru.mtuci.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mtuci.demo.demo.ApplicationUser;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Long> {
    Optional<ApplicationUser> findByEmail(String email);
    Optional<ApplicationUser> findByLogin(String login);
    Optional<ApplicationUser> findById(Long id);


}