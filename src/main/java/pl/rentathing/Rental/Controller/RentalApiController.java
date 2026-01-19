package pl.rentathing.Rental.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.rentathing.Rental.Service.RentalAvailibilityService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalApiController {


    private final RentalAvailibilityService rentalAvailibilityService;

    @GetMapping("/{itemId}/availability")
    public ResponseEntity<Boolean> checkAvailability(
            @PathVariable Long itemId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ResponseEntity.ok(rentalAvailibilityService.isAvailable(itemId, startDate, endDate));
    }
}
