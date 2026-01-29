package pl.rentathing.item.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Represents a Data Transfer Object (DTO) containing detailed item information.
 *
 * This class is designed to encapsulate comprehensive details of an item, making it suitable
 * for presenting item-related information in various contexts such as detailed views or API responses.
 *
 * Fields:
 * - id: Unique identifier of the item.
 * - title: Name or title of the item.
 * - description: A detailed description of the item.
 * - pricePerDay: The cost to rent the item per day.
 * - discountedPrice: The price of the item after applying a discount.
 * - discountedPercent: The percentage of the discount applied to the item.
 * - imageUrl: A URL linking to an image representing the item.
 * - categoryName: The name of the category the item belongs to.
 * - averageRating: The average user rating of the item.
 * - reviewCount: The total number of reviews for the item.
 * - available: Indicates whether the item is currently available for rent.
 * - canBePickedUp: Specifies if the item can be picked up by the renter.
 * - isNew: Indicates whether the item is new.
 * - canBeShipped: Specifies if the item is eligible for shipping.
 * - depositPrice: The refundable deposit amount required for renting the item.
 * - shippingPrice: The cost associated with shipping the item.
 */
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
