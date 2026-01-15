package pl.rentathing.item.review;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/{itemId}")
    public ResponseEntity<List<ReviewDto>> getReviews(
            @PathVariable Long itemId,
            @RequestParam(defaultValue = "0") int page) {

        List<ReviewDto> reviews = reviewService.getReviewsForItem(itemId, page);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/{itemId}")
    public ResponseEntity<ReviewDto> addReview(
            @PathVariable Long itemId,
            @RequestBody ReviewDto dto) {

        ReviewDto savedReview = reviewService.addReview(itemId, dto);
        return ResponseEntity.ok(savedReview);
    }
}