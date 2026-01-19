package pl.rentathing.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/rentals")
public class AdminRentalController {

    @GetMapping
    public String getRentals(){
        return "admin/rentals";
    }
}
