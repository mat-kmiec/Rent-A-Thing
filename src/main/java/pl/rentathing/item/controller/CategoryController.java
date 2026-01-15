package pl.rentathing.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.rentathing.item.dto.CategoryDto;
import pl.rentathing.item.service.CategoryService;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
}