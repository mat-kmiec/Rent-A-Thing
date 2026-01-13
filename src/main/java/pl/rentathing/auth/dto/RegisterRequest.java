package pl.rentathing.auth.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @Size(min = 2, max = 50, message = "Imie musi mieć od 2 do 50 znaków")
    private String firstName;

    @NotBlank(message = "Nazwisko nie moze być puste")
    @Size(min = 2, max = 50, message = "Nazwisko musi mieć od 2 do 50 znaków")
    private String lastName;

    @NotBlank(message = "Pole nie może być puste!")
    private String confirmPassword;
}
