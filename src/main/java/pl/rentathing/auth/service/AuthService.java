package pl.rentathing.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rentathing.auth.dto.RegisterRequest;
import pl.rentathing.user.entity.Role;
import pl.rentathing.user.entity.User;
import pl.rentathing.user.exception.UserAlreadyExistException;
import pl.rentathing.user.exception.UserNotFoundException;
import pl.rentathing.user.mapper.UserMapper;
import pl.rentathing.user.repository.UserRepository;

/**
 * Service responsible for handling authentication-related operations, specifically
 * user registration within the application.
 *
 * This class provides functionality to register new users by validating input,
 * mapping the registration data to a user entity, encoding the password for security,
 * setting default roles, and persisting the user to the database.
 *
 * Dependencies required by this service are injected via constructor injection.
 *
 * Main Operations:
 * - Validate if the provided email is already in use.
 * - Transform RegisterRequest data into a User entity using UserMapper.
 * - Encode the user's password securely.
 * - Assign a default ROLE_USER to newly registered users.
 * - Save the user entity into the database.
 *
 * Exceptions:
 * - Throws UserAlreadyExistException if a user with the given email already exists in the system.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registers a new user in the system using the provided registration request.
     * This method validates if an account with the provided email already exists,
     * maps the registration data into a user entity, encodes the user's password,
     * assigns the default role of "ROLE_USER," and saves the user entity to the database.
     *
     * @param registerRequest the registration details including first name, last name,
     *                        email, password, and other necessary information.
     *                        Must not be null and should contain valid input.
     * @throws UserAlreadyExistException if a user with the given email already exists.
     */
    @Transactional
    public void register(RegisterRequest registerRequest) {
        if(userRepository.findByEmail(registerRequest.getEmail()).isPresent()) throw new UserAlreadyExistException(registerRequest.getEmail());
        User user = userMapper.toEntity(registerRequest);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Role.ROLE_USER);
        userRepository.save(user);
    }

    /**
     * Retrieves the currently authenticated user from the security context.
     *
     * This method fetches the email of the authenticated user from the security context
     * and retrieves the corresponding User entity from the database. If no user is found
     * with the provided email, a UserNotFoundException is thrown.
     *
     * @return the currently authenticated User entity
     * @throws UserNotFoundException if no user with the authenticated email exists in the system
     */
    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    /**
     * Retrieves the unique identifier of the currently authenticated user.
     *
     * This method fetches the current user from the authentication context,
     * then retrieves and returns the user's ID as a Long value.
     *
     * @return the ID of the currently authenticated user
     * @throws UserNotFoundException if no user is found for the current authentication context
     */
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }


}
