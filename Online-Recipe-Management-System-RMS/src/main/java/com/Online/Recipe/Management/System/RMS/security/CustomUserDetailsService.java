package com.Online.Recipe.Management.System.RMS.security;

import com.Online.Recipe.Management.System.RMS.models.User;
import com.Online.Recipe.Management.System.RMS.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList; // For roles/authorities if not using User.getAuthorities()

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + email));

        // Spring Security's User class can be used directly if your User model doesn't implement UserDetails
        // If your User model implements UserDetails, you can return it directly.
        // For simplicity, we'll use Spring Security's User.
        // The authorities list would typically come from the user's roles.
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(), // Spring Security expects the "username" here, which is email in our case
            user.getPasswordHash(),
            new ArrayList<>() // Empty authorities list for now
        );
    }

    // This method would be used by JWT authentication filter to load user by ID
    @Transactional
    public UserDetails loadUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("User not found with id : " + id)
        );
         return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPasswordHash(),
            new ArrayList<>()
        );
    }
}
