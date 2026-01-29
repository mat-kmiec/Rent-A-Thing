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
 * Service implementation of the {@link UserDetailsService} interface for loading user-specific data.
 *
 * The {@code CustomUserDetailsService} is responsible for retrieving user details
 * from the database using the provided email address. If a user with the provided
 * email address is not found, a {@link UserNotFoundException} is thrown.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * Loads the user details associated with the given email address.
     * Retrieves user information from the database and returns a {@link UserDetails} object.
     * If the user with the provided email is not found, a {@link UserNotFoundException} is thrown.
     *
     * @param email the email address of the user to be retrieved
     * @return the {@link UserDetails} containing user information
     * @throws UsernameNotFoundException if the user with the given email is not found
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

    }
}
