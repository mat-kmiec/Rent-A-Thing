package pl.rentathing.Rental.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.rentathing.Rental.Entity.Rental;

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
}
