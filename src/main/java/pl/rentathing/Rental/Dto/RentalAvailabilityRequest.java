package pl.rentathing.Rental.Dto;
import java.time.LocalDateTime;

/**
 * Represents a request to check rental availability for a specific item.
 *
 * This record is used to provide the necessary details for determining whether
 * a rental item can be reserved for a given time period.
 *
 * The parameters include:
 * - itemId: The unique identifier of the item being requested for rental.
 * - start: The start date and time of the rental period.
 * - end: The end date and time of the rental period.
 */
public record RentalAvailabilityRequest(
        Long itemId,
        LocalDateTime start,
        LocalDateTime end
) {}
