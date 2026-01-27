package pl.rentathing.Rental.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.rentathing.Rental.Dto.RentalAdminListDto;
import pl.rentathing.Rental.Dto.RentalDetailsDto;
import pl.rentathing.Rental.Dto.RentalHistoryDto;
import pl.rentathing.Rental.Entity.Rental;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
}
