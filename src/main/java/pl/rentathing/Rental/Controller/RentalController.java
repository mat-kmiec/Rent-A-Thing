package pl.rentathing.Rental.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.rentathing.Rental.Dto.RentalCreateDto;
import pl.rentathing.Rental.Dto.RentalSummaryDto;
import pl.rentathing.Rental.Service.RentalService;
import pl.rentathing.auth.service.AuthService;
import pl.rentathing.item.dto.ItemDetailsDTO;
import pl.rentathing.item.service.ItemService;
import pl.rentathing.Rental.exception.RentalException;
import pl.rentathing.item.exception.ItemException;
import pl.rentathing.user.entity.User;
import pl.rentathing.user.exception.UserException;
import pl.rentathing.user.repository.UserRepository;

import java.time.LocalDate;

/**
 * Controller for handling rental-related operations.
 * Provides endpoints for displaying the rental form, confirming rental requests,
 * and showing success messages after successful rentals.
 * Utilizes services for item management, authentication, and rental processing.
 */
@Controller
@RequestMapping("/wypozyczenia")
@RequiredArgsConstructor
public class RentalController {

    private final ItemService itemService;
    private final RentalService rentalService;
    private final UserRepository userRepository;
    private final AuthService authService;

    /**
     * Handles GET requests to display the rental form for a specific item.
     * Prepares and populates the necessary data for the rental form, including item details,
     * rental information, and the current user.
     *
     * @param itemId the ID of the item being rented
     * @param startDate the optional start date of the rental, formatted as ISO date
     * @param endDate the optional end date of the rental, formatted as ISO date
     * @param model the model used to add attributes for rendering the view
     * @return the name of the view template for the rental form, or a redirect URL if the item is not available
     */
    @GetMapping("/formularz")
    public String showRentalForm(
            @RequestParam(name = "przedmiot") Long itemId,
            @RequestParam(name = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "koniec", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {
        if (!model.containsAttribute("rentalCreateDto")) {
            RentalCreateDto rentalCreateDto = rentalService.prepareRentalDto(itemId, startDate, endDate);
            model.addAttribute("rentalCreateDto", rentalCreateDto);
        }

        ItemDetailsDTO itemDto = itemService.getItemDetails(itemId);
        if (!itemDto.getAvailable()) {
            return "redirect:/catalog/details?id=" + itemId + "&error=not_available";
        }

        User currentUser = authService.getCurrentUser();
        model.addAttribute("item", itemDto);
        model.addAttribute("user", currentUser);

        return "rental/form";
    }

    /**
     * Handles the request to display the rental success page.
     * If the required rental summary attribute is not present in the model,
     * it redirects to the catalog page instead.
     *
     * @param model the model containing attributes passed to the view
     * @return the name of the view to be resolved, either the success page
     *         or redirection to the catalog page
     */
    @GetMapping("/sukces")
    public String showRentalSuccess(Model model) {
        if (!model.containsAttribute("rentalSummary")) {
            return "redirect:/katalog";
        }
        return "rental/succes";
    }

    /**
     * Handles the confirmation of a rental process by validating the input data,
     * processing the rental creation, and redirecting to the appropriate view based on the outcome.
     *
     * @param rentalCreateDto The data transfer object containing the details of the rental to be created.
     * @param bindingResult The object that holds the result of the validation and potential validation errors.
     * @param redirectAttributes The object used to add flash attributes to the redirect response.
     * @return A string representing the redirection URL, which varies depending on the validation status
     *         and result of the rental creation process.
     */
    @PostMapping("/potwierdz")
    public String confirmRental(
            @Valid @ModelAttribute("rentalCreateDto") RentalCreateDto rentalCreateDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.rentalCreateDto",
                    bindingResult);
            redirectAttributes.addFlashAttribute("rentalCreateDto", rentalCreateDto);
            redirectAttributes.addFlashAttribute("toastType", "warning");
            redirectAttributes.addFlashAttribute("toastMessage", "Popraw błędy w formularzu.");

            return "redirect:/wypozyczenia/formularz?przedmiot=" + rentalCreateDto.getItemId();
        }

        try {
            RentalSummaryDto summary = rentalService.createRental(rentalCreateDto);
            redirectAttributes.addFlashAttribute("rentalSummary", summary);
            return "redirect:/wypozyczenia/sukces";

        } catch (RentalException | ItemException | UserException e) {
            redirectAttributes.addFlashAttribute("toastType", "error");
            redirectAttributes.addFlashAttribute("toastMessage", e.getMessage());

            return String.format("redirect:/wypozyczenia/formularz?przedmiot=%d&start=%s&koniec=%s",
                    rentalCreateDto.getItemId(),
                    rentalCreateDto.getStartDate(),
                    rentalCreateDto.getEndDate());

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("toastType", "error");
            redirectAttributes.addFlashAttribute("toastMessage", "Wystąpił nieoczekiwany błąd.");
            return "redirect:/wypozyczenia/formularz?przedmiot=" + rentalCreateDto.getItemId();
        }
    }

}