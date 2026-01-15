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

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ItemRepository itemRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    public List<ReviewDto> getReviewsForItem(Long itemId, int page) {
        return reviewRepository.findByItemIdOrderByCreatedAtDesc(
                        itemId, PageRequest.of(page, 5))
                .map(reviewMapper::toDto)
                .getContent();
    }

    @Transactional
    public ReviewDto addReview(Long itemId, ReviewDto dto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId.toString()));
        Review review = reviewMapper.toEntity(dto);
        review.setItem(item);
        Review savedReview = reviewRepository.save(review);
        updateItemStats(item);
        return reviewMapper.toDto(savedReview);

    }

    private void updateItemStats(Item item) {
        Long itemId = item.getId();
        long count = reviewRepository.countByItemId(itemId);
        Double avg = reviewRepository.getAverageRatingByItemId(itemId);
        item.setReviewCount((int) count);;
        item.setAverageRating(avg != null ? BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP).doubleValue() : 0.0);
        itemRepository.save(item);
    }
}
