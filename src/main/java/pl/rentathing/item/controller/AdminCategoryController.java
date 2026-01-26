package pl.rentathing.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.rentathing.item.dto.CategoryDto;
import pl.rentathing.item.service.CategoryService;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @GetMapping("/magazyn")
    public String inventoryPage(
            @RequestParam(name = "category", required = false) String search,
            org.springframework.ui.Model model) {

        List<CategoryDto> categories;

        if (search != null && !search.trim().isEmpty()) {
            categories = categoryService.searchCategories(search);
        } else {
            categories = categoryService.getAllCategories();
        }

        model.addAttribute("categories", categories);
        model.addAttribute("searchQuery", search);
        return "admin/inventory";
    }
    @PostMapping("/magazyn/add")
    public String addCategory(@RequestParam String name,
                              @RequestParam(required = false) String description,
                              @RequestParam(required = false) String iconClass) {

        categoryService.addCategory(name, description, iconClass);

        return "redirect:/admin/magazyn#categories";
    }
}