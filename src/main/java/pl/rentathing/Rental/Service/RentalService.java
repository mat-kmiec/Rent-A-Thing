package pl.rentathing.Rental.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rentathing.Rental.Dto.*;
import pl.rentathing.Rental.Entity.DeliveryMethod;
import pl.rentathing.Rental.Entity.PaymentMethod;
import pl.rentathing.Rental.Entity.Rental;
import pl.rentathing.Rental.Entity.RentalStatus;
import pl.rentathing.Rental.Mapper.RentalMapper;
import pl.rentathing.Rental.Repository.RentalRepository;
import pl.rentathing.Rental.exception.DateNotAvailableException;
import pl.rentathing.Rental.exception.RentalNotFoundException;
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
    private final RentalMapper rentalMapper;

    @Transactional
    public RentalSummaryDto createRental(RentalCreateDto dto) {
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
        BigDecimal totalCost = rentalTotal.add(shippingCost);

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

        Rental savedRental = rentalRepository.save(rental);

        return new RentalSummaryDto(
                savedRental.getId(),
                item.getTitle(),
                savedRental.getTotalCost(),
                savedRental.getPaymentMethod().toString(),
                savedRental.getStatus().toString());
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

    public Page<RentalHistoryDto> getUserRentalHistory(User user, String search, String status, String sort, int page) {
        Sort sortOrder = sort.equalsIgnoreCase("asc") ?
                Sort.by("startDateTime").ascending() :
                Sort.by("startDateTime").descending();

        Pageable pageable = PageRequest.of(page, 10, sortOrder);

        RentalStatus rentalStatus = parseStatus(status);
        String searchParam = (search == null || search.isEmpty()) ? null : search;
        return rentalRepository.findFilteredRentals(user, searchParam, rentalStatus, pageable)
                .map(rentalMapper::toHistoryDto);
    }

    public RentalDetailsDto getRentalDetails(Long id, User user) {
        Rental rental = rentalRepository.findByIdAndUser(id, user)
                .orElseThrow(RentalNotFoundException::new);

        return rentalMapper.toDetailsDto(rental);
    }

    private RentalStatus parseStatus(String status) {
        if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("Wszystkie")) {
            try { return RentalStatus.valueOf(status); } catch (Exception ignored) {}
        }
        return null;
    }

    public Page<RentalAdminListDto> getRentals(String search, RentalStatus status, String category, String returnDate, Pageable pageable) {
        String cleanSearch = (search != null && !search.isBlank()) ? search : null;
        String cleanCategory = (category != null && !category.isBlank()) ? category : null;
        java.time.LocalDate date = (returnDate != null && !returnDate.isBlank())
                ? java.time.LocalDate.parse(returnDate)
                : null;

        return rentalRepository.findAllFiltered(cleanSearch, status, cleanCategory, date, pageable)
                .map(rentalMapper::toAdminListDto);
    }




}
