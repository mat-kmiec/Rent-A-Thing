package pl.rentathing.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Represents a Data Transfer Object (DTO) for administrative item listing.
 *
 * This record is used to encapsulate essential details of items intended for
 * administrative purposes, such as managing item inventories, viewing item
 * availability, and categorizing items.
 *
 * Fields:
 * - id: Unique identifier of the item.
 * - title: Name or title of the item.
 * - description: A brief description of the item.
 * - categoryName: Name of the category to which the item belongs.
 * - pricePerDay: The daily rental price for the item.
 * - available: Indicates whether the item is available for renting (true/false).
 * - imageUrl: A URL linking to the item's representative image.
 */
@Builder
public record ItemAdminListDTO(
        Long id,
        String title,
        String description,
        String categoryName,
        BigDecimal pricePerDay,
        Boolean available,
        String imageUrl
) {}