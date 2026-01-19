package pl.rentathing.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/inventory")
public class AdminInventoryController {

    @GetMapping
    public String getInventory(){
        return "admin/inventory";
    }
}
