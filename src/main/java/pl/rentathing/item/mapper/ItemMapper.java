package pl.rentathing.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.rentathing.item.dto.ItemDetailsDTO;
import pl.rentathing.item.entity.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "categoryName", source = "category.name")
    ItemDetailsDTO toDetailsDTO(Item item);


}
