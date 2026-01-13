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

    @NotBlank(message = "Imię nie moze być puste")
    private String firstName;

    @NotBlank(message = "Nazwisko nie moze być puste")
    private String lastName;
}
