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

/**
 * Controller responsible for handling administrative operations related to the inventory.
 * Allows management of categories, items, and provides functionalities for adding, editing,
 * deleting, and listing inventory items and categories in the admin panel.
 */
@Controller
@RequestMapping("/admin/inventory")
@RequiredArgsConstructor
public class AdminInventoryController {


    private final CategoryService categoryService;
    private final ItemService itemService;


    /**
     * Displays the inventory page for the admin panel.
     * Allows filtering, sorting, and pagination of items, as well as interaction
     * with categories and item data.
     *
     * @param itemCriteria the criteria used to filter items in the inventory
     * @param page the current page number for pagination, defaults to 0
     * @param searchCat the optional category name or criteria for filtering the displayed categories
     * @param sortCat the sorting parameter for categories, defaults to "idDesc"
     * @param model the Spring Model object used to pass data to the view
     * @return the name of the template to render the admin inventory page
     */
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

    /**
     * Adds a new category to the system and redirects to the inventory management page.
     *
     * @param name the name of the category to be added
     * @param description an optional description for the category
     * @param iconClass an optional icon class for the category
     * @return a redirect string to navigate back to the categories section of the inventory page
     */
    @PostMapping("/category/add")
    public String addCategory(@RequestParam String name,
                              @RequestParam(required = false) String description,
                              @RequestParam(required = false) String iconClass) {

        categoryService.addCategory(name, description, iconClass);

        return "redirect:/admin/inventory#categories";
    }

    /**
     * Deletes a category identified by its unique ID and redirects to the categories section
     * of the inventory management page.
     *
     * @param id the unique identifier of the category to be deleted
     * @return a redirect string to navigate back to the categories section of the inventory page
     */
    @PostMapping("/category/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/admin/inventory#categories";
    }

    /**
     * Edits an existing category in the system with updated details
     * and redirects to the inventory management page.
     *
     * @param id the unique identifier of the category to be updated
     * @param name the new name for the category
     * @param description the new description for the category
     * @param iconClass the new icon class for the category
     * @return a redirect string to navigate back to the categories section of the inventory page
     */
    @PostMapping("/category/edit")
    public String editCategory(@RequestParam Long id,
                               @RequestParam String name,
                               @RequestParam String description,
                               @RequestParam String iconClass) {
        categoryService.updateCategory(id, name, description, iconClass);
        return "redirect:/admin/inventory#categories";
    }

    /**
     * Handles GET requests to display the form for adding a new item.
     *
     * @param model the Model object used to pass attributes to the view
     * @return the name of the view template for the item form
     */
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("item", new ItemFormDTO());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/item-form";
    }

    /**
     * Handles the request to display the edit form for an item.
     * Retrieves the item details and category list to populate the edit form.
     *
     * @param id the ID of the item to be edited
     * @param model the Model object used to pass data to the view
     * @return the name of the view to render, typically the edit form view;
     *         redirects to the inventory page if the item is not found
     */
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

    /**
     * Saves the provided item to the inventory. Validates the input data and handles errors accordingly.
     * If the item is new, it is created; otherwise, the existing item is updated.
     *
     * @param itemDto the data transfer object containing the details of the item to be saved, which must be validated
     * @param result the object that holds the result of binding form fields and validation
     * @param model the model to hold attributes needed for rendering the item form on error
     * @param ra the redirect attributes to pass messages across redirect scenarios
     * @return a redirect string to the inventory page if successful, or the item creation form if validation or save errors occur
     */
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
