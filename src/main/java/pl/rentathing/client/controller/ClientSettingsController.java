package pl.rentathing.client.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.rentathing.user.dto.UserSettingsDTO;
import pl.rentathing.user.entity.User;
import pl.rentathing.user.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientSettingsController {
    
    private final UserService userService;
    
    @GetMapping("/settings")
    public String getSettings(Authentication authentication, Model model) {
        User user = (User) authentication.getPrincipal();
        
        UserSettingsDTO settings = UserSettingsDTO.builder()
            .id(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail())
            .build();
        
        model.addAttribute("settings", settings);
        return "client/settings";
    }
    
    @PostMapping("/settings")
    public String updateSettings(
        @Valid @ModelAttribute("settings") UserSettingsDTO settingsDTO,
        BindingResult bindingResult,
        Authentication authentication,
        Model model) {
        
        User user = (User) authentication.getPrincipal();
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("settings", settingsDTO);
            return "client/settings";
        }
        
        userService.updateUserSettings(user.getId(), settingsDTO);
        model.addAttribute("successMessage", "Ustawienia zostały zaktualizowane");
        
        return "redirect:/client/settings";
    }
}
