package pl.rentathing.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.rentathing.item.dto.CategoryDto;
import pl.rentathing.item.service.CategoryService;
import java.util.List;

/**
 * Controller class for managing category-related operations.
 * This class provides REST endpoints for interacting with categories.
 *
 * Endpoints:
 * - GET /api/categories: Retrieves a list of all categories.
 *
 * Annotations:
 * - @RestController: Marks the class as a REST controller.
 * - @RequestMapping("/api/categories"): Defines the base URL for all endpoints in this controller.
 * - @RequiredArgsConstructor: Generates a constructor with required arguments (final fields).
 * - @CrossOrigin("*"): Enables Cross-Origin Resource Sharing (CORS) for all origins.
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Retrieves a list of all categories.
     *
     * @return a {@code ResponseEntity} containing a list of {@code CategoryDto} objects
     *         representing all available categories.
     */
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
}