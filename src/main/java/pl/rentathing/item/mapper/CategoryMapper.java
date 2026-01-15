package pl.rentathing.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.rentathing.item.dto.CategoryDto;
import pl.rentathing.item.entity.Category;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "itemCount", expression = "java(category.getItems() != null ? category.getItems().size() : 0)")
    CategoryDto toDto(Category category);

    List<CategoryDto> toDtoList(List<Category> categories);
}