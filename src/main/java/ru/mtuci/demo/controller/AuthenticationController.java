package ru.mtuci.demo.controller;

import ru.mtuci.demo.configuration.JwtTokenProvider;
import ru.mtuci.demo.demo.ApplicationRole;
import ru.mtuci.demo.demo.ApplicationUser;
import ru.mtuci.demo.demo.AuthenticationRequest;
import ru.mtuci.demo.demo.AuthenticationResponse;
import ru.mtuci.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request) {
        String email = request.getEmail();

        ApplicationUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(
                                email, request.getPassword())
                );

        String token = jwtTokenProvider
                .createToken(email, user.getRole().getGrantedAuthorities());

        return ResponseEntity.ok(new AuthenticationResponse(email, token));
    }

    @PostMapping("/registration")
    public ResponseEntity<?> register(@RequestBody AuthenticationRequest request) {
        try {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body("Email already exists");
            }

            ApplicationUser newUser = new ApplicationUser();
            newUser.setEmail(request.getEmail());
            newUser.setLogin(request.getLogin());
            newUser.setPassword(passwordEncoder.encode(request.getPassword()));
            newUser.setRole(ApplicationRole.ADMIN);

            userRepository.save(newUser);

            return ResponseEntity.ok("User registered successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error during registration: " + e.getMessage());
        }
    }
}