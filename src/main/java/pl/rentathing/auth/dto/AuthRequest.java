package pl.rentathing.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Abstract class representing an authentication request.
 * It serves as a base class for specific authentication-related requests such as login and registration.
 *
 * The class includes validation constraints for email and password fields to ensure proper formatting
 * and security requirements.
 *
 * Subclasses are expected to provide additional fields and customization as needed.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class AuthRequest {

    @NotBlank(message = "Email nie może być pusty")
    @Email(message = "Podaj poprawny adres email")
    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Format adresu email jest nieprawidłowy"
    )
    protected String email;

    @NotBlank(message = "Hasło nie może być puste")
    @Size(min = 8, message = "Hasło musi mieć co najmniej 8 znaków")
    @Pattern(regexp = ".*[0-9].*", message = "Hasło musi zawierać co najmniej jedną cyfrę")
    @Pattern(regexp = ".*[A-Z].*", message = "Hasło musi zawierać co najmniej jedną wielką literę")
    protected String password;

}
