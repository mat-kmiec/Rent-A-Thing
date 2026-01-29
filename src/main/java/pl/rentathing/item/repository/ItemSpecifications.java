package pl.rentathing.item.repository;

import org.springframework.data.jpa.domain.Specification;
import pl.rentathing.item.dto.ItemSearchCriteria;
import pl.rentathing.item.entity.Item;

/**
 * Utility class to build JPA Specifications for filtering, searching, and querying {@code Item} entities.
 * This class provides static methods to build dynamic query specifications based on the provided {@code ItemSearchCriteria}.
 * Specifications are built for various filters such as text search, category, price range, and availability.
 */
public class ItemSpecifications {

    /**
     * Constructs a {@link Specification} for querying {@link Item} entities based on the provided search criteria.
     *
     * The specification combines multiple conditions including:
     * - Matching the title or description to the query string.
     * - Filtering by a specific category.
     * - Filtering by a range of price.
     * - Checking availability status.
     *
     * @param criteria the {@link ItemSearchCriteria} object containing search filters such as query text, category ID,
     *                 price range, and availability status.
     * @return a {@link Specification} of {@link Item} that can be used to retrieve matching results from the database.
     */
    public static Specification<Item> build(ItemSearchCriteria criteria) {
        return Specification.where(titleOrDescriptionLike(criteria.query()))
                .and(hasCategory(criteria.categoryId()))
                .and(priceBetween(criteria.priceFrom(), criteria.priceTo()))
                .and(isAvailable(criteria.availableOnly()));
    }

    /**
     * Constructs a specification to filter items where the title or description contains the given query string
     * (case-insensitive). If the query is null or blank, no predicate is applied.
     *
     * @param query the search string to look for in the title or description; may be null or blank.
     * @return a specification that filters items based on the title or description containing the query string,
     *         or null if the query is null or blank.
     */
    private static Specification<Item> titleOrDescriptionLike(String query) {
        return (root, q, cb) -> (query == null || query.isBlank()) ? null :
                cb.or(
                        cb.like(cb.lower(root.get("title")), "%" + query.toLowerCase() + "%"),
                        cb.like(cb.lower(root.get("description")), "%" + query.toLowerCase() + "%")
                );
    }

    /**
     * Creates a {@link Specification} to filter {@link Item} entities by their category ID.
     *
     * This method generates a specification that checks if the category ID of the {@link Item}
     * matches the specified value. If the provided category ID is null, no filtering is applied.
     *
     * @param categoryId the ID of the category to filter by; if null, the specification returns no condition
     * @return a {@link Specification} for filtering {@link Item} entities by category ID, or null if the category ID is null
     */
    private static Specification<Item> hasCategory(Long categoryId) {
        return (root, q, cb) -> categoryId == null ? null :
                cb.equal(root.get("category").get("id"), categoryId);
    }

    /**
     * Constructs a {@link Specification} for querying {@link Item} entities based on a specific range of prices.
     * If both `from` and `to` are null, the specification will not apply any price filtering.
     * If only `from` is provided, the specification will filter items with prices greater than or equal to `from`.
     * If only `to` is provided, the specification will filter items with prices less than or equal to `to`.
     * If both are provided, the specification will filter items with prices within the inclusive range `[from, to]`.
     *
     * @param from the minimum price for filtering; may be null if no lower bound is required
     * @param to the maximum price for filtering; may be null if no upper bound is required
     * @return a {@link Specification} of {@link Item} to filter by the specified price range, or null if no filtering is applied
     */
    private static Specification<Item> priceBetween(java.math.BigDecimal from, java.math.BigDecimal to) {
        return (root, q, cb) -> {
            if (from == null && to == null) return null;
            if (from != null && to == null) return cb.ge(root.get("pricePerDay"), from);
            if (from == null) return cb.le(root.get("pricePerDay"), to);
            return cb.between(root.get("pricePerDay"), from, to);
        };
    }

    /**
     * Creates a specification to filter items based on their availability status.
     *
     * @param availableOnly a Boolean flag indicating whether to filter only available items.
     *                       If true, the method returns a specification to filter items marked as available.
     *                       If false or null, no filtering is applied.
     * @return a Specification object for filtering items based on availability, or null if no filtering is applied.
     */
    private static Specification<Item> isAvailable(Boolean availableOnly) {
        return (root, q, cb) -> {
            if (availableOnly == null || !availableOnly) {
                return null;
            }
            return cb.isTrue(root.get("available"));
        };
    }
}