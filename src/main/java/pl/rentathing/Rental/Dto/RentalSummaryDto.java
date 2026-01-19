package pl.rentathing.Rental.Dto;

import java.math.BigDecimal;

public record RentalSummaryDto(
        Long rentalId,
        String itemTitle,
        BigDecimal totalCost,
        String paymentMethod,
        String status
) {}
