package pl.rentathing.item.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.rentathing.item.dto.ItemDetailsDTO;
import pl.rentathing.item.service.ItemService;

/**
 * Controller class responsible for handling requests related to items.
 * Acts as an intermediary between the view and the ItemService layer.
 */
@Controller
@RequestMapping("/przedmiot")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }


    /**
     * Retrieves item details for a given item ID and adds them to the model.
     *
     * @param id    the unique identifier of the item
     * @param model the model to which item details will be added
     * @return a string representing the name of the view to be rendered; redirects to "/katalog" in case of an error
     */
    @GetMapping("/{id}")
    public String getItemDetails(@PathVariable("id") Long id, Model model) {
        try {
            ItemDetailsDTO itemDetailsDTO = itemService.getItemDetails(id);
            model.addAttribute("item", itemDetailsDTO);
            return "catalog/details";
        } catch (Exception e) {
            return "redirect:/katalog";
        }
    }

}
