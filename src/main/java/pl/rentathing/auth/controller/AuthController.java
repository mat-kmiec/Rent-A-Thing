package pl.rentathing.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import pl.rentathing.auth.dto.RegisterRequest;
import pl.rentathing.auth.exception.AuthException;
import pl.rentathing.auth.service.AuthService;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/logowanie")
    public String loginPage(){
        return "auth/login";
    }

    @GetMapping("/rejestracja")
    public String registerPage(Model model){
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterRequest request,
                           BindingResult bindingResult,
                           Model model){
        if(bindingResult.hasErrors()){
            return "auth/register";
        }

        try{
            authService.register(request);
            return "redirect:/logowanie?success";
        }catch (AuthException e){
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }


    }
}
