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
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Rental entities. Provides methods
 * for querying and performing CRUD operations on Rental data.
 */
@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    /**
     * Checks if there is a conflict within the buffer period for a specific item rental.
     * A conflict occurs if there is an existing rental with an overlapping date range
     * where the rental has a status of NEW, PENDING, ACTIVE, or OVERDUE.
     *
     * @param itemId the ID of the item to check for rental conflicts
     * @param start the start date and time of the buffer period to check
     * @param end the end date and time of the buffer period to check
     * @return true if a rental conflict exists for the given item and date range, false otherwise
     */
    @Query("""
    SELECT COUNT(r) > 0 FROM Rental r 
    WHERE r.item.id = :itemId 
    AND (
        (r.status IN ('NEW', 'PENDING', 'ACTIVE', 'OVERDUE') AND r.startDateTime <= :end AND r.endDateTime >= :start)
    )
""")
    boolean existsConflictWithBuffer(@Param("itemId") Long itemId,
                                     @Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end);

    /**
     * Retrieves a paginated list of rentals filtered by user, search query, and status.
     *
     * @param user the user associated with the rentals
     * @param search the search query to filter rentals by item title or rental ID, case-insensitive; can be null
     * @param status the rental status to filter by; can be null
     * @param pageable the pagination information
     * @return a page of rentals that match the specified filters
     */
    @Query("SELECT r FROM Rental r WHERE r.user = :user " +
            "AND (:search IS NULL OR LOWER(r.item.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(CAST(r.id as String)) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:status IS NULL OR r.status = :status)")
    Page<Rental> findFilteredRentals(
            @Param("user") User user,
            @Param("search") String search,
            @Param("status") RentalStatus status,
            Pageable pageable);

    /**
     * Finds a rental by its ID and associated user.
     *
     * @param id the ID of the rental to find
     * @param user the user associated with the rental
     * @return an Optional containing the found Rental if it exists, or an empty Optional if no such Rental is found
     */
    Optional<Rental> findByIdAndUser(Long id, User user);

    /**
     * Retrieves a paginated list of rentals that match the specified filters.
     * The filters can include a search query, rental status, item category, and return date.
     * Any filter parameter can be null, indicating that it should be ignored in the filtering.
     *
     * @param search the search query to filter rentals by item title, user last name,
     *               or rental ID. Filters are case-insensitive. Can be null.
     * @param status the rental status to filter by. Can be null.
     * @param category the category name of the item to filter by. Can be null.
     * @param returnDate the specific return date to filter by in `yyyy-MM-dd` format. Can be null.
     * @param pageable the pagination information, containing page number and size.
     * @return a Page containing rentals that match the given filters.
     */
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

    /**
     * Retrieves a rental by its ID along with all associated details, including the item,
     * item's category, user, and user's address (if available).
     *
     * @param id the unique identifier of the rental to be retrieved
     * @return an Optional containing the rental with all associated details if it exists;
     *         otherwise, an empty Optional
     */
    @Query("SELECT r FROM Rental r " +
            "JOIN FETCH r.item i " +
            "JOIN FETCH i.category " +
            "JOIN FETCH r.user u " +
            "LEFT JOIN FETCH u.address " +
            "WHERE r.id = :id")
    Optional<Rental> findByIdWithDetails(@Param("id") Long id);

    /**
     * Retrieves a list of Rental entities that have start date and time
     * within the specified range. The query joins the Rental entity with
     * the associated Item and User entities and retrieves all relevant
     * details. The results are ordered in descending order by the start date and time.
     *
     * @param start the starting point of the date and time range for filtering rentals
     * @param end the ending point of the date and time range for filtering rentals
     * @return a list of Rental entities that match the specified date and time range with detailed information
     */
    @Query("SELECT r FROM Rental r " +
            "JOIN FETCH r.item i " +
            "JOIN FETCH r.user u " +
            "WHERE r.startDateTime >= :start AND r.startDateTime <= :end " +
            "ORDER BY r.startDateTime DESC")
    List<Rental> findAllByStartDateTimeBetweenWithDetails(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );


}
