package pl.rentathing.Rental.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pl.rentathing.Rental.Dto.*;
import pl.rentathing.Rental.Entity.Rental;
import pl.rentathing.item.dto.ItemSearchDto;
import pl.rentathing.item.entity.Item;
import pl.rentathing.user.dto.UserSearchDto;
import pl.rentathing.user.entity.Address;
import pl.rentathing.user.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * RentalMapper is an interface responsible for defining mappings between entities and Data Transfer Objects (DTOs)
 * related to the rental domain. It uses the MapStruct framework to automatically generate the implementation
 * of mapping methods.
 *
 * The mappings cover various aspects of the rental data transformations, including user, item, rental status,
 * and other attributes. Custom mappings and transformations are also defined as default methods within the interface.
 *
 * Supported mappings include:
 * - History DTO transformation for representing rental history data.
 * - Details DTO transformation for presenting detailed rental information.
 * - Admin List DTO transformation for summarizing rental data for administrative purposes.
 * - Admin Details DTO transformation with expanded rental and user details for administrators.
 * - Export DTO transformation for exporting rental data in a specified format.
 * - Active Rental DTO transformation for ongoing or active rental data.
 * - Search DTO transformation for simplifying user or item search functionality.
 *
 * Custom logic methods such as `calculateTimeDiff` and `mapAddress` are defined to include specific computed
 * values or custom string formatting, such as remaining time for overdue rentals or full address string creation.
 *
 * This interface includes methods to handle single-entity transformations as well as bulk transformations
 * for lists of objects.
 */
@Mapper(componentModel = "spring", imports = {LocalDateTime.class, ChronoUnit.class})
public interface RentalMapper {

    /**
     * Maps a Rental entity to a RentalHistoryDto object.
     *
     * This method is responsible for converting relevant fields of a Rental entity,
     * such as the title and image URL of the rental item, into a RentalHistoryDto
     * representation for use in scenarios involving rental history.
     *
     * @param rental the Rental entity to be mapped. Must not be null.
     * @return a RentalHistoryDto containing the mapped data from the provided Rental entity.
     */
    @Mapping(target = "itemTitle", source = "item.title")
    @Mapping(target = "itemImageUrl", source = "item.imageUrl")
    RentalHistoryDto toHistoryDto(Rental rental);

    /**
     * Maps a Rental entity to a RentalDetailsDto object.
     *
     * This method is responsible for converting relevant fields from a Rental entity,
     * such as the item's title, image URL, and ID, into a RentalDetailsDto representation
     * for use in scenarios requiring detailed rental information.
     *
     * @param rental the Rental entity to be mapped. Must not be null.
     * @return a RentalDetailsDto containing the mapped data from the provided Rental entity.
     */
    @Mapping(target = "itemTitle", source = "item.title")
    @Mapping(target = "itemImageUrl", source = "item.imageUrl")
    @Mapping(target = "itemId", source = "item.id")
    RentalDetailsDto toDetailsDto(Rental rental);


    /**
     * Maps a Rental entity to a RentalAdminListDto object.
     *
     * This method is responsible for converting relevant fields of a Rental entity into
     * a RentalAdminListDto representation, which is used for administrative purposes such as
     * listing rental entries in an admin interface.
     *
     * @param rental the Rental entity to be mapped. Must not be null.
     * @return a RentalAdminListDto containing the mapped data from the provided Rental entity.
     */
    @Mapping(target = "itemTitle", source = "item.title")
    @Mapping(target = "categoryIcon", source = "item.category.iconClass")
    @Mapping(target = "userFullName", expression = "java(rental.getUser().getFirstName() + \" \" + rental.getUser().getLastName())")
    @Mapping(target = "userInitials", expression = "java(" +
            "(rental.getUser().getFirstName() != null && !rental.getUser().getFirstName().isEmpty() ? rental.getUser().getFirstName().substring(0,1) : \"\") + " +
            "(rental.getUser().getLastName() != null && !rental.getUser().getLastName().isEmpty() ? rental.getUser().getLastName().substring(0,1) : \"\")" +
            ")")
    @Mapping(target = "timeDiffMessage", expression = "java(calculateTimeDiff(rental))")
    @Mapping(target = "isOverdue", expression = "java(rental.getEndDateTime().isBefore(LocalDateTime.now()) && rental.getReturnDateTime() == null)")
    RentalAdminListDto toAdminListDto(Rental rental);

    /**
     * Calculates the time difference between the current time and the rental's end date.
     *
     * This method determines whether the rental is still ongoing, close to its deadline,
     * overdue, or completed. The return value provides a human-readable message indicating
     * the state or time difference in days, hours, or minutes.
     *
     * @param rental the rental for which the time difference is to be calculated. Must not be null.
     *               The rental must have a valid `endDateTime` value.
     * @return a string indicating the time difference message. Returns null if the rental
     *         has already been returned (i.e., `returnDateTime` is not null).
     */
    default String calculateTimeDiff(Rental rental) {
        if (rental.getReturnDateTime() != null) return null;
        LocalDateTime now = LocalDateTime.now();
        if (rental.getEndDateTime().isAfter(now)) {
            long hours = ChronoUnit.HOURS.between(now, rental.getEndDateTime());
            if (hours <= 0) {
                long mins = ChronoUnit.MINUTES.between(now, rental.getEndDateTime());
                return "Zostało " + mins + " min";
            }
            return hours < 24 ? "Zostało " + hours + "h" : "Zostało " + ChronoUnit.DAYS.between(now, rental.getEndDateTime()) + " dni";
        } else {
            long days = ChronoUnit.DAYS.between(rental.getEndDateTime(), now);
            return days == 0 ? "Dzisiaj po terminie" : "+" + days + " dni po terminie";
        }
    }

    /**
     * Maps a Rental entity to a RentalAdminDetailsDto object.
     *
     * This method is responsible for converting the provided Rental entity
     * into a detailed RentalAdminDetailsDto representation. Various properties
     * of the entity, such as the user's full name, contact details, item details,
     * rental status, and rental duration, are mapped to corresponding fields in
     * the DTO. Additional computed properties, such as overdue status and duration
     * in days, are also included.
     *
     * @param rental the Rental entity to be mapped. Must not be null. The rental
     *               should contain associated user, item, and status objects with
     *               valid data.
     * @return a RentalAdminDetailsDto containing the mapped data from the provided
     *         Rental entity, including computed and derived details.
     */
    @Mapping(target = "userFullName", expression = "java(rental.getUser().getFirstName() + \" \" + rental.getUser().getLastName())")
    @Mapping(target = "userEmail", source = "user.email")
    @Mapping(target = "userPhone", source = "user.phoneNumber")
    @Mapping(target = "fullAddress", source = "user.address", qualifiedByName = "mapAddress")
    @Mapping(target = "itemTitle", source = "item.title")
    @Mapping(target = "itemSku", source = "item.sku")
    @Mapping(target = "itemCategoryName", source = "item.category.name")
    @Mapping(target = "itemCategoryIcon", source = "item.category.iconClass")
    @Mapping(target = "itemImageUrl", source = "item.imageUrl")
    @Mapping(target = "statusDisplayName", source = "status.displayName")
    @Mapping(target = "statusBadgeClass", source = "status.badgeClass")
    @Mapping(target = "overdue", expression = "java(rental.getStatus() == pl.rentathing.Rental.Entity.RentalStatus.ACTIVE && java.time.LocalDateTime.now().isAfter(rental.getEndDateTime()))")
    @Mapping(target = "durationDays", expression = "java(java.time.Duration.between(rental.getStartDateTime(), rental.getEndDateTime()).toDays())")
    RentalAdminDetailsDto toAdminDetailsDto(Rental rental);

    /**
     * Formats an address into a readable string representation.
     *
     * This method processes the provided Address object, verifying its fields,
     * and constructs a formatted string. If the address is null, it returns
     * a default message indicating the absence of the address.
     *
     * @param address the Address object containing street, house number,
     *                optional apartment number, zip code, and city.
     *                Can be null, in which case a default message is returned.
     * @return a formatted string representing the address.
     *         Returns "Brak adresu" if the address is null.
     */
    @Named("mapAddress")
    default String mapAddress(Address address) {
        if (address == null) return "Brak adresu";
        String apt = (address.getApartmentNumber() != null && !address.getApartmentNumber().isEmpty())
                ? "/" + address.getApartmentNumber() : "";
        return String.format("%s %s%s, %s %s",
                address.getStreet(), address.getHouseNumber(), apt, address.getZipCode(), address.getCity());
    }

    /**
     * Maps a Rental entity to a RentalExportDto object with specified field mappings
     * and custom formatting for certain attributes.
     *
     * @param rental the Rental entity to be mapped to a RentalExportDto
     * @return a RentalExportDto object containing the mapped and formatted data from the input Rental entity
     */
    @Mapping(target = "itemTitle", source = "item.title")
    @Mapping(target = "userFullName", expression = "java(rental.getUser().getFirstName() + \" \" + rental.getUser().getLastName())")
    @Mapping(target = "startDateTime", source = "startDateTime", dateFormat = "yyyy-MM-dd HH:mm")
    @Mapping(target = "endDateTime", source = "endDateTime", dateFormat = "yyyy-MM-dd HH:mm")
    @Mapping(target = "status", source = "status.displayName")
    @Mapping(target = "totalCost", expression = "java(rental.getTotalCost() != null ? rental.getTotalCost().toString() + \" zł\" : \"0 zł\")")
    RentalExportDto toExportDto(Rental rental);

    List<RentalExportDto> toExportDtoList(List<Rental> rentals);

    /**
     * Maps a User entity to a UserSearchDto object.
     *
     * This method is responsible for converting relevant fields of a User entity,
     * such as the user's ID, full name, and email, into a UserSearchDto representation
     * for scenarios involving user search operations.
     *
     * @param user the User entity to be mapped. Must not be null.
     * @return a UserSearchDto containing the mapped data from the provided User entity.
     */
    default UserSearchDto toUserSearchDto(User user) {
        return new UserSearchDto(
                user.getId(),
                user.getFirstName() + " " + user.getLastName() + " (" + user.getEmail() + ")",
                user.getEmail()
        );
    }

    /**
     * Converts an {@link Item} object into an {@link ItemSearchDto} object.
     *
     * @param item the {@link Item} object to be converted
     * @return the converted {@link ItemSearchDto} containing the relevant fields
     */
    default ItemSearchDto toItemSearchDto(Item item) {
        return new ItemSearchDto(
                item.getId(),
                item.getTitle(),
                item.getSku(),
                item.getImageUrl(),
                item.getPricePerDay(),
                item.getDepositPrice() != null ? item.getDepositPrice() : BigDecimal.ZERO,
                item.getShippingPrice() != null ? item.getShippingPrice() : BigDecimal.ZERO,
                item.getCanBeShipped() != null ? item.getCanBeShipped() : false
        );
    }

    /**
     * Converts a Rental entity to an ActiveRentalDTO.
     * Maps properties from the source entity to the target DTO, including specific transformations
     * and expressions for calculated or formatted fields.
     *
     * @param rental the Rental entity to be converted. Must not be null.
     * @return the ActiveRentalDTO containing mapped and transformed data from the input Rental entity.
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "itemTitle", source = "item.title")
    @Mapping(target = "imageUrl", source = "item.imageUrl")
    @Mapping(target = "requestNumber", expression = "java(\"#REQ-\" + rental.getId())")
    @Mapping(target = "returnDeadline", source = "endDateTime")
    @Mapping(target = "remainingTimeText", expression = "java(calculateTimeDiff(rental))")
    @Mapping(target = "returnNote", ignore = true)
    ActiveRentalDTO toActiveDto(Rental rental);

    /**
     * Converts a list of Rental objects to a list of ActiveRentalDTO objects.
     *
     * @param rentals the list of Rental objects to be converted
     * @return a list of ActiveRentalDTO objects corresponding to the input Rental objects
     */
    List<ActiveRentalDTO> toActiveDtoList(List<Rental> rentals);


    /**
     * Converts a list of Rental objects into a list of RentalHistoryDto objects.
     *
     * @param rentals the list of Rental objects to be converted
     * @return a list of RentalHistoryDto objects corresponding to the provided rentals
     */
    List<RentalHistoryDto> toHistoryDtoList(List<Rental> rentals);
}
