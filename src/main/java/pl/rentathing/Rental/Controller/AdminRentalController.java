package pl.rentathing.Rental.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.rentathing.Rental.Dto.RentalAdminListDto;
import pl.rentathing.Rental.Entity.Rental;
import pl.rentathing.Rental.Entity.RentalStatus;
import pl.rentathing.Rental.Service.RentalService;
import pl.rentathing.item.service.CategoryService;

@Controller
@RequestMapping("/admin/rentals")
@RequiredArgsConstructor
public class AdminRentalController {

    private final RentalService rentalService;
    private final CategoryService categoryService;

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

    @GetMapping("/{id}")
    public String showDetails(@PathVariable Long id, Model model) {
        model.addAttribute("rental", rentalService.getRentalDetails(id));
        return "admin/rental-details";
    }

    @PostMapping("/{id}/pay-deposit")
    public String handleDeposit(@PathVariable Long id, RedirectAttributes ra) {
        rentalService.markDepositAsPaid(id);
        ra.addFlashAttribute("success", "Kaucja została odnotowana jako wpłacona.");
        return "redirect:/admin/rentals/" + id;
    }

    @PostMapping("/{id}/cancel")
    public String handleCancel(@PathVariable Long id, RedirectAttributes ra) {
        rentalService.cancelRental(id);
        ra.addFlashAttribute("info", "Wypożyczenie zostało anulowane.");
        return "redirect:/admin/rentals/" + id;
    }

    @PostMapping("/{id}/update-status")
    public String updateStatus(@PathVariable Long id, @RequestParam RentalStatus newStatus, RedirectAttributes ra) {
        try {
            rentalService.updateStatus(id, newStatus);
            ra.addFlashAttribute("success", "Status wypożyczenia został zmieniony na: " + newStatus.getDisplayName());
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Nie udało się zmienić statusu.");
        }
        return "redirect:/admin/rentals/" + id;
    }
}
