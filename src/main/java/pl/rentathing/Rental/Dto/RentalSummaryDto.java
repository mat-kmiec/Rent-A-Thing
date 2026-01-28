package pl.rentathing.Rental.Dto;

import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) representing a summary of rental details.
 *
 * This class encapsulates essential information about a rental transaction,
 * focusing on key summary attributes such as the rental ID, title of the item rented,
 * total cost, payment method, and the current rental status. It is suitable for
 * scenarios where a concise overview of rental transactions is required.
 *
 * Key Fields:
 * - rentalId: The unique identifier of the rental transaction.
 * - itemTitle: The title of the item associated with the rental.
 * - totalCost: The total cost incurred for the rental.
 * - paymentMethod: The payment method used for the rental transaction.
 * - status: The current status of the rental (e.g., active, completed).
 */
public record RentalSummaryDto(
        Long rentalId,
        String itemTitle,
        BigDecimal totalCost,
        String paymentMethod,
        String status
) {}
