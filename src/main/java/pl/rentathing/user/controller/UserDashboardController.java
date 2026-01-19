package pl.rentathing.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/moje-konto")
public class UserDashboardController {

    @GetMapping
    public String getDashboard(){
        return "client/dashboard";
    }
}
