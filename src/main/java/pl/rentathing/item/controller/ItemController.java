package pl.rentathing.item.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping
    public String getItemDetails(@RequestParam("id") Long id, Model model){
        ItemDetailsDTO itemDetailsDTO = itemService.getItemDetails(id);
        model.addAttribute("item", itemDetailsDTO);
        return "catalog/details";
    }

}
