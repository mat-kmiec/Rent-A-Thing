package pl.rentathing.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {

    @NotBlank(message = "Ulica jest wymagana")
    @Size(min = 2, max = 100, message = "Ulica musi mieć od 2 do 100 znaków")
    @Pattern(regexp = "^[a-zA-ZąęćńółśźżĄĘĆŃÓŁŚŹŻ\\s\\.\\-]+$", message = "Ulica może zawierać tylko litery, spacje, kropki i myślniki")
    private String street;

    @NotBlank(message = "Numer domu jest wymagany")
    @Size(max = 10, message = "Numer domu nie może być dłuższy niż 10 znaków")
    private String houseNumber;

    @Size(max = 10, message = "Numer lokalu nie może być dłuższy niż 10 znaków")
    private String apartmentNumber;

    @NotBlank(message = "Miasto jest wymagane")
    @Size(min = 2, max = 100, message = "Miasto musi mieć od 2 do 100 znaków")
    @Pattern(regexp = "^[a-zA-ZąęćńółśźżĄĘĆŃÓŁŚŹŻ\\s\\-]+$", message = "Miasto może zawierać tylko litery, spacje i myślniki")
    private String city;

    @NotBlank(message = "Kod pocztowy jest wymagany")
    @Pattern(regexp = "^\\d{2}-\\d{3}$", message = "Kod pocztowy musi być w formacie XX-XXX")
    private String zipCode;
}