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

    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categoryMapper.toDtoList(categories);
    }
    public List<CategoryDto> searchCategories(String query) {
        String lowerQuery = query.toLowerCase();
        return categoryMapper.toDtoList(categoryRepository.findAll())
                .stream()
                .filter(c -> c.getName().toLowerCase().contains(lowerQuery) ||
                        (c.getDescription() != null && c.getDescription().toLowerCase().contains(lowerQuery)))
                .toList();
    }
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
    public void updateCategory(Long id, String name, String description, String iconClass) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono kategorii o ID: " + id));

        category.setName(name);
        category.setDescription(description);
        category.setIconClass(iconClass);

        categoryRepository.save(category);
    }
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
    private CategoryDto convertToDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setIconClass(category.getIconClass());
        return dto;
    }
}
