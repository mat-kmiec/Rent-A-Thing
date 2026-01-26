package pl.rentathing.Rental.Dto;

import lombok.Builder;
import lombok.Data;
import pl.rentathing.Rental.Entity.RentalStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class RentalDetailsDto {
    private Long id;
    private String itemTitle;
    private String itemImageUrl;
    private Long itemId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private LocalDateTime returnDateTime;
    private BigDecimal totalCost;
    private BigDecimal deposit;
    private BigDecimal shippingCost;
    private String deliveryMethod;
    private String paymentMethod;
    private boolean depositPaid;
    private RentalStatus status;
    private String handOverNotes;
    private String returnNotes;
}