package pl.rentathing.user.dto;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for managing user-related data in a list or table format.
 *
 * This class serves as a lightweight container used for transferring user information
 * between different layers of the application. It does not contain any business logic
 * and is primarily used for read-only purposes or as input/output for APIs and services.
 *
 * Fields:
 * - id: A unique identifier for the user.
 * - firstName: The user's first name, which must not be empty.
 * - lastName: The user's last name, which must not be empty.
 * - email: The user's email address, which must be in a valid format and cannot be empty.
 * - role: The user's role within the system (e.g., Admin, User, etc.).
 * - enabled: A flag indicating whether the user account is active.
 * - locked: A flag indicating whether the user account is locked.
 * - createdAt: The timestamp when the user account was created.
 */
@Data
@Builder
public class UserListDTO {
    private Long id;
    
    @NotBlank(message = "Imię nie może być puste")
    private String firstName;
    
    @NotBlank(message = "Nazwisko nie może być puste")
    private String lastName;
    
    @Email(message = "Nieprawidłowy format emaila")
    @NotBlank(message = "Email nie może być pusty")
    private String email;
    
    private String role;
    
    private Boolean enabled;
    
    private Boolean locked;
    
    private LocalDateTime createdAt;
}
