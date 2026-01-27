package pl.rentathing.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Represents a Data Transfer Object (DTO) for searching and retrieving item details.
 *
 * This class encapsulates essential information about an item, making it suitable
 * for item listings, search results, and other read-only operations where item details are needed.
 *
 * The class provides the following fields:
 * - id: Unique identifier of the item.
 * - title: The name or title of the item.
 * - sku: Stock Keeping Unit, representing a unique identifier for inventory management.
 * - imageUrl: A URL linking to an image representing the item.
 * - pricePerDay: The cost to rent the item per day.
 * - depositPrice: The refundable deposit amount required for renting the item.
 * - shippingPrice: The cost associated with shipping the item.
 * - canBeShipped: Indicates whether the item is eligible for shipping (true/false).
 */
@Getter
@Setter
@AllArgsConstructor
public class ItemSearchDto {
    private Long id;
    private String title;
    private String sku;
    private String imageUrl;
    private BigDecimal pricePerDay;
    private BigDecimal depositPrice;
    private BigDecimal shippingPrice;
    private Boolean canBeShipped;
}
