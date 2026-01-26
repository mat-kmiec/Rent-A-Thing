package pl.rentathing.Rental.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.rentathing.Rental.Entity.Rental;
import pl.rentathing.Rental.Entity.RentalStatus;
import pl.rentathing.user.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    @Query("""
                SELECT COUNT(r) > 0 FROM Rental r
                WHERE r.item.id = :itemId
                AND (
                    (r.status IN ('PENDING', 'ACTIVE', 'OVERDUE') AND r.startDateTime <= :end AND r.endDateTime >= :start)
                )
            """)
    boolean existsConflictWithBuffer(@Param("itemId") Long itemId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT r FROM Rental r WHERE r.user = :user " +
            "AND (:search IS NULL OR LOWER(r.item.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(CAST(r.id as String)) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:status IS NULL OR r.status = :status)")
    Page<Rental> findFilteredRentals(
            @Param("user") User user,
            @Param("search") String search,
            @Param("status") RentalStatus status,
            Pageable pageable);

    Optional<Rental> findByIdAndUser(Long id, User user);

    List<Rental> findTop5ByOrderByStartDateTimeDesc();

    @Query("SELECT r.item, COUNT(r) as rentalCount FROM Rental r GROUP BY r.item ORDER BY rentalCount DESC")
    List<Object[]> findTopRentedItems(Pageable pageable);

    @Query("SELECT SUM(r.totalCost) FROM Rental r WHERE r.startDateTime >= :start AND r.startDateTime <= :end")
    BigDecimal calculateRevenueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT r.user, COUNT(r) as rentalCount FROM Rental r GROUP BY r.user ORDER BY rentalCount DESC")
    List<Object[]> findTopUsers(Pageable pageable);

    List<Rental> findByStatus(RentalStatus status);

    List<Rental> findByStatusIn(List<RentalStatus> statuses);

    long countByStatus(RentalStatus status);

    long countByEndDateTimeBetween(LocalDateTime start, LocalDateTime end);
}
