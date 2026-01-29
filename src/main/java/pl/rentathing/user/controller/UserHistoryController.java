package pl.rentathing.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.rentathing.Rental.Dto.RentalDetailsDto;
import pl.rentathing.Rental.Dto.RentalHistoryDto;
import pl.rentathing.Rental.Entity.Rental;
import pl.rentathing.Rental.Service.RentalService;
import pl.rentathing.user.entity.User;

/**
 * Controller responsible for handling user's rental history views and related operations
 * such as filtering, sorting, and retrieving detailed information about a specific rental.
 */
@Controller
@RequestMapping("/moje-konto/historia")
@RequiredArgsConstructor
public class UserHistoryController {

    private final RentalService rentalService;

    /**
     * Handles the HTTP GET request to retrieve the rental history of the authenticated user,
     * including optional filters for searching, status, sorting, and pagination, and prepares
     * the model with the necessary data to display the history view.
     *
     * @param user the authenticated {@code User} making the request
     * @param search an optional search query to filter the rental history (nullable)
     * @param status an optional filter for the rental status, defaulting to "Wszystkie" if not provided
     * @param sort the sorting order, either "asc" or "desc", defaulting to "desc" if not provided
     * @param page the page number to retrieve for paginated results, defaulting to 0
     * @param model the {@code Model} used to populate attributes for the view
     * @return the name of the view template to render, specifically "client/history"
     */
    @GetMapping
    public String getHistory(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "Wszystkie") String status,
            @RequestParam(required = false, defaultValue = "desc") String sort,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Page<RentalHistoryDto> rentalPage = rentalService.getUserRentalHistory(user, search, status, sort, page);

        model.addAttribute("rentals", rentalPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", rentalPage.getTotalPages());
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("sort", sort);

        return "client/history";
    }

    /**
     * Fetches and displays rental details based on the rental ID and authenticated user.
     *
     * @param id the ID of the rental to retrieve details for
     * @param user the currently authenticated user requesting the rental details
     * @param model the model to which the rental details are added for rendering in the view
     * @return the name of the view template that displays the rental details
     */
    @GetMapping("/szczegoly/{id}")
    public String getRentalDetails(@PathVariable Long id,
                                   @AuthenticationPrincipal User user,
                                   Model model) {
        RentalDetailsDto rental = rentalService.getRentalDetails(id, user);
        model.addAttribute("rental", rental);
        return "client/history-details";
    }
}
