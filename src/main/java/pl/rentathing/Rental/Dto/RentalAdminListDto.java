package pl.rentathing.Rental.Dto;

import lombok.Builder;
import lombok.Getter;
import pl.rentathing.Rental.Entity.RentalStatus;

import java.time.LocalDateTime;

@Getter
@Builder
public class RentalAdminListDto {
    private Long id;
    private String itemTitle;
    private String categoryIcon;
    private String userFullName;
    private String userInitials;
    private LocalDateTime endDateTime;
    private RentalStatus status;
    private String timeDiffMessage;
    private boolean isOverdue;
}