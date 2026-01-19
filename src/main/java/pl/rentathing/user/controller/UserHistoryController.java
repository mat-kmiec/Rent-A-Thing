package pl.rentathing.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/moje-konto/historia")
public class UserHistoryController {

    @GetMapping
    public String getHistory(){
        return "client/history";
    }
}
