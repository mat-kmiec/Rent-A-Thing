package pl.rentathing.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
            @RequestParam(name = "sort", required = false, defaultValue = "idDesc") String sort,
            Model model) {

        List<CategoryDto> categories = categoryService.searchAndSortCategories(search, sort);

        model.addAttribute("currentSort", sort);
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

    @PostMapping("/magazyn/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/admin/magazyn#categories";
    }

    @PostMapping("/magazyn/edit")
    public String editCategory(@RequestParam Long id,
                               @RequestParam String name,
                               @RequestParam String description,
                               @RequestParam String iconClass) {
        categoryService.updateCategory(id, name, description, iconClass);
        return "redirect:/admin/magazyn#categories";
    }

}