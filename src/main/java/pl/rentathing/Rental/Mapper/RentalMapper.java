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
 * RentalMapper is a MapStruct mapper interface designed to map between domain objects and
 * Data Transfer Objects (DTOs) in the rental management system. This mapper facilitates
 * seamless conversion of entity data into formats suitable for various use cases, such as
 * presenting rental details to users, exporting rental data, or listing rentals in administrative panels.
 *
 * The mapper defines mappings between the Rental domain object and different DTO representations,
 * tailoring the structure and content of each DTO to its intended purpose. It also supports
 * default methods for complex transformations.
 *
 * Key Features:
 * - Converts Rental objects to multiple DTOs, such as RentalHistoryDto, RentalDetailsDto,
 *   RentalAdminListDto, RentalAdminDetailsDto, and RentalExportDto, using explicitly defined
 *   mappings.
 * - Handles conditional and computed fields using expressions within mappings to calculate
 *   values, such as time difference messages, user initials, or overdue status.
 * - Provides default methods for mapping nested objects and performing custom logic, such
 *   as address formatting or user and item search DTO generation.
 * - Utilizes MapStruct's mapping capabilities to enhance the readability and maintainability
 *   of transformation logic.
 *
 * Mapping Methods:
 * - toHistoryDto: Maps a Rental entity to RentalHistoryDto for representing historical rental information.
 * - toDetailsDto: Maps a Rental entity to RentalDetailsDto for detailed rental view purposes.
 * - toAdminListDto: Maps a Rental entity to RentalAdminListDto, used in administrative overviews of rentals.
 * - toAdminDetailsDto: Maps a Rental entity to RentalAdminDetailsDto, providing enhanced administrative insights.
 * - toExportDto: Maps a Rental entity to RentalExportDto, used for exporting rental data.
 * - toExportDtoList: Converts a list of Rental entities to a list of RentalExportDto.
 * - toUserSearchDto: Maps a User entity to UserSearchDto for user lookup purposes.
 * - toItemSearchDto: Maps an Item entity to ItemSearchDto for item lookup purposes.
 *
 * Default Methods:
 * - calculateTimeDiff: Computes a user-friendly message for the time difference between the
 *   current time and the rental's end date.
 * - mapAddress: Converts an Address entity into a formatted string for display, handling cases
 *   where parts of the address may be absent.
 */
@Mapper(componentModel = "spring", imports = {LocalDateTime.class, ChronoUnit.class})
public interface RentalMapper {

    @Mapping(target = "itemTitle", source = "item.title")
    @Mapping(target = "itemImageUrl", source = "item.imageUrl")
    RentalHistoryDto toHistoryDto(Rental rental);

    @Mapping(target = "itemTitle", source = "item.title")
    @Mapping(target = "itemImageUrl", source = "item.imageUrl")
    @Mapping(target = "itemId", source = "item.id")
    RentalDetailsDto toDetailsDto(Rental rental);


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

    @Named("mapAddress")
    default String mapAddress(Address address) {
        if (address == null) return "Brak adresu";
        String apt = (address.getApartmentNumber() != null && !address.getApartmentNumber().isEmpty())
                ? "/" + address.getApartmentNumber() : "";
        return String.format("%s %s%s, %s %s",
                address.getStreet(), address.getHouseNumber(), apt, address.getZipCode(), address.getCity());
    }

    @Mapping(target = "itemTitle", source = "item.title")
    @Mapping(target = "userFullName", expression = "java(rental.getUser().getFirstName() + \" \" + rental.getUser().getLastName())")
    @Mapping(target = "startDateTime", source = "startDateTime", dateFormat = "yyyy-MM-dd HH:mm")
    @Mapping(target = "endDateTime", source = "endDateTime", dateFormat = "yyyy-MM-dd HH:mm")
    @Mapping(target = "status", source = "status.displayName")
    @Mapping(target = "totalCost", expression = "java(rental.getTotalCost() != null ? rental.getTotalCost().toString() + \" zł\" : \"0 zł\")")
    RentalExportDto toExportDto(Rental rental);

    List<RentalExportDto> toExportDtoList(List<Rental> rentals);

    default UserSearchDto toUserSearchDto(User user) {
        return new UserSearchDto(
                user.getId(),
                user.getFirstName() + " " + user.getLastName() + " (" + user.getEmail() + ")",
                user.getEmail()
        );
    }

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

    @Mapping(target = "id", source = "id")
    @Mapping(target = "itemTitle", source = "item.title")
    @Mapping(target = "imageUrl", source = "item.imageUrl")
    @Mapping(target = "requestNumber", expression = "java(\"#REQ-\" + rental.getId())")
    @Mapping(target = "returnDeadline", source = "endDateTime")
    @Mapping(target = "remainingTimeText", expression = "java(calculateTimeDiff(rental))")
    @Mapping(target = "returnNote", ignore = true)
    ActiveRentalDTO toActiveDto(Rental rental);

    List<ActiveRentalDTO> toActiveDtoList(List<Rental> rentals);


    List<RentalHistoryDto> toHistoryDtoList(List<Rental> rentals);
}
