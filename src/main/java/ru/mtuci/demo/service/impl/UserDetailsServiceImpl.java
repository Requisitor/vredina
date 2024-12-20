package ru.mtuci.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.mtuci.demo.demo.ApplicationUser;
import ru.mtuci.demo.demo.UserDetailsImpl;
import ru.mtuci.demo.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<ApplicationUser> user = userRepository.findByEmail(email);
        return UserDetailsImpl.fromApplicationUser(user.orElseThrow(() -> new UsernameNotFoundException("User not found")));
    }

    public Optional<ApplicationUser> loadUserById(Long userId) {
        return userRepository.findById(userId);
    }
}