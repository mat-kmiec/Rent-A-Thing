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

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    protected String email;

    @NotBlank(message = "Password cannot be blank")
    protected String password;
}
