package pl.rentathing.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import pl.rentathing.auth.dto.RegisterRequest;
import pl.rentathing.auth.exception.AuthException;
import pl.rentathing.auth.service.AuthService;

/**
 * Controller responsible for managing authentication and user registration.
 */
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Handles the login page request. If the user is already authenticated,
     * they will be redirected to the homepage; otherwise, the login page is displayed.
     *
     * @param authentication the current user's authentication object that provides details
     *                        about the user's authentication state
     * @return a string representing the view name to be rendered, either a redirect to the
     *         homepage if authenticated or the login page if not authenticated
     */
    @GetMapping("/logowanie")
    public String loginPage(Authentication authentication){
        if(authentication != null && authentication.isAuthenticated()){
            return "redirect:/?loggedIn";
        }
        return "auth/login";
    }

    /**
     * Handles the HTTP GET request for the registration page.
     *
     * If the user is already authenticated, the method redirects the user
     * to the home page with a logged-in parameter. Otherwise, it adds a
     * {@code registerRequest} attribute to the model and returns the registration
     * page view name.
     *
     * @param authentication the authentication object of the currently logged-in user, or null if the user is not authenticated
     * @param model the model object to which attributes can be added
     * @return a string representing the view name for the registration page or a redirect URL
     */
    @GetMapping("/rejestracja")
    public String registerPage(Authentication authentication, Model model){
        if(authentication != null && authentication.isAuthenticated()){
            return "redirect:/?loggedIn";
        }
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    /**
     * Handles POST requests for user registration.
     * Validates the provided registration data, ensures passwords match,
     * and attempts to create a new user account. If any validation errors occur,
     * or registration fails, the appropriate error messages are displayed.
     *
     * @param request the registration request object containing user details such as username, password, and confirm password
     * @param bindingResult the object holding validation results, such as field-specific errors
     * @param model the model used to pass attributes to the view, such as error messages
     * @return a string representing the view name; redirects to the login page upon successful registration,
     *         or returns the registration view to display validation or specific error messages
     */
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterRequest request,
                           BindingResult bindingResult,
                           Model model){
        if(!request.getPassword().equals(request.getConfirmPassword())){
            bindingResult.rejectValue("confirmPassword", "error.mismatch", "Hasła nie są takie same");
        }
        if(bindingResult.hasErrors()){
            return "auth/register";
        }

        try{
           authService.register(request);
            return "redirect:/logowanie?registered";
        }catch (AuthException e){
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }


    }
}
