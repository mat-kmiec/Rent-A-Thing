package pl.rentathing.Rental.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.rentathing.Rental.Repository.RentalRepository;
import pl.rentathing.Rental.exception.StartAfterEndDateException;
import pl.rentathing.item.entity.Item;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RentalAvailibilityService {

    private final RentalRepository rentalRepository;

    public Boolean checkAvailability(Item item, LocalDateTime start, LocalDateTime end) {

        if(start.isAfter(end)) throw new StartAfterEndDateException();

        LocalDateTime normalizedStart = start.toLocalDate().atStartOfDay();
        LocalDateTime normalizedEnd = end.toLocalDate().atTime(23, 59, 59);

        return !rentalRepository.existsConflictWithBuffer(
                item.getId(), normalizedStart, normalizedEnd);

    }
}
