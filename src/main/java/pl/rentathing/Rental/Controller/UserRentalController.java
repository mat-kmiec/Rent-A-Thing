package pl.rentathing.Rental.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.rentathing.Rental.Dto.RentalCreateDto;
import pl.rentathing.Rental.Service.RentalService;
import pl.rentathing.auth.service.AuthService;
import pl.rentathing.item.dto.ItemDetailsDTO;
import pl.rentathing.item.service.ItemService;
import pl.rentathing.user.entity.User;
import pl.rentathing.user.repository.UserRepository;

import java.time.LocalDate;

@Controller
@RequestMapping("/wypozyczenia")
@RequiredArgsConstructor
public class UserRentalController {

    private final ItemService itemService;
    private final RentalService rentalService;
    private final UserRepository userRepository;
    private final AuthService authService;

    @GetMapping("/formularz")
    public String showRentalForm(
            @RequestParam(name = "przedmiot") Long itemId,
            @RequestParam(name = "start", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "koniec", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model
    ) {
        ItemDetailsDTO itemDto = itemService.getItemDetails(itemId);
        if (!itemDto.getAvailable()) {
            return "redirect:/catalog/details?id=" + itemId + "&error=not_available";
        }

        RentalCreateDto rentalCreateDto = rentalService.prepareRentalDto(itemId, startDate, endDate);
        User currentUser = authService.getCurrentUser();

        model.addAttribute("item", itemDto);
        model.addAttribute("startDate", rentalCreateDto.getStartDate());
        model.addAttribute("endDate", rentalCreateDto.getEndDate());

        model.addAttribute("user", currentUser);
        model.addAttribute("rentalCreateDto", rentalCreateDto);

        return "rental/form";
    }

    @PostMapping("/potwierdz")
    public String confirmRental(
            @Valid @ModelAttribute("rentalCreateDto") RentalCreateDto rentalCreateDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            ItemDetailsDTO itemDto = itemService.getItemDetails(rentalCreateDto.getItemId());
            User currentUser = authService.getCurrentUser();

            model.addAttribute("item", itemDto);
            model.addAttribute("user", currentUser);
            return "rental/form";
        }

        try {
            rentalService.createRental(rentalCreateDto);
            return "redirect:/wypozyczenia/sukces";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Wystąpił błąd: " + e.getMessage());
            return "redirect:/wypozyczenia/formularz?przedmiot=" + rentalCreateDto.getItemId();
        }
    }

}