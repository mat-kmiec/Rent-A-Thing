package pl.rentathing.Rental.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.rentathing.Rental.Repository.RentalRepository;
import pl.rentathing.Rental.exception.PastDateException;
import pl.rentathing.Rental.exception.StartAfterEndDateException;
import pl.rentathing.item.entity.Item;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RentalAvailibilityService {

    private final RentalRepository rentalRepository;

    public boolean isAvailable(Item item, LocalDate startDate, LocalDate endDate) {
        validateDates(startDate, endDate);

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        return !rentalRepository.existsConflictWithBuffer(item.getId(), start, end);
    }

    private void validateDates(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            throw new StartAfterEndDateException();
        }
        if (start.isBefore(LocalDate.now())) {
            throw new PastDateException();
        }
    }
}
