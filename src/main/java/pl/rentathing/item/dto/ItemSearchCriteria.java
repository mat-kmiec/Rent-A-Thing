package pl.rentathing.item.dto;

import org.springframework.data.domain.Sort;

import java.math.BigDecimal;

/**
 * Encapsulates search criteria for filtering and retrieving items.
 *
 * This record is used to define the parameters for fetching items based on specific
 * filtering conditions and sorting preferences. It allows flexibility in defining
 * multiple parameters to refine the search results.
 *
 * Fields:
 * - query: A search term or keyword for filtering items based on their title or description.
 * - categoryId: The identifier of the category to filter items by.
 * - priceFrom: The minimum price to include in the search results.
 * - priceTo: The maximum price to include in the search results.
 * - availableOnly: If true, filters the results to include only items that are available.
 * - sort: The sorting criteria for the search results (e.g., "price" for sorting by price).
 */
public record ItemSearchCriteria(
        String query,
        Long categoryId,
        BigDecimal priceFrom,
        BigDecimal priceTo,
        Boolean availableOnly,
        String sort
){}