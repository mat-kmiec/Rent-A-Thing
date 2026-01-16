package pl.rentathing.item.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.rentathing.item.dto.ItemDetailsDTO;
import pl.rentathing.item.entity.Item;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper(componentModel = "spring")
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


}
