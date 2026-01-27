package pl.rentathing.Rental.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.rentathing.Rental.Service.RentalAvailibilityService;

import java.time.LocalDate;

/**
 * REST controller for managing rental operations.
 * Provides endpoints related to rental processes, such as checking the availability
 * of items for rent within specific date ranges.
 */
@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalApiController {


    private final RentalAvailibilityService rentalAvailibilityService;

    /**
     * Checks the availability of a specific item for rental within a given date range.
     *
     * @param itemId the unique identifier of the item to check for availability
     * @param startDate the start date for the desired rental period, in ISO-8601 format
     * @param endDate the end date for the desired rental period, in ISO-8601 format
     * @return a ResponseEntity containing true if the item is available for the specified date range, false otherwise
     */
    @GetMapping("/{itemId}/availability")
    public ResponseEntity<Boolean> checkAvailability(
            @PathVariable Long itemId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ResponseEntity.ok(rentalAvailibilityService.isAvailable(itemId, startDate, endDate));
    }
}
