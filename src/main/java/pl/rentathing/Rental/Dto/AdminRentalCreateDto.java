package pl.rentathing.Rental.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import pl.rentathing.Rental.Entity.DeliveryMethod;
import pl.rentathing.Rental.Entity.PaymentMethod;

import java.time.LocalDate;

@Getter
@Setter
public class AdminRentalCreateDto {

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