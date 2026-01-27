package pl.rentathing.Rental.Entity;

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

    public String getDisplayName() { return displayName; }
    public String getBadgeClass() { return badgeClass; }
}
