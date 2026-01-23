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
import pl.rentathing.user.repository.UserRepository;

import java.util.Optional;

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

    public Page<UserListDTO> getUsersByRole(Role role, Pageable pageable) {
        return userRepository.findByRole(role, pageable)
                .map(this::mapToUserListDTO);
    }

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

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public void updateUserSettings(Long userId, UserSettingsDTO settingsDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        user.setFirstName(settingsDTO.getFirstName());
        user.setLastName(settingsDTO.getLastName());
        user.setEmail(settingsDTO.getEmail());
        user.setPhoneNumber(settingsDTO.getPhoneNumber());
        user.setNotifEmail(Boolean.TRUE.equals(settingsDTO.getNotifEmail()));
        user.setNotifSms(Boolean.TRUE.equals(settingsDTO.getNotifSms()));
        user.setNewsletter(Boolean.TRUE.equals(settingsDTO.getNewsletter()));

        if (settingsDTO.getNewPassword() != null && !settingsDTO.getNewPassword().isEmpty()) {
            if (settingsDTO.getCurrentPassword() == null || settingsDTO.getCurrentPassword().isEmpty()) {
                throw new RuntimeException("Aktualne hasło jest wymagane do zmiany hasła");
            }
            if (!passwordEncoder.matches(settingsDTO.getCurrentPassword(), user.getPassword())) {
                throw new RuntimeException("Aktualne hasło jest nieprawidłowe");
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

    public void blockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));
        user.setLocked(true);
        userRepository.save(user);
    }

    public void unblockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));
        user.setLocked(false);
        userRepository.save(user);
    }

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
