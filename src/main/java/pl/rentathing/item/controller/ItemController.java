package pl.rentathing.item.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.rentathing.item.dto.ItemDetailsDTO;
import pl.rentathing.item.service.ItemService;

@Controller
@RequestMapping("/przedmiot")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }


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
