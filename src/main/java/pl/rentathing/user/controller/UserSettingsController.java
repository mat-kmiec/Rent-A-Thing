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

/**
 * Controller responsible for managing user settings and preferences.
 * Provides endpoints for displaying and updating user profile information,
 * address details, password, and notification preferences.
 */
@Controller
@RequestMapping("/moje-konto/ustawienia")
@RequiredArgsConstructor
public class UserSettingsController {

    private final UserService userService;

    /**
     *
     */
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

    /**
     *
     */
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

    /**
     * Handles the update of a user's address settings. Validates the input data,
     * fills in any missing information, and updates the user's settings in the system.
     * Redirects the user to the settings page with a success message upon successful update.
     *
     * @param settingsDTO the user settings data transfer object holding address-related data
     *                    to be updated
     * @param bindingResult the result of the validation for the provided user settings data
     * @param authentication the security authentication object holding details of
     *                       the currently logged-in user
     * @param redirectAttributes the redirect attributes used to pass flash messages
     *                           to the redirected page
     * @return a string representing the view or redirection URL. If there are validation
     *         errors, it returns the view for the settings page; otherwise, it redirects
     *         to the user settings overview page.
     */
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

    /**
     * Updates the password for the currently authenticated user.
     *
     * @param settingsDTO          an instance of {@code UserSettingsDTO} containing the current password, new password,
     *                             and confirmation of the new password.
     * @param bindingResult        an instance of {@code BindingResult} used to report validation errors for the form data.
     * @param authentication       an instance of {@code Authentication} representing the authenticated user's details.
     * @param redirectAttributes   an instance of {@code RedirectAttributes} used to add flash attributes for the redirect.
     * @return                     a String representing the view name or a redirect URL. Returns "client/settings" if
     *                             validation errors exist or an exception occurs. Otherwise, redirects to the settings page.
     */
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

    /**
     * Updates the notification settings for the currently authenticated user.
     *
     * @param settingsDTO the user settings data transfer object containing the notification preferences to be updated
     * @param authentication the authentication object representing the currently authenticated user
     * @param redirectAttributes the attributes used to pass messages or data during a redirect
     * @return a redirect string pointing to the user's account settings page with the notifications card section displayed
     */
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

    /**
     * Populates missing data in the provided UserSettingsDTO object with information retrieved from the corresponding
     * user entity identified by the userId. If any property in the UserSettingsDTO is null, it is replaced with the
     * value from the user entity.
     *
     * @param userId the ID of the user whose data will be fetched to fill in missing properties in the settingsDTO
     * @param settingsDTO the UserSettingsDTO object that requires missing fields to be populated from the user's data
     */
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
