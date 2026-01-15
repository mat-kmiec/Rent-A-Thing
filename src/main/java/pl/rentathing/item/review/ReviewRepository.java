package pl.rentathing.item.review;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Slice<Review> findByItemIdOrderByCreatedAtDesc(Long itemId, Pageable pageable);

    long countByItemId(Long itemId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.item.id = :itemId")
    Double getAverageRatingByItemId(Long itemId);
}
