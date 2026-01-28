package pl.rentathing.Rental.Dto;

import lombok.Builder;
import lombok.Data;
import pl.rentathing.Rental.Entity.RentalStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) representing the rental history of an item.
 *
 * This class encapsulates information associated with past rental records,
 * providing details necessary for displaying or processing historical rental data.
 *
 * Fields:
 * - id: Unique identifier of the rental record.
 * - itemTitle: Name or title of the rented item.
 * - itemImageUrl: URL of the image representing the rented item.
 * - startDateTime: The date and time when the rental began.
 * - endDateTime: The date and time when the rental ended.
 * - totalCost: Total cost incurred for the rental.
 * - status: Current status of the rental, indicating its lifecycle stage.
 */
@Data
@Builder
public class RentalHistoryDto {
    private Long id;
    private String itemTitle;
    private String itemImageUrl;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private BigDecimal totalCost;
    private RentalStatus status;
}
