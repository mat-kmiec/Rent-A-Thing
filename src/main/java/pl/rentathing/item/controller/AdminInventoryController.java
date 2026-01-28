package pl.rentathing.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.rentathing.item.dto.CategoryDto;
import pl.rentathing.item.dto.ItemAdminListDTO;
import pl.rentathing.item.dto.ItemFormDTO;
import pl.rentathing.item.dto.ItemSearchCriteria;
import pl.rentathing.item.service.CategoryService;
import pl.rentathing.item.service.ItemService;

import java.util.List;

@Controller
@RequestMapping("/admin/inventory")
@RequiredArgsConstructor
public class AdminInventoryController {


    private final CategoryService categoryService;
    private final ItemService itemService;


    @GetMapping
    public String inventoryPage(
            ItemSearchCriteria itemCriteria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(name = "category", required = false) String searchCat,
            @RequestParam(name = "sortCat", required = false, defaultValue = "idDesc") String sortCat,
            Model model) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        Page<ItemAdminListDTO> itemsPage = itemService.getAdminItemsPage(itemCriteria, pageable);
        model.addAttribute("items", itemsPage.getContent());
        model.addAttribute("itemsPage", itemsPage);
        model.addAttribute("allCategories", categoryService.getAllCategories());
        model.addAttribute("criteria", itemCriteria);
        model.addAttribute("categories", categoryService.searchAndSortCategories(searchCat, sortCat));
        model.addAttribute("currentSort", sortCat);

        return "admin/inventory";
    }

    @PostMapping("/category/add")
    public String addCategory(@RequestParam String name,
                              @RequestParam(required = false) String description,
                              @RequestParam(required = false) String iconClass) {

        categoryService.addCategory(name, description, iconClass);

        return "redirect:/admin/inventory#categories";
    }

    @PostMapping("/category/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/admin/inventory#categories";
    }

    @PostMapping("/category/edit")
    public String editCategory(@RequestParam Long id,
                               @RequestParam String name,
                               @RequestParam String description,
                               @RequestParam String iconClass) {
        categoryService.updateCategory(id, name, description, iconClass);
        return "redirect:/admin/inventory#categories";
    }

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
