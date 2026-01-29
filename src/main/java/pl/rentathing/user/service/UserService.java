package pl.rentathing.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rentathing.user.dto.AddressDTO;
import pl.rentathing.user.dto.UserListDTO;
import pl.rentathing.user.dto.UserSettingsDTO;
import pl.rentathing.user.entity.Address;
import pl.rentathing.user.entity.Role;
import pl.rentathing.user.entity.User;
import pl.rentathing.user.exception.InvalidPasswordException;
import pl.rentathing.user.exception.UserNotFoundException;
import pl.rentathing.user.repository.UserRepository;

import java.util.Optional;

/**
 * Service class responsible for managing users in the system.
 * This class provides methods for retrieving, updating, and managing user entities.
 * It uses the UserRepository for database interactions and PasswordEncoder for password encoding.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<UserListDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::mapToUserListDTO);
    }

    /**
     * Retrieves a paginated list of users based on their role.
     *
     * @param role the role to filter users by
     * @param pageable the pagination information
     * @return a paginated list of users matching the specified role
     */
    public Page<UserListDTO> getUsersByRole(Role role, Pageable pageable) {
        return userRepository.findByRole(role, pageable)
                .map(this::mapToUserListDTO);
    }

    /**
     * Searches for users based on the provided filters such as search query, role, and status.
     * If no filters are provided, it returns all users.
     *
     * @param search    A string used to search for users based on their attributes (e.g., name, email).
     * @param role      The role of the users to search for, specified as a string. If "ALL" or invalid, no role filters are applied.
     * @param status    The status filter of the users. It can be "active", "inactive", or "blocked". If null, no status filters are applied.
     * @param pageable  The pagination and sorting information.
     * @return A page of UserListDTO objects that match the search criteria.
     */
    public Page<UserListDTO> searchUsers(String search, String role, String status, Pageable pageable) {
        Role roleEnum = null;
        if (role != null && !role.isEmpty() && !role.equals("ALL")) {
            try {
                roleEnum = Role.valueOf(role);
            } catch (IllegalArgumentException e) {
                roleEnum = null;
            }
        }

        Boolean statusLocked = null;
        Boolean statusDisabled = null;

        if (status != null && !status.isEmpty()) {
            if ("blocked".equals(status)) {
                statusLocked = true;
            } else if ("inactive".equals(status)) {
                statusDisabled = false;
            } else if ("active".equals(status)) {
                statusLocked = false;
                statusDisabled = true;
            }
        }

        if (search != null && !search.isEmpty() || roleEnum != null || statusLocked != null || statusDisabled != null) {
            return userRepository.searchUsersWithStatus(search, roleEnum, statusLocked, statusDisabled, pageable)
                    .map(this::mapToUserListDTO);
        } else {
            return userRepository.findAll(pageable)
                    .map(this::mapToUserListDTO);
        }
    }

    /**
     * Retrieves an optional User object based on the provided email address.
     *
     * @param email the email address to be used for searching the user
     * @return an Optional containing the User if found, or an empty Optional if no user is associated with the given email
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Updates the settings of a user by their ID.
     *
     * @param userId          The unique identifier of the user whose settings need to be updated.
     * @param settingsDTO     An object containing the user's updated settings, including personal information,
     *                        notification preferences, password, and address.
     *
     * @throws UserNotFoundException      If a user with the given ID is not found.
     * @throws InvalidPasswordException   If the provided current password is missing or does not match
     *                                    the user's existing password when attempting to update the password.
     */
    public void updateUserSettings(Long userId, UserSettingsDTO settingsDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.setFirstName(settingsDTO.getFirstName());
        user.setLastName(settingsDTO.getLastName());
        user.setEmail(settingsDTO.getEmail());
        user.setPhoneNumber(settingsDTO.getPhoneNumber());
        user.setNotifEmail(Boolean.TRUE.equals(settingsDTO.getNotifEmail()));
        user.setNotifSms(Boolean.TRUE.equals(settingsDTO.getNotifSms()));
        user.setNewsletter(Boolean.TRUE.equals(settingsDTO.getNewsletter()));

        if (settingsDTO.getNewPassword() != null && !settingsDTO.getNewPassword().isEmpty()) {
            if (settingsDTO.getCurrentPassword() == null || settingsDTO.getCurrentPassword().isEmpty()) {
                throw new InvalidPasswordException("Aktualne hasło jest wymagane do zmiany hasła");
            }
            if (!passwordEncoder.matches(settingsDTO.getCurrentPassword(), user.getPassword())) {
                throw new InvalidPasswordException("Aktualne hasło jest nieprawidłowe");
            }
            user.setPassword(passwordEncoder.encode(settingsDTO.getNewPassword()));
        }

        if (settingsDTO.getAddress() != null) {
            Address address = user.getAddress();
            AddressDTO addressDTO = settingsDTO.getAddress();

            if (address == null) {
                address = new Address();
                address.setUser(user);
                user.setAddress(address);
            }

            address.setStreet(addressDTO.getStreet());
            address.setHouseNumber(addressDTO.getHouseNumber());
            address.setApartmentNumber(addressDTO.getApartmentNumber());
            address.setCity(addressDTO.getCity());
            address.setZipCode(addressDTO.getZipCode());
        }

        userRepository.save(user);
    }

    /**
     * Blocks a user by setting their account as locked. This method retrieves the user
     * by their ID, throws an exception if the user is not found, and updates their
     * locked status.
     *
     * @param userId the ID of the user to be blocked
     */
    public void blockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        user.setLocked(true);
        userRepository.save(user);
    }

    /**
     * Unlocks a user account that was previously locked.
     *
     * @param userId the unique identifier of the user to be unlocked
     * @throws UserNotFoundException if no user with the specified ID is found
     */
    public void unblockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        user.setLocked(false);
        userRepository.save(user);
    }

    /**
     * Maps a User entity to a UserListDTO object.
     *
     * @param user the User entity to be mapped to UserListDTO
     * @return a UserListDTO object containing mapped information from the User entity
     */
    private UserListDTO mapToUserListDTO(User user) {
        return UserListDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .enabled(user.isEnabled())
                .locked(user.isLocked())
                .build();
    }
}
