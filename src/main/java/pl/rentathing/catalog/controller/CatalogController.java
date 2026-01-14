package pl.rentathing.catalog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/katalog")
public class CatalogController {


    @GetMapping
    public String catalog() {
        return "catalog/list";
    }
}
