package pl.rentathing.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthExceptionController {

    @GetMapping("/wymagane-logowanie")
    public String showAuthRequiredPage() {
        return "error/auth-required";
    }
}
