package pl.rentathing.item.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ItemDetailsDTO {
    private Long id;
    private String title;
    private String description;
    private BigDecimal pricePerDay;
    private BigDecimal discountedPrice;
    private Integer discountedPercent;
    private String imageUrl;
    private String categoryName;
    private Double averageRating;
    private Integer reviewCount;
    private Boolean available;
    private Boolean canBePickedUp;
    private Boolean isNew;
    private Boolean canBeShipped;
    private BigDecimal depositPrice;
    private BigDecimal shippingPrice;
}
