package pl.rentathing.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rentathing.user.dto.UserListDTO;
import pl.rentathing.user.dto.UserSettingsDTO;
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
        
        if (settingsDTO.getNewPassword() != null && !settingsDTO.getNewPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(settingsDTO.getNewPassword()));
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
