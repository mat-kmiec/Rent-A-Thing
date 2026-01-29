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

/**
 * The ItemMapper interface provides mapping functionalities between
 * the Item entity and its corresponding DTOs such as ItemDetailsDTO
 * and ItemFormDTO. It simplifies the transformation of domain models
 * into transferable objects and vice versa, ensuring consistency and
 * proper handling of fields during the mapping process.
 * <br>
 * This interface uses MapStruct to generate the implementation at compile-time
 * and follows Spring's component model for dependency injection with the specified
 * unmapped target policy set to ignore unmapped fields.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ItemMapper {

    /**
     * Maps an {@code Item} entity to an {@code ItemDetailsDTO}.
     *
     * @param item the {@code Item} entity containing the data to be mapped to the DTO
     * @return an {@code ItemDetailsDTO} containing the mapped values from the {@code Item} entity
     */
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "discountedPrice", expression = "java(calculateDiscountedPrice(item))")
    ItemDetailsDTO toDetailsDTO(Item item);

    /**
     * Calculates the discounted price for a given item based on its discount percentage.
     * If the item does not have a valid discount percentage (null or non-positive),
     * the original price per day is returned. Otherwise, the discounted price
     * is computed by applying the discount percentage to the item's price per day.
     *
     * @param item the item whose discounted price is to be calculated. The item must not be null
     *             and should have a valid pricePerDay and optionally a discount percentage.
     * @return the discounted price of the item as a {@code BigDecimal}.
     *         Returns the original pricePerDay if no valid discount is applied.
     */
    default BigDecimal calculateDiscountedPrice(Item item) {
        if (item.getDiscountedPercent() == null || item.getDiscountedPercent() <= 0) {
            return item.getPricePerDay();
        }
        BigDecimal discount = item.getPricePerDay()
                .multiply(BigDecimal.valueOf(item.getDiscountedPercent()))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return item.getPricePerDay().subtract(discount);
    }

    /**
     * Maps an {@code Item} entity to an {@code ItemFormDTO}.
     *
     * @param item the {@code Item} entity containing the data to be mapped to the DTO
     * @return an {@code ItemFormDTO} containing the mapped values from the {@code Item} entity
     */
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "currentImageUrl", source = "imageUrl")
    @Mapping(target = "imageFile", ignore = true)
    ItemFormDTO toDto(Item item);

    /**
     * Updates the fields of an existing {@code Item} entity with the data from the provided {@code ItemFormDTO}
     * while ignoring specific fields during the update process. This method utilizes MapStruct for mapping
     * between the DTO and entity.
     *
     * @param dto the {@code ItemFormDTO} containing updated values to map to the {@code Item} entity
     * @param item the existing {@code Item} entity to be updated; it is the mapping target
     */
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
