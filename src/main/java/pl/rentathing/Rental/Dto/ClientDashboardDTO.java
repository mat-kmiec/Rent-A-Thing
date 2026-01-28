package pl.rentathing.Rental.Dto;

import lombok.Builder;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing the client dashboard information.
 *
 * This class encapsulates data displayed on the client dashboard, providing an overview
 * of the user's current rental activities and rental history. It includes a summary
 * of active rentals, detailed information about each active rental, and a list of
 * recent past rentals.
 *
 * Fields:
 * - activeRentalsCount: The total number of currently active rentals.
 * - activeRentals: A list of active rental details, described using ActiveRentalDTO.
 * - recentRentals: A list of recent rental history records, each described using RentalHistoryDto.
 */
@Builder
public record ClientDashboardDTO(
        long activeRentalsCount,
        List<ActiveRentalDTO> activeRentals,
        List<RentalHistoryDto> recentRentals
) {}