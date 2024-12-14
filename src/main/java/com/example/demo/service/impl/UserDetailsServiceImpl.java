package com.example.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.demo.demo.ApplicationUser;
import com.example.demo.demo.UserDetailsImpl;
import com.example.demo.repository.UserRepository;

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
}

