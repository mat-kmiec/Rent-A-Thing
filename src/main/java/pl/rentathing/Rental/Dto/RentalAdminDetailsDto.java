package pl.rentathing.Rental.Dto;

import lombok.Getter;
import lombok.Setter;
import pl.rentathing.Rental.Entity.RentalStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a Data Transfer Object (DTO) containing detailed administrative data
 * about a rental transaction. This class is utilized for transferring comprehensive
 * rental-related information, including user, item, and transaction details,
 * to administrative interfaces or services.
 *
 * The object encapsulates the following main categories of information:
 *
 * - Rental Information:
 *   Contains the rental's unique identifier, status, related timestamps, and notes
 *   for handover or return operations.
 *
 * - User Details:
 *   Includes information about the user associated with the rental, such as the
 *   user's ID, full name, email, phone number, and full address.
 *
 * - Item Details:
 *   Provides details about the rented item, including the item ID, title, SKU
 *   (Stock Keeping Unit), category name, category icon, and image URL.
 *
 * - Financial Details:
 *   Covers monetary aspects of the rental, such as total cost, deposit amount,
 *   shipping cost, whether the deposit was paid, payment method, and whether an
 *   invoice was requested.
 *
 * - Rental Timing:
 *   Details the start, end, and return timestamps, along with the duration of the
 *   rental in days.
 *
 * - Additional Flags:
 *   Indicates whether the rental is overdue and provides a badge class for status
 *   visualization in the user interface.
 *
 * This DTO serves administrative purposes and equips the administrator with
 * all critical data for managing rentals efficiently in the system.
 */
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