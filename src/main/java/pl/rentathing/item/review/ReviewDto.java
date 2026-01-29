package pl.rentathing.item.review;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * ReviewDto is a Data Transfer Object (DTO) representing a review of an item.
 * It carries review data between different layers of the application without
 * exposing underlying entity details.
 *
 * This class provides information about the review's author, content, rating,
 * and the time it was created. It is primarily used in mapping reviews from
 * the entity layer to the API response layer and vice versa.
 */
@Data
@Builder
public class ReviewDto {
    private Long id;
    private String authorName;
    private String content;
    private Integer rating;
    private LocalDateTime createdAt;
}
