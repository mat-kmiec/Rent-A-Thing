package pl.rentathing.item.review;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rentathing.item.entity.Item;
import pl.rentathing.item.exception.ItemNotFoundException;
import pl.rentathing.item.repository.ItemRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Service class for managing reviews of items.
 * This class provides methods to retrieve, add, and manage reviews for specific items.
 * It integrates various layers including repositories, mappers, and domain logic to perform operations.
 *
 * The service ensures proper interaction with the persistence layer, validates item existence,
 * updates item statistics after reviews are added, and handles pagination for fetching reviews.
 */
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ItemRepository itemRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    /**
     * Retrieves a paginated list of reviews for a specific item, ordered by their creation timestamp in descending order.
     *
     * @param itemId the unique identifier of the item for which reviews are being retrieved
     * @param page the page number for pagination
     * @return a list of {@link ReviewDto} objects representing the reviews of the specified item
     */
    public List<ReviewDto> getReviewsForItem(Long itemId, int page) {
        return reviewRepository.findByItemIdOrderByCreatedAtDesc(
                        itemId, PageRequest.of(page, 5))
                .map(reviewMapper::toDto)
                .getContent();
    }

    /**
     * Adds a review to the specified item and updates the item's statistics.
     *
     * @param itemId the ID of the item to which the review will be added
     * @param dto the ReviewDto containing the details of the review to be added
     * @return a ReviewDto representing the saved review
     * @throws ItemNotFoundException if the item with the given ID is not found
     */
    @Transactional
    public ReviewDto addReview(Long itemId, ReviewDto dto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId.toString()));
        Review review = reviewMapper.toEntity(dto);
        review.setItem(item);
        Review savedReview = reviewRepository.save(review);
        updateItemStats(item);
        return reviewMapper.toDto(savedReview);

    }

    /**
     * Updates the review statistics of the specified item. This method calculates
     * the total count of reviews and the average rating for the item, then saves
     * the updated statistics to the repository.
     *
     * @param item the item for which the statistics are being updated; must not be null
     */
    private void updateItemStats(Item item) {
        Long itemId = item.getId();
        long count = reviewRepository.countByItemId(itemId);
        Double avg = reviewRepository.getAverageRatingByItemId(itemId);
        item.setReviewCount((int) count);;
        item.setAverageRating(avg != null ? BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP).doubleValue() : 0.0);
        itemRepository.save(item);
    }
}
