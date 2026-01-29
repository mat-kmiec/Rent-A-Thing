package pl.rentathing.item.review;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * ReviewRepository is a repository interface for managing Review entities
 * in the data persistence layer. It extends JpaRepository to provide standard
 * CRUD operations for the Review entity.
 *
 * This interface includes additional query methods for retrieving and operating
 * on reviews based on specific criteria, such as finding reviews by item ID
 * and calculating review statistics like average rating and count.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Slice<Review> findByItemIdOrderByCreatedAtDesc(Long itemId, Pageable pageable);

    long countByItemId(Long itemId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.item.id = :itemId")
    Double getAverageRatingByItemId(Long itemId);
}
