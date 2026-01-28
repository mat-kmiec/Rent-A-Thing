package pl.rentathing.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.rentathing.item.entity.Item;
import pl.rentathing.item.service.ItemService;
import pl.rentathing.item.service.CategoryService;

@Controller
@RequestMapping("/admin/inventory")
@RequiredArgsConstructor
public class AdminItemController {

    private final ItemService itemService;
    private final CategoryService categoryService;

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("item", new Item());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/item-form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("item", itemService.getItemById(id));
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/item-form";
    }

    @PostMapping("/save")
    public String saveItem(@ModelAttribute("item") Item item,
                           @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        itemService.saveItem(item, imageFile);
        return "redirect:/admin/magazyn";
    }
}