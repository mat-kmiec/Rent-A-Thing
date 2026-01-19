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
import pl.rentathing.Rental.Service.RentalService;
import pl.rentathing.Rental.exception.DateNotAvailableException;
import pl.rentathing.Rental.exception.StartAfterEndDateException;
import pl.rentathing.auth.service.AuthService;
import pl.rentathing.item.dto.ItemDetailsDTO;
import pl.rentathing.item.service.ItemService;
import pl.rentathing.user.entity.User;
import pl.rentathing.user.repository.UserRepository;

import java.time.LocalDate;

@Controller
@RequestMapping("/wypozyczenia")
@RequiredArgsConstructor
public class RentalController {

    private final ItemService itemService;
    private final RentalService rentalService;
    private final UserRepository userRepository;
    private final AuthService authService;

    @GetMapping("/formularz")
    public String showRentalForm(
            @RequestParam(name = "przedmiot") Long itemId,
            @RequestParam(name = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "koniec", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model
    ) {
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


    @PostMapping("/potwierdz")
    public String confirmRental(
            @Valid @ModelAttribute("rentalCreateDto") RentalCreateDto rentalCreateDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.rentalCreateDto", bindingResult);
            redirectAttributes.addFlashAttribute("rentalCreateDto", rentalCreateDto);
            redirectAttributes.addFlashAttribute("toastType", "warning");
            redirectAttributes.addFlashAttribute("toastMessage", "Popraw błędy w formularzu.");

            return "redirect:/wypozyczenia/formularz?przedmiot=" + rentalCreateDto.getItemId();
        }

        try {
            rentalService.createRental(rentalCreateDto);
            redirectAttributes.addFlashAttribute("toastType", "success");
            redirectAttributes.addFlashAttribute("toastMessage", "Wypożyczenie zostało utworzone!");
            return "redirect:/wypozyczenia/sukces";

        } catch (DateNotAvailableException | StartAfterEndDateException e) {
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