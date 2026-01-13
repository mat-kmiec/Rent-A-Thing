package pl.rentathing.auth.dto;


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
public class RegisterRequest extends AuthRequest{

    @NotBlank(message = "Username cannot be blank")
    private String firstName;

    @NotBlank(message = "Username cannot be blank")
    private String lastName;
}
