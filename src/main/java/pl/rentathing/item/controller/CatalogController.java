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

@Controller
@RequiredArgsConstructor
public class CatalogController {


    private final ItemRepository itemRepository;
    private final CategoryService categoryService;

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
