package pl.rentathing.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.rentathing.item.dto.CategoryDto;
import pl.rentathing.item.entity.Category;
import pl.rentathing.item.mapper.CategoryMapper;
import pl.rentathing.item.repository.CategoryRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    /**
     * Retrieves all categories from the repository and maps them to a list of CategoryDto objects.
     *
     * @return a list of CategoryDto objects representing all categories.
     */
    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categoryMapper.toDtoList(categories);
    }
    /**
     * Searches for categories whose name or description contains the specified query string,
     * case-insensitively.
     *
     * @param query the search query string to match against category names and descriptions
     * @return a list of CategoryDto objects that match the search criteria
     */
    public List<CategoryDto> searchCategories(String query) {
        String lowerQuery = query.toLowerCase();
        return categoryMapper.toDtoList(categoryRepository.findAll())
                .stream()
                .filter(c -> c.getName().toLowerCase().contains(lowerQuery) ||
                        (c.getDescription() != null && c.getDescription().toLowerCase().contains(lowerQuery)))
                .toList();
    }
    /**
     * Adds a new category with the specified details to the repository.
     *
     * @param name the name of the category to be added
     * @param description a description of the category
     * @param iconClass a CSS class for an icon representing the category
     */
    public void addCategory(String name, String description, String iconClass) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setIconClass(iconClass);

        categoryRepository.save(category);
    }
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
    /**
     * Updates an existing category with the specified ID by setting its name,
     * description, and iconClass, and saves the updated category to the repository.
     *
     * @param id the ID of the category to be updated
     * @param name the new name of the category
     * @param description the new description of the category
     * @param iconClass the new icon class of the category
     * @throws RuntimeException if no category with the specified ID is found
     */
    public void updateCategory(Long id, String name, String description, String iconClass) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono kategorii o ID: " + id));

        category.setName(name);
        category.setDescription(description);
        category.setIconClass(iconClass);

        categoryRepository.save(category);
    }
    /**
     * Searches for categories by a query string and sorts the results based on the specified sorting order.
     *
     * @param query the search string to filter categories by name; if null or empty, all categories are returned
     * @param sort  the sorting order for the categories; possible values are "nameAsc", "nameDesc", "idAsc", or any other value indicating descending by ID
     * @return a list of CategoryDto objects filtered and sorted according to the query and sort parameters
     */
    public List<CategoryDto> searchAndSortCategories(String query, String sort) {
        List<Category> categories;

        if (query != null && !query.isEmpty()) {
            categories = categoryRepository.findByNameContainingIgnoreCase(query);
        } else {
            categories = categoryRepository.findAll();
        }

        switch (sort) {
            case "nameAsc":
                categories.sort((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()));
                break;
            case "nameDesc":
                categories.sort((c1, c2) -> c2.getName().compareToIgnoreCase(c1.getName()));
                break;
            case "idAsc":
                categories.sort(Comparator.comparing(Category::getId));
                break;
            default:
                categories.sort((c1, c2) -> c2.getId().compareTo(c1.getId()));
        }

        return categories.stream().map(this::convertToDto).toList();
    }
    /**
     * Converts a Category entity to a CategoryDto object.
     *
     * @param category the Category entity to be converted
     * @return a CategoryDto object containing the mapped data from the given Category entity
     */
    private CategoryDto convertToDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setIconClass(category.getIconClass());
        return dto;
    }
}
