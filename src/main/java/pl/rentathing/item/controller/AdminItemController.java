package pl.rentathing.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.rentathing.item.dto.ItemFormDTO;
import pl.rentathing.item.service.CategoryService;
import pl.rentathing.item.service.ItemService;

@Controller
@RequestMapping("/admin/inventory")
@RequiredArgsConstructor
public class AdminItemController {

    private final ItemService itemService;
    private final CategoryService categoryService;

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("item", new ItemFormDTO());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/item-form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        ItemFormDTO itemDto = itemService.getItemFormById(id);
        model.addAttribute("item", itemDto);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/item-form";
    }

    @PostMapping("/save")
    public String saveItem(@Valid @ModelAttribute("item") ItemFormDTO itemDto,
                           BindingResult result,
                           Model model) {

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            if (itemDto.getId() != null && itemDto.getCurrentImageUrl() == null) {
            }
            return "admin/item-form";
        }

        try {
            itemService.saveItem(itemDto);
        } catch (Exception e) {
            result.rejectValue("imageFile", "error.item", "Wystąpił błąd podczas zapisu: " + e.getMessage());
            model.addAttribute("categories", categoryService.getAllCategories());
            return "admin/item-form";
        }

        return "redirect:/admin/inventory";
    }
}