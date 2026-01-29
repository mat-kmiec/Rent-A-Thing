package pl.rentathing.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import pl.rentathing.user.entity.User;
import pl.rentathing.user.repository.UserRepository;

/**
 * Global controller advice for user-related controllers in the specified package.
 * This advice adds a global model attribute for the currently logged-in user.
 *
 * The class uses the UserRepository to retrieve user details from the database.
 * It ensures that the authenticated user's details are accessible as a model attribute
 * across all methods in the controllers within the specified package.
 */
@ControllerAdvice(basePackages = "pl.rentathing.user.controller")
@RequiredArgsConstructor
public class GlobalUserControllerAdvice {

    private final UserRepository userRepository;

    /**
     * Adds the currently logged-in user to the model as an attribute using the provided authentication object.
     * If the user is authenticated and their principal is of type {@code User}, the user is retrieved
     * from the database using their ID. If the user is not found, the principal is used directly.
     * If there is no authentication, or the principal is not a {@code User}, {@code null} is returned.
     *
     * @param authentication the authentication object containing the principal of the currently logged-in user
     * @return the logged-in {@code User} object if found or available, or {@code null} if no user is authenticated
     */
    @ModelAttribute("loggedInUser")
    public User addLoggedInUserToModel(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return userRepository.findById(user.getId()).orElse(user);
        }
        return null;
    }
}
