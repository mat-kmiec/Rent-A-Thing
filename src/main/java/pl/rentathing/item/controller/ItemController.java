package pl.rentathing.item.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/przedmiot")
public class ItemController {

    @GetMapping
    public String index(){
        return "catalog/details";
    }

}
