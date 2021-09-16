package com.services;

import com.entities.User;
import com.repositories.UserRepository;
import com.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("Login " + usernameOrEmail + " not found");
        }
        return UserPrincipal.create(user.get());
    }

}
