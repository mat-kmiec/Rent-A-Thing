package pl.rentathing.Rental.Entity;

public enum RentalStatus {
    PENDING("Oczekujące", "bg-warning text-dark"),
    ACTIVE("W trakcie", "bg-success text-success"),
    COMPLETED("Zakończone", "bg-secondary text-secondary"),
    CANCELLED("Anulowane", "bg-danger text-danger"),
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
