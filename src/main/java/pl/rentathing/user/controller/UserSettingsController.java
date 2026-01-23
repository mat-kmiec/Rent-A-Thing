package pl.rentathing.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.rentathing.user.dto.UserSettingsDTO;
import pl.rentathing.user.dto.AddressDTO;
import pl.rentathing.user.entity.User;
import pl.rentathing.user.service.UserService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/moje-konto/ustawienia")
@RequiredArgsConstructor
public class UserSettingsController {

    private final UserService userService;

    @GetMapping
    public String getSettings(Authentication authentication, Model model) {
        User principal = (User) authentication.getPrincipal();
        User user = userService.findById(principal.getId())
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        AddressDTO addressDTO = AddressDTO.builder().build();
        if (user.getAddress() != null) {
            addressDTO = AddressDTO.builder()
                    .street(user.getAddress().getStreet())
                    .houseNumber(user.getAddress().getHouseNumber())
                    .apartmentNumber(user.getAddress().getApartmentNumber())
                    .city(user.getAddress().getCity())
                    .zipCode(user.getAddress().getZipCode())
                    .build();
        }

        UserSettingsDTO settings = UserSettingsDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .notifEmail(user.isNotifEmail())
                .notifSms(user.isNotifSms())
                .newsletter(user.isNewsletter())
                .address(addressDTO)
                .build();

        model.addAttribute("settings", settings);
        return "client/settings";
    }

    @PostMapping("/profile")
    public String updateSettings(
            @Valid @ModelAttribute("settings") UserSettingsDTO settingsDTO,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        User principal = (User) authentication.getPrincipal();

        fillMissingData(principal.getId(), settingsDTO);

        if (bindingResult.hasErrors()) {
            return "client/settings";
        }

        userService.updateUserSettings(principal.getId(), settingsDTO);

        Optional<User> updatedUser = userService.findById(principal.getId());
        updatedUser.ifPresent(user -> {
            Authentication newAuth = new UsernamePasswordAuthenticationToken(user, user.getPassword(),
                    user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        });

        redirectAttributes.addFlashAttribute("successMessage", "Ustawienia zostały zaktualizowane");

        return "redirect:/moje-konto/ustawienia#profile-card";
    }

    @PostMapping("/address")
    public String updateAddress(
            @Valid @ModelAttribute("settings") UserSettingsDTO settingsDTO,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        User principal = (User) authentication.getPrincipal();

        fillMissingData(principal.getId(), settingsDTO);

        if (bindingResult.hasErrors()) {
            return "client/settings";
        }

        userService.updateUserSettings(principal.getId(), settingsDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Adres został zaktualizowany");

        return "redirect:/moje-konto/ustawienia";
    }

    @PostMapping("/password")
    public String updatePassword(
            @ModelAttribute("settings") UserSettingsDTO settingsDTO,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        User principal = (User) authentication.getPrincipal();

        fillMissingData(principal.getId(), settingsDTO);

        boolean hasErrors = false;

        if (settingsDTO.getCurrentPassword() == null || settingsDTO.getCurrentPassword().isEmpty()) {
            bindingResult.rejectValue("currentPassword", "error.settings", "Aktualne hasło jest wymagane");
            hasErrors = true;
        }

        if (settingsDTO.getNewPassword() == null || settingsDTO.getNewPassword().length() < 8) {
            bindingResult.rejectValue("newPassword", "error.settings", "Nowe hasło musi mieć minimum 8 znaków");
            hasErrors = true;
        }

        if (settingsDTO.getConfirmPassword() == null
                || !settingsDTO.getConfirmPassword().equals(settingsDTO.getNewPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.settings", "Hasła nie są identyczne");
            hasErrors = true;
        }

        if (hasErrors) {
            return "client/settings";
        }

        try {
            userService.updateUserSettings(principal.getId(), settingsDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Hasło zostało zmienione");
        } catch (Exception e) {
            bindingResult.rejectValue("currentPassword", "error.settings", e.getMessage());
            return "client/settings";
        }

        return "redirect:/moje-konto/ustawienia#password-card";
    }

    @PostMapping("/notifications")
    public String updateNotifications(
            @ModelAttribute("settings") UserSettingsDTO settingsDTO,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        User principal = (User) authentication.getPrincipal();
        fillMissingData(principal.getId(), settingsDTO);
        userService.updateUserSettings(principal.getId(), settingsDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Preferencje powiadomień zostały zaktualizowane");

        return "redirect:/moje-konto/ustawienia#notifications-card";
    }

    private void fillMissingData(Long userId, UserSettingsDTO settingsDTO) {
        User user = userService.findById(userId).orElseThrow();

        if (settingsDTO.getFirstName() == null) {
            settingsDTO.setFirstName(user.getFirstName());
        }
        if (settingsDTO.getLastName() == null) {
            settingsDTO.setLastName(user.getLastName());
        }
        if (settingsDTO.getEmail() == null) {
            settingsDTO.setEmail(user.getEmail());
        }
        if (settingsDTO.getPhoneNumber() == null) {
            settingsDTO.setPhoneNumber(user.getPhoneNumber());
        }

        if (settingsDTO.getNotifEmail() == null) {
            settingsDTO.setNotifEmail(user.isNotifEmail());
        }
        if (settingsDTO.getNotifSms() == null) {
            settingsDTO.setNotifSms(user.isNotifSms());
        }
        if (settingsDTO.getNewsletter() == null) {
            settingsDTO.setNewsletter(user.isNewsletter());
        }

        if (settingsDTO.getAddress() == null || settingsDTO.getAddress().getStreet() == null) {
            if (user.getAddress() != null) {
                AddressDTO addressDTO = AddressDTO.builder()
                        .street(user.getAddress().getStreet())
                        .houseNumber(user.getAddress().getHouseNumber())
                        .apartmentNumber(user.getAddress().getApartmentNumber())
                        .city(user.getAddress().getCity())
                        .zipCode(user.getAddress().getZipCode())
                        .build();
                settingsDTO.setAddress(addressDTO);
            } else {
                settingsDTO.setAddress(null);
            }
        }
    }
}
