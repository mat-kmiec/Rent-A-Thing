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

    @Query("SELECT r FROM Rental r " +
            "LEFT JOIN r.item i " +
            "LEFT JOIN r.user u " +
            "WHERE (:search IS NULL OR " +
            "   LOWER(i.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "   LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "   CAST(r.id AS string) LIKE CONCAT('%', :search, '%')) " +
            "AND (:status IS NULL OR r.status = :status) " +
            "AND (:category IS NULL OR i.category.name = :category) " +
            "AND (:returnDate IS NULL OR CAST(r.endDateTime AS date) = :returnDate)")
    Page<Rental> findAllFiltered(
            @Param("search") String search,
            @Param("status") RentalStatus status,
            @Param("category") String category,
            @Param("returnDate") java.time.LocalDate returnDate,
            Pageable pageable);


}
