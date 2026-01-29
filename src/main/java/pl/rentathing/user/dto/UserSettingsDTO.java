package pl.rentathing.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.Valid;

/**
 * Data Transfer Object (DTO) for transferring and validating user-specific settings.
 *
 * This class encompasses information about user preferences and allows data validation
 * through annotations for fields such as names, email, passwords, and contact information.
 *
 * Fields:
 * - id: Unique identifier of the user settings record.
 * - firstName: The user's first name. It must adhere to validation rules such as size
 *   constraints and acceptable character patterns.
 * - lastName: The user's last name. It is subject to validation for length and character rules.
 * - email: The user's email address, which must be in a valid format and cannot exceed a
 *   certain length.
 * - newPassword: A new password that the user wishes to set. It must meet minimum length requirements.
 * - currentPassword: The user's current password, used to authenticate changes to sensitive information.
 * - confirmPassword: A confirmation of the new password to ensure consistency.
 * - phoneNumber: The user's phone number, validated based on specific patterns for standard formats.
 * - notifEmail: A flag indicating whether the user has opted in for email notifications.
 * - notifSms: A flag indicating whether the user has opted in for SMS notifications.
 * - newsletter: A flag indicating whether the user has subscribed to newsletters.
 * - address: An instance of AddressDTO representing the user's address information.
 *   This field is validated as an embedded object.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSettingsDTO {
    private Long id;

    @NotBlank(message = "Imię nie może być puste")
    @Size(min = 2, max = 50, message = "Imię musi zawierać od 2 do 50 znaków")
    @Pattern(regexp = "^[a-zA-ZąęćńółśźżĄĘĆŃÓŁŚŹŻ\\s\\-]+$", message = "Imię może zawierać tylko litery, spacje i myślniki")
    private String firstName;

    @NotBlank(message = "Nazwisko nie może być puste")
    @Size(min = 2, max = 50, message = "Nazwisko musi zawierać od 2 do 50 znaków")
    @Pattern(regexp = "^[a-zA-ZąęćńółśźżĄĘĆŃÓŁŚŹŻ\\s\\-]+$", message = "Nazwisko może zawierać tylko litery, spacje i myślniki")
    private String lastName;

    @Email(message = "Nieprawidłowy format emaila")
    @NotBlank(message = "Email nie może być pusty")
    @Size(max = 100, message = "Email nie może być dłuższy niż 100 znaków")
    private String email;

    @Size(min = 8, message = "Hasło musi mieć minimum 8 znaków")
    private String newPassword;

    private String currentPassword;

    private String confirmPassword;

    @Pattern(regexp = "^(\\+48)?\\s?\\d{3}\\s?\\d{3}\\s?\\d{3}$|^\\d{9}$", message = "Nieprawidłowy format numeru telefonu (np. 123456789 lub +48 123 456 789)")
    private String phoneNumber;

    private Boolean notifEmail;
    private Boolean notifSms;
    private Boolean newsletter;

    @Valid
    private AddressDTO address;
}
