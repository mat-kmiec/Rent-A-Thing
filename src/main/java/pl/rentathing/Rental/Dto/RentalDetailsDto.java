package pl.rentathing.Rental.Dto;

import lombok.Builder;
import lombok.Data;
import pl.rentathing.Rental.Entity.RentalStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) representing detailed information about a rental transaction.
 *
 * This class encapsulates comprehensive details of a rental transaction, including
 * item information, rental durations, costs, payment, and status. It is primarily
 * used for scenarios where detailed rental information is required, such as rental
 * histories or transaction summaries.
 *
 * Key Fields:
 * - id: Unique identifier for the rental transaction.
 * - itemTitle: The title of the item being rented.
 * - itemImageUrl: URL of the image associated with the item.
 * - itemId: Unique identifier for the item being rented.
 * - startDateTime: Starting date and time of the rental period.
 * - endDateTime: Ending date and time of the rental period.
 * - returnDateTime: Date and time when the item was returned.
 * - totalCost: Total cost of the rental transaction.
 * - deposit: Security deposit amount for the rental.
 * - shippingCost: Cost of shipping the item.
 * - deliveryMethod: Delivery method chosen for the rental.
 * - paymentMethod: Payment method used for the rental.
 * - depositPaid: Indicates if the deposit has been paid (true/false).
 * - status: Current status of the rental, represented by the RentalStatus enum.
 * - handOverNotes: Notes provided during the handover of the item.
 * - returnNotes: Notes provided during the return of the item.
 */
@Data
@Builder
public class RentalDetailsDto {
    private Long id;
    private String itemTitle;
    private String itemImageUrl;
    private Long itemId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private LocalDateTime returnDateTime;
    private BigDecimal totalCost;
    private BigDecimal deposit;
    private BigDecimal shippingCost;
    private String deliveryMethod;
    private String paymentMethod;
    private boolean depositPaid;
    private RentalStatus status;
    private String handOverNotes;
    private String returnNotes;
}