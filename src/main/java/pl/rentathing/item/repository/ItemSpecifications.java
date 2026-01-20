package pl.rentathing.item.repository;

import org.springframework.data.jpa.domain.Specification;
import pl.rentathing.item.dto.ItemSearchCriteria;
import pl.rentathing.item.entity.Item;

public class ItemSpecifications {

    public static Specification<Item> build(ItemSearchCriteria criteria) {
        return Specification.where(titleOrDescriptionLike(criteria.query()))
                .and(hasCategory(criteria.categoryId()))
                .and(priceBetween(criteria.priceFrom(), criteria.priceTo()))
                .and(isAvailable(criteria.availableOnly()));
    }

    private static Specification<Item> titleOrDescriptionLike(String query) {
        return (root, q, cb) -> (query == null || query.isBlank()) ? null :
                cb.or(
                        cb.like(cb.lower(root.get("title")), "%" + query.toLowerCase() + "%"),
                        cb.like(cb.lower(root.get("description")), "%" + query.toLowerCase() + "%")
                );
    }

    private static Specification<Item> hasCategory(Long categoryId) {
        return (root, q, cb) -> categoryId == null ? null :
                cb.equal(root.get("category").get("id"), categoryId);
    }

    private static Specification<Item> priceBetween(java.math.BigDecimal from, java.math.BigDecimal to) {
        return (root, q, cb) -> {
            if (from == null && to == null) return null;
            if (from != null && to == null) return cb.ge(root.get("pricePerDay"), from);
            if (from == null) return cb.le(root.get("pricePerDay"), to);
            return cb.between(root.get("pricePerDay"), from, to);
        };
    }

    private static Specification<Item> isAvailable(Boolean availableOnly) {
        return (root, q, cb) -> (availableOnly != null && availableOnly) ?
                cb.isTrue(root.get("available")) : null;
    }
}