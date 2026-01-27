package pl.rentathing.Rental.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.rentathing.Rental.Repository.RentalRepository;
import pl.rentathing.Rental.exception.PastDateException;
import pl.rentathing.Rental.exception.StartAfterEndDateException;
import pl.rentathing.item.exception.ItemNotFoundException;
import pl.rentathing.item.repository.ItemRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Service responsible for verifying the availability of rental items.
 * This class provides logic to check whether a specific item is available
 * for rent within a given date range, ensuring there are no conflicts
 * with existing rentals.
 */
@Service
@RequiredArgsConstructor
public class RentalAvailibilityService {

    private final RentalRepository rentalRepository;
    private final ItemRepository itemRepository;

    /**
     * Checks whether a specific item is available for rental within the provided date range.
     * Validates the input dates, ensures the item exists, and verifies there are no conflicts
     * with existing rentals for the specified period.
     *
     * @param itemId the unique identifier of the item to check for availability
     * @param startDate the start date for the desired rental period
     * @param endDate the end date for the desired rental period
     * @return true if the item is available for the specified date range, false otherwise
     * @throws ItemNotFoundException if the item with the given ID does not exist
     */
    public boolean isAvailable(Long itemId, LocalDate startDate, LocalDate endDate) {
        validateDates(startDate, endDate);

        if (!itemRepository.existsById(itemId)) {
            throw new ItemNotFoundException(itemId.toString());
        }

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        return !rentalRepository.existsConflictWithBuffer(itemId, start, end);
    }

    /**
     * Validates the provided start and end dates for rental availability.
     * Ensures that the start date is not after the end date and that the start date is not in the past.
     *
     * @param start the start date for the rental period
     * @param end   the end date for the rental period
     * @throws StartAfterEndDateException if the start date is after the end date
     * @throws PastDateException          if the start date is in the past
     */
    private void validateDates(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            throw new StartAfterEndDateException();
        }
        if (start.isBefore(LocalDate.now())) {
            throw new PastDateException();
        }
    }
}
