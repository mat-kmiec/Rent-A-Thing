package pl.rentathing.Rental.Dto;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) that represents details of an active rental.
 *
 * This class provides information about a rental that is currently active,
 * including the associated item, return deadlines, and notes. It is primarily
 * used to present or manage ongoing rental details in systems handling rentals.
 *
 * Fields:
 * - id: Unique identifier of the active rental.
 * - itemTitle: The title or name of the item being rented.
 * - imageUrl: URL of the item's image.
 * - requestNumber: The unique request number associated with the rental.
 * - returnDeadline: The deadline for returning the rented item.
 * - remainingTimeText: Human-readable representation of the time remaining to return the item.
 * - returnNote: Notes or instructions related to the return process.
 */
@Builder
public record ActiveRentalDTO(
        Long id,
        String itemTitle,
        String imageUrl,
        String requestNumber,
        LocalDateTime returnDeadline,
        String remainingTimeText,
        String returnNote
) {}