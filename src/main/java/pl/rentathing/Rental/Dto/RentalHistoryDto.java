package pl.rentathing.Rental.Dto;

import lombok.Builder;
import lombok.Data;
import pl.rentathing.Rental.Entity.RentalStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
