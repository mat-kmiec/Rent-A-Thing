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

import java.time.LocalDate;
import java.time.LocalDateTime;

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
}
