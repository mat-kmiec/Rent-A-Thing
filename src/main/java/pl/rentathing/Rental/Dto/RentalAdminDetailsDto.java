package pl.rentathing.Rental.Dto;

import lombok.Getter;
import lombok.Setter;
import pl.rentathing.Rental.Entity.RentalStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
public class RentalAdminDetailsDto {

    private Long id;
    private RentalStatus status;
    private String statusDisplayName;
    private String statusBadgeClass;


    private Long userId;
    private String userFullName;
    private String userEmail;
    private String userPhone;
    private String fullAddress;


    private Long itemId;
    private String itemTitle;
    private String itemSku;
    private String itemCategoryName;
    private String itemCategoryIcon;
    private String itemImageUrl;


    private BigDecimal totalCost = BigDecimal.ZERO;
    private BigDecimal deposit = BigDecimal.ZERO;
    private BigDecimal shippingCost = BigDecimal.ZERO;
    private boolean depositPaid;
    private boolean invoiceRequested;
    private String paymentMethod;


    private String deliveryMethod;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private LocalDateTime returnDateTime;
    private LocalDateTime createdAt;


    private String handOverNotes;
    private String returnNotes;

    private boolean overdue;
    private long durationDays;
}