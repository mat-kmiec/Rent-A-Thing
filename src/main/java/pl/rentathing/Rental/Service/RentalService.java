package pl.rentathing.Rental.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rentathing.Rental.Dto.RentalCreateDto;
import pl.rentathing.Rental.Entity.DeliveryMethod;
import pl.rentathing.Rental.Entity.PaymentMethod;
import pl.rentathing.Rental.Entity.Rental;
import pl.rentathing.Rental.Entity.RentalStatus;
import pl.rentathing.Rental.Repository.RentalRepository;
import pl.rentathing.Rental.exception.DateNotAvailableException;
import pl.rentathing.auth.service.AuthService;
import pl.rentathing.item.entity.Item;
import pl.rentathing.item.exception.ItemNotFoundException;
import pl.rentathing.item.repository.ItemRepository;
import pl.rentathing.user.entity.User;
import pl.rentathing.user.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RentalService {


    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;
    private final RentalPriceCalculator priceCalculator;
    private final AuthService authService;
    private final RentalAvailibilityService rentalAvailibilityService;

    @Transactional
    public void createRental(RentalCreateDto dto) {
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException(dto.getItemId().toString()));

        if (!rentalAvailibilityService.isAvailable(item.getId(), dto.getStartDate(), dto.getEndDate())) {
            throw new DateNotAvailableException();
        }

        User user = authService.getCurrentUser();

        long days = priceCalculator.calculateRentalDays(dto.getStartDate(), dto.getEndDate());
        BigDecimal pricePerDay = priceCalculator.calculateDiscountedPrice(item);
        BigDecimal shippingCost = priceCalculator.calculateShippingCost(item, dto.getDeliveryMethod());
        BigDecimal deposit = priceCalculator.calculateDeposit(item);

        BigDecimal rentalTotal = pricePerDay.multiply(BigDecimal.valueOf(days));
        BigDecimal totalCost = rentalTotal.add(shippingCost).add(deposit);

        Rental rental = Rental.builder()
                .item(item)
                .user(user)
                .startDateTime(dto.getStartDate().atStartOfDay())
                .endDateTime(dto.getEndDate().atTime(23, 59, 59))
                .totalCost(totalCost)
                .deposit(deposit)
                .shippingCost(shippingCost)
                .deliveryMethod(DeliveryMethod.valueOf(dto.getDeliveryMethod().toUpperCase()))
                .paymentMethod(PaymentMethod.valueOf(dto.getPayment().toUpperCase()))
                .status(RentalStatus.PENDING)
                .depositPaid(!item.isDeposit())
                .createdAt(LocalDateTime.now())
                .build();

        rentalRepository.save(rental);
    }

    public RentalCreateDto prepareRentalDto(Long itemId, LocalDate startDate, LocalDate endDate) {
        User user = authService.getCurrentUser();

        RentalCreateDto dto = new RentalCreateDto();
        dto.setItemId(itemId);
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setDeliveryMethod("PICKUP");
        dto.setPayment("BLIK");
        dto.setStartDate(startDate != null ? startDate : LocalDate.now());
        dto.setEndDate(endDate != null ? endDate : LocalDate.now().plusDays(1));

        return dto;
    }



}
