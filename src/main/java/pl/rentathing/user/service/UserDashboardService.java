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


@Service
@RequiredArgsConstructor
public class UserDashboardService {

    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;

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
