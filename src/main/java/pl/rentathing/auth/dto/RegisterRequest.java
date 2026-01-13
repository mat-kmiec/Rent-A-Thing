package pl.rentathing.auth.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Represents a registration request containing user details required for creating a new account.
 * Extends {@code AuthRequest} to include additional fields specific to registration.
 *
 * The {@code RegisterRequest} class ensures that all required fields for registration
 * are validated according to predefined rules.
 *
 * Fields include:
 * - {@code firstName}: The user's first name with validation for non-empty
 *   input and length constraints.
 * - {@code lastName}: The user's last name with validation for non-empty input
 *   and length constraints.
 * - {@code confirmPassword}: A field to confirm the user's password with validation
 *   for non-empty input.
 *
 * This class uses annotations for validation and leverages Lombok to reduce boilerplate code.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RegisterRequest extends AuthRequest{

    @NotBlank(message = "Imię nie moze być puste")
    @Size(min = 2, max = 50, message = "Imie musi mieć od 2 do 50 znaków")
    private String firstName;

    @NotBlank(message = "Nazwisko nie moze być puste")
    @Size(min = 2, max = 50, message = "Nazwisko musi mieć od 2 do 50 znaków")
    private String lastName;

    @NotBlank(message = "Pole nie może być puste!")
    private String confirmPassword;
}
