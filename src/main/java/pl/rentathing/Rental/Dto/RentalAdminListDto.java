package pl.rentathing.Rental.Dto;

import lombok.Builder;
import lombok.Getter;
import pl.rentathing.Rental.Entity.RentalStatus;

import java.time.LocalDateTime;

/**
 * Represents a Data Transfer Object (DTO) designed for administrative purposes in managing rental entries.
 *
 * This class provides essential details required for displaying and interacting with a list of rental entries
 * within an administrative interface or other relevant contexts.
 *
 * The included fields capture information about the rental, associated item, customer details,
 * rental duration, status, and other metadata that are pertinent for administrative decision-making
 * or listing purposes.
 *
 * Key details provided in this DTO include:
 * - id: Identifier of the rental.
 * - itemTitle: The title of the rented item.
 * - categoryIcon: An icon or representation indicating the category of the item.
 * - userFullName: The full name of the customer associated with the rental.
 * - userInitials: Initials of the customer's name.
 * - endDateTime: The end date and time of the rental period.
 * - status: Current status of the rental (e.g., NEW, ACTIVE, COMPLETED).
 * - timeDiffMessage: A message describing the time difference (e.g., remaining or overdue days).
 * - isOverdue: Boolean flag indicating whether the rental is overdue.
 */
@Getter
@Builder
public class RentalAdminListDto {
    private Long id;
    private String itemTitle;
    private String categoryIcon;
    private String userFullName;
    private String userInitials;
    private LocalDateTime endDateTime;
    private RentalStatus status;
    private String timeDiffMessage;
    private boolean isOverdue;
}