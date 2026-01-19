package pl.rentathing.user.dto;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;

@Data
@Builder
public class UserSettingsDTO {
    private Long id;

    @NotBlank(message = "Imię nie może być puste")
    @Size(min = 2, max = 50, message = "Imię musi zawierać od 2 do 50 znaków")
    private String firstName;

    @NotBlank(message = "Nazwisko nie może być puste")
    @Size(min = 2, max = 50, message = "Nazwisko musi zawierać od 2 do 50 znaków")
    private String lastName;

    @Email(message = "Nieprawidłowy format emaila")
    @NotBlank(message = "Email nie może być pusty")
    private String email;

    @Size(min = 8, message = "Hasło musi mieć minimum 8 znaków")
    private String newPassword;

    private String currentPassword;

    @Valid
    private AddressDTO address;
}
