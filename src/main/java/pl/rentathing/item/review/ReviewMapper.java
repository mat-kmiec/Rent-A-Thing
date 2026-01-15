package pl.rentathing.item.review;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    ReviewDto toDto(Review review);
    Review toEntity(ReviewDto dto);
    List<ReviewDto> toDtoList(List<Review> reviews);

}
