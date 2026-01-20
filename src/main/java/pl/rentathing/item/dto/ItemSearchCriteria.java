package pl.rentathing.item.dto;

import java.math.BigDecimal;

public record ItemSearchCriteria(
        String query,
        Long categoryId,
        BigDecimal priceFrom,
        BigDecimal priceTo,
        Boolean availableOnly
) {}