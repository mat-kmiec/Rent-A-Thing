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
