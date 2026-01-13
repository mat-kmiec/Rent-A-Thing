package pl.rentathing.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.rentathing.user.entity.User;
import pl.rentathing.user.exception.UserNotFoundException;
import pl.rentathing.user.repository.UserRepository;

/**
 * CustomUserDetailsService is an implementation of the {@link UserDetailsService}.
 * It provides the mechanism to load user-specific data during the authentication process.
 *
 * This service interacts with the {@link UserRepository} to fetch user information
 * based on the provided email and builds an instance of {@link UserDetails}
 * which is required by the Spring Security framework.
 *
 * When a user is not found in the database by their email, a {@link UserNotFoundException} is thrown.
 *
 * Responsibilities:
 * - Loads user data from the database via {@link UserRepository#findByEmail(String)}.
 * - Converts the fetched user data into {@link UserDetails}.
 * - Manages user authentication requirements such as roles, enabled status, and account locking.
 *
 * Note: The email is used as a unique identifier for loading user details.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRole().name())
                .disabled(!user.isEnabled())
                .accountLocked(user.isLocked())
                .build();
    }
}
