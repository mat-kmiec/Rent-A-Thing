package pl.rentathing.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.rentathing.Rental.Dto.ActiveRentalDTO;
import pl.rentathing.Rental.Dto.ClientDashboardDTO;
import pl.rentathing.Rental.Dto.RentalHistoryDto;
import pl.rentathing.Rental.Entity.Rental;
import pl.rentathing.Rental.Entity.RentalStatus;
import pl.rentathing.Rental.Mapper.RentalMapper;
import pl.rentathing.Rental.Repository.RentalRepository;


/**
 * Service class responsible for providing operations related to the user dashboard.
 * This class manages retrieval and aggregation of data needed to populate the dashboard
 * such as details on active rentals and rental history.
 */
@Service
@RequiredArgsConstructor
public class UserDashboardService {

    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;

    /**
     * Retrieves the data needed for populating the client's dashboard, including details
     * about active rentals and rental history.
     *
     * @param userId the ID of the user for whom the dashboard data is to be retrieved
     * @return a ClientDashboardDTO object containing aggregated information about the client's
     *         active rentals and recent rental history
     */
    public ClientDashboardDTO getClientDashboardData(Long userId) {
        var activeRentals = rentalRepository.findByUserIdAndStatusOrderByEndDateTimeAsc(userId, RentalStatus.ACTIVE);
        var historyRentals = rentalRepository.findRecentHistory(userId, RentalStatus.COMPLETED, PageRequest.of(0, 5));
        return ClientDashboardDTO.builder()
                .activeRentalsCount(rentalRepository.countByUserIdAndStatus(userId, RentalStatus.ACTIVE))
                .activeRentals(rentalMapper.toActiveDtoList(activeRentals))
                .recentRentals(rentalMapper.toHistoryDtoList(historyRentals))
                .build();
    }
}
