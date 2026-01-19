package pl.rentathing.Rental.Dto;
import java.time.LocalDateTime;

public record RentalAvailabilityRequest(
        Long itemId,
        LocalDateTime start,
        LocalDateTime end
) {}
