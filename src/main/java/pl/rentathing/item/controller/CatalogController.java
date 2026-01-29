package pl.rentathing.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.rentathing.item.dto.ItemSearchCriteria;
import pl.rentathing.item.repository.ItemRepository;
import pl.rentathing.item.repository.ItemSpecifications;
import pl.rentathing.item.service.CategoryService;

/**
 * This controller is responsible for handling requests related to the catalog of items.
 * It provides functionality to display a paginated and optionally filtered list of items
 * based on search criteria and integrates with the view layer by populating the model
 * with required data.
 */
@Controller
@RequiredArgsConstructor
public class CatalogController {


    private final ItemRepository itemRepository;
    private final CategoryService categoryService;

    /**
     * Handles the request to display the catalog of items. Retrieves a paginated
     * and optionally filtered list of items based on the provided search criteria.
     * Adds the list of items, search criteria, and available categories to the model.
     *
     * @param criteria the search criteria used to filter the items
     * @param pageable the pagination and sorting information
     * @param model the model to which attributes will be added
     * @return the name of the view to render, in this case "catalog/list"
     */
    @GetMapping("/katalog")
    public String showCatalog(
            ItemSearchCriteria criteria,
            @PageableDefault(size = 9, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable,
            Model model
    ) {
        var spec = ItemSpecifications.build(criteria);
        var itemsPage = itemRepository.findAll(spec, pageable);

        model.addAttribute("items", itemsPage);
        model.addAttribute("criteria", criteria);
        model.addAttribute("categories", categoryService.getAllCategories());

        return "catalog/list";
    }


}
