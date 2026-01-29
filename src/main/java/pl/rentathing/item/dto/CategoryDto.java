package pl.rentathing.item.dto;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing a category.
 * This class is used to encapsulate data for the category entity.
 * It includes attributes related to category and provides
 * a structured format for category data handling in the application.
 *
 * Attributes:
 * - id: Unique identifier for the category.
 * - name: Name of the category.
 * - description: Description of the category.
 * - iconClass: CSS class for the category icon.
 * - itemCount: Number of items associated with the category.
 */
@Data
public class CategoryDto {
    private Long id;
    private String name;
    private String description;
    private String iconClass;
    private int itemCount;
}