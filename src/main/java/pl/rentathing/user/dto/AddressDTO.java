package pl.rentathing.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for storing and transferring address-related data.
 *
 * This class serves as a simple container for address information and includes
 * validation constraints to ensure the integrity of the data. It is typically
 * used for communication between different layers of the application or as a
 * part of other complex objects.
 *
 * Fields:
 * - street: The name of the street. Must meet validation rules such as length and character constraints.
 * - houseNumber: The number of the house. Subject to length validation rules.
 * - apartmentNumber: The number of the apartment (optional). Limited by maximum length constraints.
 * - city: The name of the city. Validated with length and character rules.
 * - zipCode: The postal code, which must conform to a specific format (e.g., XX-XXX).
 */
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