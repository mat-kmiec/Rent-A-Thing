package pl.rentathing.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class AuthRequest {

    @NotBlank(message = "Email nie może być pusty")
    @Email(message = "Email must be valid")
    protected String email;

    @NotBlank(message = "Hasło nie może być puste")
    protected String password;
}
