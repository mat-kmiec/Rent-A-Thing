package pl.rentathing.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.rentathing.item.dto.CategoryDto;
import pl.rentathing.item.service.CategoryService;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AdminCategoryController {

    private final CategoryService categoryService;

    @GetMapping("/magazyn")
    public String inventoryPage() {
        return "localhost:8080/admin/inventory";
    }

}