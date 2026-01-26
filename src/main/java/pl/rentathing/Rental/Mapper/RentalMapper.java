package pl.rentathing.Rental.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.rentathing.Rental.Dto.RentalDetailsDto;
import pl.rentathing.Rental.Dto.RentalHistoryDto;
import pl.rentathing.Rental.Entity.Rental;

@Mapper(componentModel = "spring")
public interface RentalMapper {

    @Mapping(target = "itemTitle", source = "item.title")
    @Mapping(target = "itemImageUrl", source = "item.imageUrl")
    RentalHistoryDto toHistoryDto(Rental rental);

    @Mapping(target = "itemTitle", source = "item.title")
    @Mapping(target = "itemImageUrl", source = "item.imageUrl")
    @Mapping(target = "itemId", source = "item.id")
    RentalDetailsDto toDetailsDto(Rental rental);
}
