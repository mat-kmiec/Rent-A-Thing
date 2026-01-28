package pl.rentathing.Rental.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import pl.rentathing.Rental.Entity.DeliveryMethod;
import pl.rentathing.Rental.Entity.PaymentMethod;

import java.time.LocalDate;

/**
 * Represents a Data Transfer Object (DTO) used for creating a new rental by an administrator.
 *
 * This class is utilized to capture the necessary details required for creating a rental,
 * including identifying information about the user and the item, the rental period,
 * delivery and payment preferences, and an optional flag for marking the deposit as paid.
 *
 * The following fields are included:
 * - userId: The ID of the user who is renting the item. This field is mandatory.
 * - itemId: The ID of the item being rented. This field is mandatory.
 * - startDate: The starting date for the rental period. This field is mandatory.
 * - endDate: The ending date for the rental period. This field is mandatory.
 * - deliveryMethod: Specifies the delivery method for the rental. Defaults to 'PICKUP'.
 * - paymentMethod: Specifies the payment method for the rental. Defaults to 'CASH'.
 * - markDepositPaid: A boolean flag indicating whether the deposit is marked as paid. Defaults to false.
 */
@Getter
@Setter
public class RentalAdminCreateDto {

    @NotNull(message = "Musisz wybrać klienta")
    private Long userId;

    @NotNull(message = "Musisz wybrać przedmiot")
    private Long itemId;

    @NotNull(message = "Data rozpoczęcia jest wymagana")
    private LocalDate startDate;

    @NotNull(message = "Data zakończenia jest wymagana")
    private LocalDate endDate;

    private DeliveryMethod deliveryMethod = DeliveryMethod.PICKUP;
    private PaymentMethod paymentMethod = PaymentMethod.CASH;

    private boolean markDepositPaid = false;
}