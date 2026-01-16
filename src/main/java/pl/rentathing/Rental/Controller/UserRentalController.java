package pl.rentathing.Rental.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.rentathing.Rental.Dto.RentalCreateDto;
import pl.rentathing.Rental.Service.RentalService;
import pl.rentathing.item.dto.ItemDetailsDTO;
import pl.rentathing.item.exception.ItemNotAvailableExpection;
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

    @GetMapping("/formularz")
    public String showRentalForm(
            @RequestParam(name = "przedmiot") Long itemId,
            @RequestParam(name = "start", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "koniec", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication,
            Model model
    ) {
        ItemDetailsDTO dto = itemService.getItemDetails(itemId);
        User currentUser = userRepository.findByEmail(authentication.getName()).orElseThrow();

        RentalCreateDto rentalCreateDto = new RentalCreateDto();
        rentalCreateDto.setItemId(itemId);
        rentalCreateDto.setFirstName(currentUser.getFirstName());
        rentalCreateDto.setLastName(currentUser.getLastName());
        rentalCreateDto.setEmail(currentUser.getEmail());
        rentalCreateDto.setDeliveryMethod("PICKUP");
        rentalCreateDto.setPayment("ONLINE");
        rentalCreateDto.setStartDate(startDate != null ? startDate : LocalDate.now());
        rentalCreateDto.setEndDate(endDate != null ? endDate : LocalDate.now().plusDays(1));


        if (!dto.getAvailable()) {
            return "redirect:/catalog/details?id=" + itemId + "&error=not_available";
        }

        model.addAttribute("item", dto);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("user", currentUser);
        model.addAttribute("rentalCreateDto", rentalCreateDto);

        return "rental/form";
    }

    @PostMapping("/potwierdz")
    public String confirmRental(
            @Valid @ModelAttribute("rentalCreateDto") RentalCreateDto rentalCreateDto,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            ItemDetailsDTO itemDto = itemService.getItemDetails(rentalCreateDto.getItemId());
            User currentUser = userRepository.findByEmail(authentication.getName()).orElseThrow();
            model.addAttribute("item", itemDto);
            model.addAttribute("user", currentUser);
            return "rental/form";
        }

        try {
            rentalService.createRental(rentalCreateDto, authentication);
            return "redirect:/wypozyczenia/sukces";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Wystąpił błąd: " + e.getMessage());
            return "redirect:/wypozyczenia/formularz?przedmiot=" + rentalCreateDto.getItemId();
        }
    }


}