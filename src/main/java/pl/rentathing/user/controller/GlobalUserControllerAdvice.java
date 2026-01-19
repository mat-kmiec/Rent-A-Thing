package pl.rentathing.user.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import pl.rentathing.user.entity.User;

@ControllerAdvice(basePackages = "pl.rentathing.user.controller")
public class GlobalUserControllerAdvice {

    @ModelAttribute("loggedInUser")
    public User addLoggedInUserToModel(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }
}
