package pl.rentathing.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import pl.rentathing.user.entity.User;
import pl.rentathing.user.repository.UserRepository;

@ControllerAdvice(basePackages = "pl.rentathing.user.controller")
@RequiredArgsConstructor
public class GlobalUserControllerAdvice {

    private final UserRepository userRepository;

    @ModelAttribute("loggedInUser")
    public User addLoggedInUserToModel(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return userRepository.findById(user.getId()).orElse(user);
        }
        return null;
    }
}
