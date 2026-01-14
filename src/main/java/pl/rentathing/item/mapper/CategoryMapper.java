package pl.rentathing.item.mapper;

import org.mapstruct.Mapper;
import pl.rentathing.item.dto.CategoryDto;
import pl.rentathing.item.entity.Category;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toDto(Category category);
    List<CategoryDto> toDtoList(List<Category> categories);
}
