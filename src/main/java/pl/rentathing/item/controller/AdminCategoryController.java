package pl.rentathing.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.rentathing.item.service.CategoryService;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @GetMapping("/magazyn")
    public String inventoryPage(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());

        return "admin/inventory";
    }
}