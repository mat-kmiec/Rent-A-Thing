package pl.rentathing.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressDTO {

    @NotBlank(message = "Ulica jest wymagana")
    private String street;

    @NotBlank(message = "Numer domu jest wymagany")
    private String houseNumber;

    private String apartmentNumber;

    @NotBlank(message = "Miasto jest wymagane")
    private String city;

    @NotBlank(message = "Kod pocztowy jest wymagany")
    @Pattern(regexp = "^\\d{2}-\\d{3}$", message = "Kod pocztowy musi być w formacie XX-XXX")
    private String zipCode;
}