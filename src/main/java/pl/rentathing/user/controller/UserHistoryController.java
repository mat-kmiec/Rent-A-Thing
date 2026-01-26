package pl.rentathing.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.rentathing.Rental.Entity.Rental;
import pl.rentathing.Rental.Service.RentalService;
import pl.rentathing.user.entity.User;

@Controller
@RequestMapping("/moje-konto/historia")
@RequiredArgsConstructor
public class UserHistoryController {

    private final RentalService rentalService;

    @GetMapping
    public String getHistory(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "Wszystkie") String status,
            @RequestParam(required = false, defaultValue = "desc") String sort,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Page<Rental> rentalPage = rentalService.getUserRentalHistory(user, search, status, sort, page);

        model.addAttribute("rentals", rentalPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", rentalPage.getTotalPages());
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("sort", sort);

        return "client/history";
    }
}
