package pl.rentathing.item.review;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ReviewController is a REST controller responsible for handling review-related operations.
 * This includes retrieving reviews for specific items and adding new reviews.
 * It maps to the base URL "/api/reviews".
 */
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Retrieves a list of reviews for a given item, sorted by creation date in descending order.
     *
     * @param itemId the ID of the item for which reviews are to be fetched
     * @param page the page number to fetch, defaults to 0 if not provided
     * @return a ResponseEntity containing the list of ReviewDto objects for the specified item
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<List<ReviewDto>> getReviews(
            @PathVariable Long itemId,
            @RequestParam(defaultValue = "0") int page) {

        List<ReviewDto> reviews = reviewService.getReviewsForItem(itemId, page);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Adds a new review for a specified item.
     *
     * @param itemId the ID of the item to which the review is associated
     * @param dto the ReviewDto object containing the details of the review to be created
     * @return a ResponseEntity containing the created ReviewDto object
     */
    @PostMapping("/{itemId}")
    public ResponseEntity<ReviewDto> addReview(
            @PathVariable Long itemId,
            @RequestBody ReviewDto dto) {

        ReviewDto savedReview = reviewService.addReview(itemId, dto);
        return ResponseEntity.ok(savedReview);
    }
}