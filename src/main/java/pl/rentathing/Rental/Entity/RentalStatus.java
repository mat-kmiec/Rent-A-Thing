package pl.rentathing.Rental.Entity;

import lombok.Getter;

/**
 * Represents the status of a rental transaction.
 *
 * This enumeration is used to define and categorize the various states
 * a rental can have within the system. Each status provides descriptive
 * labels and corresponding style classes for UI representation.
 *
 * The available statuses include:
 * - NEW: Indicates a newly created rental (not yet processed).
 * - PENDING: Represents a rental that is awaiting action or confirmation.
 * - ACTIVE: Specifies that the rental is currently in progress.
 * - COMPLETED: Denotes a rental that has been concluded successfully.
 * - CANCELLED: Identifies a rental that was cancelled.
 * - OVERDUE: Represents a rental that has surpassed its return date.
 * - DAMAGED: Highlights a rental where the returned item is reported as damaged.
 *
 * Each status is associated with a display name for localization purposes
 * and a badge class for visual styling in the user interface.
 */
@Getter
public enum RentalStatus {
    NEW("Nowe", "bg-info bg-opacity-10 text-info"),
    PENDING("Oczekujące", "bg-warning text-dark"),
    ACTIVE("W trakcie", "bg-primary bg-opacity-10 text-primary"),
    COMPLETED("Zakończone", "bg-success bg-opacity-10 text-success"),
    CANCELLED("Anulowane", "bg-secondary text-white"),
    OVERDUE("Po terminie", "bg-danger text-white"),
    DAMAGED("Uszkodzone", "bg-dark text-white");

    private final String displayName;
    private final String badgeClass;

    RentalStatus(String displayName, String badgeClass) {
        this.displayName = displayName;
        this.badgeClass = badgeClass;
    }

}
