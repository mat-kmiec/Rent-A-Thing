package pl.rentathing.item.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

/**
 * Represents a Data Transfer Object (DTO) for handling item form submissions.
 *
 * This class is used to transfer user-provided data for creating or updating an item.
 * It encapsulates various fields required for item management, including item details,
 * pricing, rental options, and image handling. The class also includes validation
 * annotations to ensure data integrity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemFormDTO {

    private Long id;

    @NotBlank(message = "Nazwa jest wymagana")
    @Size(max = 100, message = "Nazwa może mieć max 100 znaków")
    private String title;
    @NotBlank(message = "Opis jest wymagany")
    @Size(max = 5000, message = "Opis jest zbyt długi")
    private String description;
    @NotNull(message = "Cena jest wymagana")
    @PositiveOrZero(message = "Cena nie może być ujemna")
    private BigDecimal pricePerDay;
    @Min(value = 0, message = "Rabat min. 0%")
    @Max(value = 100, message = "Rabat max. 100%")
    private Integer discountedPercent;
    private boolean deposit;
    @PositiveOrZero(message = "Kaucja nie może być ujemna")
    private BigDecimal depositPrice;
    private String currentImageUrl;
    private MultipartFile imageFile;
    @NotNull(message = "Kategoria jest wymagana")
    private Long categoryId;
    private Boolean available = true;
    private Boolean canBeShipped = true;
    private Boolean canBePickedUp = true;
    @PositiveOrZero(message = "Cena wysyłki nie może być ujemna")
    private BigDecimal shippingPrice;
    private Boolean isNew = true;
    @NotBlank(message = "SKU jest wymagane")
    @Size(max = 50, message = "SKU max 50 znaków")
    private String sku;
    @Min(value = 1, message = "Min. 1 dzień")
    private Integer minRentalDays = 1;
}
