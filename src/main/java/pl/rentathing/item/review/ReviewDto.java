package pl.rentathing.item.review;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewDto {
    private Long id;
    private String authorName;
    private String content;
    private Integer rating;
    private LocalDateTime createdAt;
}
