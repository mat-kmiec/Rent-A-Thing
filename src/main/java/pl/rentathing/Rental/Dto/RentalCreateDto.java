package pl.rentathing.Rental.Dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class RentalCreateDto {

    @NotNull
    private Long itemId;

    @NotNull
    @FutureOrPresent(message = "Data rozpoczęcia nie może być z przeszłości")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;


    @NotBlank(message = "Imię jest wymagane")
    private String firstName;

    @NotBlank(message = "Nazwisko jest wymagane")
    private String lastName;

    @Email(message = "Błędny format email")
    @NotBlank
    private String email;

//    @NotBlank(message = "Telefon jest wymagany")
//    private String phone;

//    @NotBlank(message = "Adres jest wymagany")
//    private String address;
//
//    @NotBlank(message = "Miasto jest wymagane")
//    private String city;
//
//    @NotBlank(message = "Kod pocztowy jest wymagany")
//    private String zipCode;

    @NotBlank(message = "Wybierz sposób dostawy")
    private String deliveryMethod;

    @NotBlank
    private String payment;

}