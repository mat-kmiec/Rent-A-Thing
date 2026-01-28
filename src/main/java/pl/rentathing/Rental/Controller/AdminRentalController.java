package pl.rentathing.Rental.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.rentathing.Rental.Dto.RentalAdminCreateDto;
import pl.rentathing.Rental.exception.RentalException;
import pl.rentathing.item.dto.ItemSearchDto;
import pl.rentathing.Rental.Dto.RentalAdminListDto;
import pl.rentathing.item.exception.ItemException;
import pl.rentathing.user.dto.UserSearchDto;
import pl.rentathing.Rental.Entity.RentalStatus;
import pl.rentathing.Rental.Service.RentalExportService;
import pl.rentathing.Rental.Service.RentalService;
import pl.rentathing.item.service.CategoryService;
import pl.rentathing.user.exception.UserException;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller responsible for handling operations related to rental management within the admin panel.
 *
 * Provides endpoints for viewing, creating, updating, and managing rentals. Also includes functionalities
 * such as exporting rentals data to CSV and searching for users or items via APIs.
 */
@Controller
@RequestMapping("/admin/rentals")
@RequiredArgsConstructor
public class AdminRentalController {

    private final RentalService rentalService;
    private final CategoryService categoryService;
    private final RentalExportService rentalExportService;

    /**
     * Displays a paginated list of rentals in the admin panel. Provides filtering options
     * based on search query, rental status, category, and return date. The list is sorted
     * by creation date in descending order by default.
     *
     * @param search Optional search keyword to filter rentals by related data.
     * @param status Optional filter to specify the rental status.
     * @param category Optional filter to specify the category of the rental items.
     * @param returnDate Optional filter to specify rentals with a specific return date.
     * @param pageable Pagination information including page size, sort order, and page number.
     * @param model Spring's Model object to add attributes for rendering the view.
     * @return A string representing the name of the view to render, which is "admin/rentals".
     */
    @GetMapping
    public String listRentals(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) RentalStatus status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String returnDate,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {

        Page<RentalAdminListDto> rentals = rentalService.getRentals(search, status, category, returnDate, pageable);

        model.addAttribute("rentals", rentals);
        model.addAttribute("statuses", RentalStatus.values());
        model.addAttribute("categories", categoryService.getAllCategories());

        return "admin/rentals";
    }

    /**
     * Displays details of a specific rental based on the provided rental ID.
     *
     * @param id The unique identifier of the rental to display details for.
     * @param model Spring's Model object used to add attributes for rendering the view.
     * @return A string representing the name of the view to be rendered, which is "admin/rental-details".
     */
    @GetMapping("/{id}")
    public String showDetails(@PathVariable Long id, Model model) {
        model.addAttribute("rental", rentalService.getRentalDetails(id));
        return "admin/rental-details";
    }

    /**
     * Handles the deposit payment for a specific rental.
     * Marks the deposit as paid and redirects to the rental's details page.
     *
     * @param id the ID of the rental for which the deposit is being paid
     * @param ra the RedirectAttributes used to pass flash*/
    @PostMapping("/{id}/pay-deposit")
    public String handleDeposit(@PathVariable Long id, RedirectAttributes ra) {
        rentalService.markDepositAsPaid(id);
        ra.addFlashAttribute("toastType", "success");
        ra.addFlashAttribute("toastMessage", "Kaucja została odnotowana jako wpłacona.");
        return "redirect:/admin/rentals/" + id;
    }

    /**
     * Handles the cancellation of a rental identified by its unique ID.
     * Updates the system state and redirects the user to the appropriate page with a status message.
     *
     * @param id the unique identifier of the rental to be canceled
     * @param ra the RedirectAttributes object used to pass flash attributes to the redirected page
     * @return the redirection URL to the rental details page
     */
    @PostMapping("/{id}/cancel")
    public String handleCancel(@PathVariable Long id, RedirectAttributes ra) {
        rentalService.cancelRental(id);
        ra.addFlashAttribute("toastType", "info");
        ra.addFlashAttribute("toastMessage", "Wypożyczenie zostało anulowane.");
        return "redirect:/admin/rentals/" + id;
    }

    /**
     * Updates the rental status for a specific rental identified by its ID.
     *
     * @param id the ID of the rental whose status is to be updated
     * @param newStatus the new status to be set for the rental
     * @param ra an object used to pass flash attributes for feedback messages
     * @return a redirect URL to the rental details page after the status update
     */
    @PostMapping("/{id}/update-status")
    public String updateStatus(@PathVariable Long id, @RequestParam RentalStatus newStatus, RedirectAttributes ra) {
        try {
            rentalService.updateStatus(id, newStatus);
            ra.addFlashAttribute("toastType", "success");
            ra.addFlashAttribute("toastMessage", "Status zmieniony na: " + newStatus.getDisplayName());
        } catch (Exception e) {
            ra.addFlashAttribute("toastType", "error");
            ra.addFlashAttribute("toastMessage", "Błąd: " + e.getMessage());
        }
        return "redirect:/admin/rentals/" + id;
    }

    /**
     * Exports rental data within a specified date range to a CSV file.
     *
     * @param from the start date of the export range, formatted as ISO_DATE
     * @param to the end date of the export range, formatted as ISO_DATE
     * @return a ResponseEntity containing the generated CSV file as a byte array,
     *         along with appropriate headers for file download
     */
    @GetMapping("/export")
    public Object exportToCsv(
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                               RedirectAttributes ra) {

        if (to.isBefore(from)) {
            ra.addFlashAttribute("toastType", "warning");
            ra.addFlashAttribute("toastMessage", "Data końcowa nie może być wcześniejsza niż początkowa.");
            return "redirect:/admin/rentals";
        }

        try {
            byte[] csvContent = rentalExportService.exportRentalsToCsv(from, to);
            String fileName = String.format("wypozyczenia_%s_do_%s.csv", from, to);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                    .body(csvContent);
        } catch (Exception e) {
            ra.addFlashAttribute("toastType", "error");
            ra.addFlashAttribute("toastMessage", "Błąd generowania raportu.");
            return "redirect:/admin/rentals";
        }
    }

    /**
     * Handles the return process for a specific rental item.
     *
     * @param id the ID of the rental item to be returned
     * @param returnNotes optional notes regarding the return process
     * @param ra RedirectAttributes used to pass flash attributes such as success or error messages
     * @return a redirect URL to the rental details page
     */
    @PostMapping("/{id}/return")
    public String handleReturn(@PathVariable Long id, @RequestParam(required = false) String returnNotes, RedirectAttributes ra) {
        try {
            rentalService.processReturn(id, returnNotes);
            ra.addFlashAttribute("toastType", "success");
            ra.addFlashAttribute("toastMessage", "Przedmiot został pomyślnie odebrany.");
        } catch (Exception e) {
            ra.addFlashAttribute("toastType", "error");
            ra.addFlashAttribute("toastMessage", "Błąd zwrotu: " + e.getMessage());
        }
        return "redirect:/admin/rentals/" + id;
    }

    /**
     * Handles the HTTP GET request for displaying the rental creation form.
     * Adds an empty RentalAdminCreateDto object to the model to populate the form.
     *
     * @param model the model object used to pass data to the view
     * @return a string representing the view name for the rental creation form
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("rentalDto")) {
            model.addAttribute("rentalDto", new RentalAdminCreateDto());
        }
        return "admin/rental-create";
    }

    /**
     * Processes the form submission for creating a new rental.
     *
     * @param dto the data transfer object containing the rental details
     * @param result the binding result capturing validation errors
     * @param model the model to populate attributes for the view
     * @param redirectAttributes the redirect attributes for flash messages
     * @return the view name to be rendered or redirect URL
     */
    @PostMapping("/create")
    public String processCreateForm(@Valid @ModelAttribute("rentalDto") RentalAdminCreateDto dto,
                                    BindingResult result,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.rentalDto", result);
            redirectAttributes.addFlashAttribute("rentalDto", dto);
            redirectAttributes.addFlashAttribute("toastType", "warning");
            redirectAttributes.addFlashAttribute("toastMessage", "Popraw błędy w formularzu.");

            return "redirect:/admin/rentals/create";
        }

        try {
            rentalService.createRentalByAdmin(dto);
            redirectAttributes.addFlashAttribute("toastType", "success");
            redirectAttributes.addFlashAttribute("toastMessage", "Pomyślnie utworzono nowe wypożyczenie.");

            return "redirect:/admin/rentals";

        } catch (RentalException | ItemException | UserException e) {
            redirectAttributes.addFlashAttribute("toastType", "error");
            redirectAttributes.addFlashAttribute("toastMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("rentalDto", dto);

            return "redirect:/admin/rentals/create";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("toastType", "error");
            redirectAttributes.addFlashAttribute("toastMessage", "Wystąpił nieoczekiwany błąd serwera.");
            redirectAttributes.addFlashAttribute("rentalDto", dto);

            return "redirect:/admin/rentals/create";
        }
    }

    /**
     * Searches for users based on the provided query string.
     *
     * @param query the search query used to find matching users
     * @return a ResponseEntity containing a list of UserSearchDto objects that match the search criteria
     */
    @GetMapping("/api/users/search")
    @ResponseBody
    public ResponseEntity<List<UserSearchDto>> searchUsers(@RequestParam String query) {
        return ResponseEntity.ok(rentalService.searchUsersForAdmin(query));
    }

    /**
     * Handles the search operation for items based on the given query string.
     *
     * @param query the search text used to find items
     * @return a ResponseEntity containing a list of ItemSearchDto objects matching the search criteria
     */
    @GetMapping("/api/items/search")
    @ResponseBody
    public ResponseEntity<List<ItemSearchDto>> searchItems(@RequestParam String query) {
        return ResponseEntity.ok(rentalService.searchItemsForAdmin(query));
    }
}
