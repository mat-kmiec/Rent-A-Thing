package pl.rentathing.Rental.Service;

import jakarta.persistence.EntityNotFoundException;
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
import pl.rentathing.item.dto.ItemSearchDto;
import pl.rentathing.item.entity.Item;
import pl.rentathing.item.exception.ItemNotFoundException;
import pl.rentathing.item.repository.ItemRepository;
import pl.rentathing.user.dto.UserSearchDto;
import pl.rentathing.user.entity.User;
import pl.rentathing.user.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service providing operations for managing rental processes.
 * This class handles the creation, retrieval, updating, and cancellation
 * of rental data, as well as processing rental returns and managing
 * related user and item data.
 *
 * This service interacts with repositories and utilities for managing items,
 * users, and rentals, ensuring business rules are enforced.
 */
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

    /**
     * Creates a new rental based on the provided rental creation details and stores it in the system.
     * Validates item availability, calculates costs, and sets the rental status.
     *
     * @param dto the rental creation details, including item ID, start and end dates, delivery method, and payment method
     * @return a summary of the created rental, including its ID, item title, total cost, payment method, and status
     * @throws ItemNotFoundException if the item with the given ID is not found in the repository
     * @throws DateNotAvailableException if the item is not available for the requested rental period
     */
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

    /**
     * Prepares and returns a RentalCreateDto object based on the provided item ID, start date,
     * and end date. The method populates relevant data including user information, delivery method,
     * payment method, and rental period.
     *
     * @param itemId the ID of the item to be rented
     * @param startDate the start date of the rental period; if null, defaults to the current date
     * @param endDate the end date of the rental period; if null, defaults to the next day after the current date
     * @return a RentalCreateDto object containing the rental details
     */
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

    /**
     * Retrieves the rental history of a user based on filtering and sorting criteria.
     *
     * @param user the user whose rental history is being retrieved
     * @param search a search keyword to filter rental history by relevant details; can be null or empty
     * @param status the rental status filter (e.g., active, completed); null or empty means no status filtering
     * @param sort the sort order for rental history, either "asc" for ascending or "desc" for descending
     * @param page the page number for pagination, starting from 0
     * @return a paginated list of rental history data transfer objects (DTOs) matching the filtering and sorting criteria
     */
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

    /**
     * Retrieves rental details for a specific rental ID and user.
     *
     * @param id the unique identifier of the rental
     * @param user the user associated with the rental
     * @return a RentalDetailsDto object containing the rental details
     * @throws RentalNotFoundException if no rental is found for the given ID and user
     */
    public RentalDetailsDto getRentalDetails(Long id, User user) {
        Rental rental = rentalRepository.findByIdAndUser(id, user)
                .orElseThrow(RentalNotFoundException::new);

        return rentalMapper.toDetailsDto(rental);
    }

    /**
     * Parses the given status string and converts it to a corresponding RentalStatus enum value.
     * If the status is null, empty, or does not match any RentalStatus, the method returns null.
     *
     * @param status the string representation of the status to be parsed
     * @return the corresponding RentalStatus enum value, or null if the input is invalid or cannot be parsed
     */
    private RentalStatus parseStatus(String status) {
        if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("Wszystkie")) {
            try { return RentalStatus.valueOf(status); } catch (Exception ignored) {}
        }
        return null;
    }

    /**
     * Retrieves a paginated list of rentals filtered by the provided parameters.
     *
     * @param search    the search query string; can be null or blank to exclude from filtering.
     * @param status    the status of the rentals to filter; can be null to include all statuses.
     * @param category  the category of rentals to filter; can be null or blank to exclude from filtering.
     * @param returnDate the return date to filter rentals by; can be null or blank to exclude from filtering.
     * @param pageable  the pagination information.
     * @return a paginated list of rentals represented as RentalAdminListDto objects.
     */
    public Page<RentalAdminListDto> getRentals(String search, RentalStatus status, String category, String returnDate, Pageable pageable) {
        String cleanSearch = (search != null && !search.isBlank()) ? search : null;
        String cleanCategory = (category != null && !category.isBlank()) ? category : null;
        java.time.LocalDate date = (returnDate != null && !returnDate.isBlank())
                ? java.time.LocalDate.parse(returnDate)
                : null;

        return rentalRepository.findAllFiltered(cleanSearch, status, cleanCategory, date, pageable)
                .map(rentalMapper::toAdminListDto);

    }

    /**
     * Retrieves rental administrative details for a given rental ID.
     *
     * @param id the unique identifier of the rental
     * @return a RentalAdminDetailsDto object containing the rental details
     * @throws RentalNotFoundException if no rental is found with the given ID
     */
    public RentalAdminDetailsDto getRentalDetails(Long id) {
        return rentalRepository.findByIdWithDetails(id)
                .map(rentalMapper::toAdminDetailsDto)
                .orElseThrow(RentalNotFoundException::new);
    }

    /**
     * Marks the deposit as paid for a rental identified by the given ID.
     *
     * @param id the unique identifier of the rental for which the deposit should be marked as paid
     */
    @Transactional
    public void markDepositAsPaid(Long id) {
        Rental rental = rentalRepository.findById(id).orElseThrow();
        rental.setDepositPaid(true);
    }

    /**
     * Cancels an existing rental by setting its status to "CANCELLED" and updating the availability
     * of the associated item. The rental must exist in the repository; otherwise, an exception is thrown.
     *
     * @param id the unique identifier of the rental to be canceled
     */
    @Transactional
    public void cancelRental(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rental not found"));

        rental.setStatus(RentalStatus.CANCELLED);
        rental.getItem().setAvailable(true);

        rentalRepository.save(rental);
    }

    /**
     * Updates the status of a rental identified by its ID. Depending on the new status,
     * it also updates the availability of the associated item.
     *
     * @param id the ID of the rental to be updated
     * @param newStatus the new status to be set for the rental
     * @throws EntityNotFoundException if no rental is found with the given ID
     */
    @Transactional
    public void updateStatus(Long id, RentalStatus newStatus) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono wypożyczenia"));

        rental.setStatus(newStatus);

        if (newStatus == RentalStatus.COMPLETED || newStatus == RentalStatus.CANCELLED) {
            rental.getItem().setAvailable(true);
        }

        if (newStatus == RentalStatus.ACTIVE) {
            rental.getItem().setAvailable(false);
        }

        rentalRepository.save(rental);
    }

    /**
     * Processes the return of a rental by updating its status, return date, return notes,
     * and marking the associated item as available if applicable.
     *
     * @param id the unique identifier of the rental to process
     * @param returnNotes notes or comments provided for the return process
     */
    @Transactional
    public void processReturn(Long id, String returnNotes) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono wypożyczenia o ID: " + id));

        rental.setStatus(RentalStatus.COMPLETED);
        rental.setReturnDateTime(LocalDateTime.now());
        rental.setReturnNotes(returnNotes);

        if (rental.getItem() != null) {
            rental.getItem().setAvailable(true);
        }

        rentalRepository.save(rental);
    }

    /**
     * Searches for users based on the provided query string, specifically for administrative purposes.
     *
     * @param query the search query used to find matching users; it can be a part of a username, email, or other identifying information.
     * @return a list of UserSearchDto objects containing the search results, limited to a maximum of 10 items.
     */
    @Transactional(readOnly = true)
    public List<UserSearchDto> searchUsersForAdmin(String query) {
        return userRepository.searchUsersForAdmin(query, PageRequest.of(0, 10))
                .stream()
                .map(rentalMapper::toUserSearchDto)
                .toList();
    }

    /**
     * Searches for items matching the given query string, specifically for administrative purposes.
     * This method retrieves items from the repository and maps them to a list of ItemSearchDto objects
     * for easy representation and consumption.
     *
     * @param query the search query string used to filter and retrieve items
     * @return a list of ItemSearchDto objects representing the matching items
     */
    public List<ItemSearchDto> searchItemsForAdmin(String query) {
        List<Item> items = itemRepository.searchAllItemsForAdmin(query, PageRequest.of(0, 10));

        return items.stream()
                .map(item -> new ItemSearchDto(
                        item.getId(),
                        item.getTitle(),
                        item.getSku(),
                        item.getImageUrl(),
                        item.getPricePerDay(),
                        item.getDepositPrice() != null ? item.getDepositPrice() : BigDecimal.ZERO,
                        item.getShippingPrice() != null ? item.getShippingPrice() : BigDecimal.ZERO,
                        item.getCanBeShipped() != null ? item.getCanBeShipped() : false
                ))
                .collect(Collectors.toList());
    }

    /**
     * Creates a rental by an administrator. This method validates the availability of the item for the given dates
     * and calculates the rental costs including shipping and deposit. If valid, it persists the rental details,
     * and updates the item's availability.
     *
     * @param dto the data transfer object containing the rental details such as item ID, user ID, start date,
     *            end date, delivery method, payment method, and whether the deposit is marked as paid
     * @throws ItemNotFoundException if the item with the specified ID is not found
     * @throws EntityNotFoundException if the user with the specified ID is not found
     * @throws DateNotAvailableException if the item is not available for the given date range
     */
    @Transactional
    public void createRentalByAdmin(RentalAdminCreateDto dto) {
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("Nie znaleziono przedmiotu"));

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono użytkownika"));

        if (!rentalAvailibilityService.isAvailable(item.getId(), dto.getStartDate(), dto.getEndDate())) {
            throw new DateNotAvailableException();
        }

        long days = priceCalculator.calculateRentalDays(dto.getStartDate(), dto.getEndDate());
        BigDecimal pricePerDay = priceCalculator.calculateDiscountedPrice(item);
        BigDecimal shippingCost = priceCalculator.calculateShippingCost(item, dto.getDeliveryMethod().name());
        BigDecimal deposit = priceCalculator.calculateDeposit(item);
        BigDecimal totalCost = pricePerDay.multiply(BigDecimal.valueOf(days)).add(shippingCost);

        Rental rental = Rental.builder()
                .item(item)
                .user(user)
                .startDateTime(dto.getStartDate().atStartOfDay())
                .endDateTime(dto.getEndDate().atTime(23, 59, 59))
                .totalCost(totalCost)
                .shippingCost(shippingCost)
                .deposit(deposit)
                .deliveryMethod(dto.getDeliveryMethod())
                .paymentMethod(dto.getPaymentMethod())
                .status(RentalStatus.ACTIVE)
                .depositPaid(dto.isMarkDepositPaid())
                .invoiceRequested(false)
                .createdAt(LocalDateTime.now())
                .build();

        if (rental.getStatus() == RentalStatus.ACTIVE) {
            item.setAvailable(false);
        }

        rentalRepository.save(rental);
    }
}
