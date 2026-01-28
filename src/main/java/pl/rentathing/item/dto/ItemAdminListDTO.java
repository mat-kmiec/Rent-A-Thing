package pl.rentathing.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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