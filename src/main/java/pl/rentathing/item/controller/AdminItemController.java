package pl.rentathing.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
        try {
            ItemFormDTO itemDto = itemService.getItemFormById(id);
            model.addAttribute("item", itemDto);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "admin/item-form";
        } catch (IllegalArgumentException e) {
            return "redirect:/admin/inventory";
        }
    }

    @PostMapping("/save")
    public String saveItem(@Valid @ModelAttribute("item") ItemFormDTO itemDto,
                           BindingResult result,
                           Model model,
                           RedirectAttributes ra) {

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("toastType", "error");
            model.addAttribute("toastMessage", "Popraw błędy w formularzu.");
            return "admin/item-form";
        }

        try {
            boolean isNew = (itemDto.getId() == null);
            itemService.saveItem(itemDto);
            ra.addFlashAttribute("toastType", "success");
            ra.addFlashAttribute("toastMessage", isNew ?
                    "Pomyślnie dodano nowy przedmiot: " + itemDto.getTitle() :
                    "Zmiany w przedmiocie " + itemDto.getTitle() + " zostały zapisane.");

        } catch (Exception e) {
            result.rejectValue("imageFile", "error.item", "Wystąpił błąd podczas zapisu: " + e.getMessage());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("toastType", "error");
            model.addAttribute("toastMessage", "Błąd zapisu: " + e.getMessage());
            return "admin/item-form";
        }

        return "redirect:/admin/inventory";
    }
}