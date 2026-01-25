package pl.rentathing.item.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import pl.rentathing.item.review.Review;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false, length = 100)
    private String title;

    @Column(length = 5000, nullable = false)
    private String description;

    @PositiveOrZero
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerDay;

    @Min(0)
    @Max(100)
    private Integer discountedPercent;

    @Builder.Default
    private boolean deposit = false;

    @PositiveOrZero
    private BigDecimal depositPrice;

    @Column(length = 1024)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @DecimalMin("0.0")
    @DecimalMax("5.0")
    private Double averageRating;

    @PositiveOrZero
    @Builder.Default
    private Integer reviewCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean available = true;

    @Builder.Default
    private Boolean canBeShipped = true;

    @Builder.Default
    private Boolean canBePickedUp = true;

    @PositiveOrZero
    private BigDecimal shippingPrice;

    @Builder.Default
    private Boolean isNew = true;

    @UpdateTimestamp
    private LocalDateTime lastModified;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<Review> reviews;

    @NotBlank
    @Column(unique = true, length = 50)
    private String sku;

    @Min(1)
    @Builder.Default
    private Integer minRentalDays = 1;

}
