package pl.rentathing.Rental.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rentathing.Rental.Dto.RentalCreateDto;
import pl.rentathing.Rental.Entity.DeliveryMethod;
import pl.rentathing.Rental.Entity.PaymentMethod;
import pl.rentathing.Rental.Entity.Rental;
import pl.rentathing.Rental.Entity.RentalStatus;
import pl.rentathing.Rental.Repository.RentalRepository;
import pl.rentathing.auth.exception.UnautorizedException;
import pl.rentathing.item.entity.Item;
import pl.rentathing.item.exception.ItemNotFoundException;
import pl.rentathing.item.repository.ItemRepository;
import pl.rentathing.user.entity.User;
import pl.rentathing.user.exception.UserNotFoundException;
import pl.rentathing.user.repository.UserRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class RentalService {


    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;

    @Transactional
    public void createRental(RentalCreateDto dto, Authentication authentication) {
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException(dto.getItemId().toString()));

        if(authentication == null) throw new UnautorizedException();
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UserNotFoundException(authentication.getName()));
        long days = ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate());
        if(days <= 0) days = 1;

        BigDecimal pricePerDay = item.getPricePerDay();
        if (item.getDiscountedPercent() != null && item.getDiscountedPercent() > 0) {
            BigDecimal discountMultiplier = BigDecimal.valueOf(100 - item.getDiscountedPercent());
            pricePerDay = pricePerDay.multiply(discountMultiplier)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        BigDecimal rentalTotal = pricePerDay.multiply(BigDecimal.valueOf(days));
        DeliveryMethod selectedMethod = DeliveryMethod.valueOf(dto.getDeliveryMethod());
        BigDecimal shippingCost = BigDecimal.ZERO;

        if (selectedMethod == DeliveryMethod.DELIVERY) {
            if (item.getShippingPrice() != null) {
                shippingCost = item.getShippingPrice();
            } else {
                shippingCost = BigDecimal.valueOf(25);
            }
        }

        BigDecimal deposit = (item.isDeposit() && item.getDepositPrice() != null)
                ? item.getDepositPrice()
                : BigDecimal.ZERO;

        BigDecimal totalCost = rentalTotal.add(shippingCost).add(deposit);

        Rental rental = Rental.builder()
                .item(item)
                .user(user)
                .startDateTime(dto.getStartDate().atStartOfDay())
                .endDateTime(dto.getEndDate().atTime(23, 59, 59))
                .totalCost(totalCost)
                .deposit(deposit)
                .shippingCost(shippingCost)
                .deliveryMethod(selectedMethod)
                .paymentMethod(PaymentMethod.valueOf(dto.getPayment().toUpperCase()))
                .status(RentalStatus.PENDING)
                .depositPaid(item.isDeposit())
                .build();

        rentalRepository.save(rental);
    }

    public RentalCreateDto prepareRentalDto(Long itemId, LocalDate startDate, LocalDate endDate, Authentication authentication) {
        if(authentication == null) throw new UnautorizedException();
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UserNotFoundException(authentication.getName()));

        RentalCreateDto dto = new RentalCreateDto();
        dto.setItemId(itemId);
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setDeliveryMethod("PICKUP");
        dto.setPayment("ONLINE");
        dto.setStartDate(startDate != null ? startDate : LocalDate.now());
        dto.setEndDate(endDate != null ? endDate : LocalDate.now().plusDays(1));

        return dto;
    }

}
