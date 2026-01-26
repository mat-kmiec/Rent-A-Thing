package pl.rentathing.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.rentathing.Rental.Entity.Rental;
import pl.rentathing.Rental.Entity.RentalStatus;
import pl.rentathing.Rental.Repository.RentalRepository;

import java.util.List;

@Controller
@RequestMapping("/admin/rentals")
@RequiredArgsConstructor
public class AdminRentalController {

    private final RentalRepository rentalRepository;

    @GetMapping
    public String getRentals(Model model) {
        List<Rental> activeRentals = rentalRepository.findByStatus(RentalStatus.ACTIVE);
        // Assuming PENDING should also be visible somewhere? Let's add them to active
        // for now or separate?
        // The UI has "Active". Usually active means currently in possession.
        // Let's stick to RentalStatus.ACTIVE as per my previous thought.

        List<Rental> overdueRentals = rentalRepository.findByStatus(RentalStatus.OVERDUE);
        List<Rental> historyRentals = rentalRepository.findByStatusIn(
                List.of(RentalStatus.COMPLETED, RentalStatus.CANCELLED, RentalStatus.DAMAGED));

        model.addAttribute("activeRentals", activeRentals);
        model.addAttribute("overdueRentals", overdueRentals);
        model.addAttribute("historyRentals", historyRentals);

        return "admin/rentals";
    }
}
