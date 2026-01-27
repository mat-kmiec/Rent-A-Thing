package pl.rentathing.Rental.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.rentathing.Rental.Dto.RentalAdminListDto;
import pl.rentathing.Rental.Entity.RentalStatus;
import pl.rentathing.Rental.Service.RentalService;
import pl.rentathing.item.service.CategoryService;

@Controller
@RequestMapping("/admin/rentals")
@RequiredArgsConstructor
public class AdminRentalController {

    private final RentalService rentalService;
    private final CategoryService categoryService;

    @GetMapping
    public String listRentals(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) RentalStatus status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String returnDate,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {

        Page<RentalAdminListDto> rentals = rentalService.getRentals(search, status, category, returnDate, pageable);

        model.addAttribute("rentals", rentals);
        model.addAttribute("statuses", RentalStatus.values());
        model.addAttribute("categories", categoryService.getAllCategories());

        return "admin/rentals";
    }
}
