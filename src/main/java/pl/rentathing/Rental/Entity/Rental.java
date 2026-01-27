package pl.rentathing.Rental.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import pl.rentathing.item.entity.Item;
import pl.rentathing.user.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rentals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    private LocalDateTime startDateTime;

    @NotNull
    private LocalDateTime endDateTime;

    private LocalDateTime returnDateTime;

    @PositiveOrZero
    @Column(precision = 10, scale = 2)
    private BigDecimal totalCost;

    @PositiveOrZero
    @Column(precision = 10, scale = 2)
    private BigDecimal deposit;

    @Enumerated(EnumType.STRING)
    private DeliveryMethod deliveryMethod;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    @Builder.Default
    private boolean depositPaid = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean invoiceRequested = false;

    @PositiveOrZero
    private BigDecimal shippingCost;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RentalStatus status = RentalStatus.NEW;

    @Column(length = 1024)
    private String handOverNotes;

    @Column(length = 1024)
    private String returnNotes;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

}
