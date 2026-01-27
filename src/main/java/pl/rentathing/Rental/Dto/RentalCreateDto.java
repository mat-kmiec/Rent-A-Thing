package pl.rentathing.Rental.Dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Represents a Data Transfer Object (DTO) for creating a new rental.
 *
 * This class is used to encapsulate data required for initiating a rental operation.
 * It includes information about the item, rental dates, customer details,
 * delivery preferences, and payment methods.
 *
 * Fields:
 * - itemId: The unique identifier of the item to be rented.
 * - startDate: The start date of the rental period. Must be the current or a future date.
 * - endDate: The end date of the rental period.
 * - firstName: The first name of the customer. This field is mandatory.
 * - lastName: The last name of the customer. This field is mandatory.
 * - email: The email address of the customer, validated for proper format.
 * - deliveryMethod: The delivery method for the rented item, selected by the customer.
 * - payment: The selected payment method for the rental.
 */
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