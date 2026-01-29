package pl.rentathing.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.rentathing.item.dto.CategoryDto;
import pl.rentathing.item.entity.Category;
import java.util.List;

/**
 * The CategoryMapper interface provides mapping functionalities between
 * the Category entity and its corresponding DTO, CategoryDto.
 *
 * It simplifies the transformation of Category domain models into transferable
 * objects, particularly DTOs, for use in the application. The interface
 * leverages MapStruct to generate the corresponding implementation at compile time.
 *
 * This mapper follows Spring's component model to support dependency injection.
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper {

    /**
     * Maps a {@code Category} entity to a {@code CategoryDto}.
     *
     * The method copies values from the {@code Category} entity, including its
     * id, name, description, iconClass, and calculates the {@code itemCount}
     * as the size of the {@code items} list in the provided {@code Category}.
     * If the {@code items} list is null, the {@code itemCount} is set to 0.
     *
     * @param category the {@code Category} entity to be mapped to a {@code CategoryDto}.
     *                 If {@code category} is null, the method will return null.
     * @return a {@code CategoryDto} containing the mapped values from the input {@code Category}.
     *         Returns null if the input {@code category} is null.
     */
    @Mapping(target = "itemCount", expression = "java(category.getItems() != null ? category.getItems().size() : 0)")
    CategoryDto toDto(Category category);

    /**
     * Maps a list of {@code Category} entities to a list of {@code CategoryDto} objects.
     *
     * @param categories the list of {@code Category} entities to be mapped
     * @return a list of {@code CategoryDto} objects containing the mapped data from the input {@code Category} entities
     */
    List<CategoryDto> toDtoList(List<Category> categories);
}