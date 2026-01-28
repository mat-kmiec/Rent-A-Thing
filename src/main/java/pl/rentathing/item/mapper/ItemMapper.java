package pl.rentathing.item.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import pl.rentathing.item.dto.ItemDetailsDTO;
import pl.rentathing.item.dto.ItemFormDTO;
import pl.rentathing.item.entity.Item;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ItemMapper {

    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "discountedPrice", expression = "java(calculateDiscountedPrice(item))")
    ItemDetailsDTO toDetailsDTO(Item item);

    default BigDecimal calculateDiscountedPrice(Item item) {
        if (item.getDiscountedPercent() == null || item.getDiscountedPercent() <= 0) {
            return item.getPricePerDay();
        }
        BigDecimal discount = item.getPricePerDay()
                .multiply(BigDecimal.valueOf(item.getDiscountedPercent()))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return item.getPricePerDay().subtract(discount);
    }

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "currentImageUrl", source = "imageUrl")
    @Mapping(target = "imageFile", ignore = true)
    ItemFormDTO toDto(Item item);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "reviewCount", ignore = true)
    void updateEntityFromDto(ItemFormDTO dto, @MappingTarget Item item);


}
