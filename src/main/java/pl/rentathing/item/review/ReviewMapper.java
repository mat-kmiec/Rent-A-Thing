package pl.rentathing.item.review;

import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper interface for converting between Review entity and ReviewDto.
 * It facilitates the transformation of data between the persistence layer
 * (Review entity) and the service or API layers (ReviewDto) while maintaining separation of concerns.
 *
 * This mapper uses MapStruct for automatic implementation generation.
 */
@Mapper(componentModel = "spring")
public interface ReviewMapper {
    /**
     * Maps a Review entity to a ReviewDto.
     * This method is responsible for converting a Review object,
     * typically used in persistence or database operations, into a ReviewDto
     * used for transferring data through service or API layers.
     *
     * @param review the Review entity to be converted into a DTO
     * @return the corresponding ReviewDto containing the mapped data
     */
    ReviewDto toDto(Review review);

    /**
     * Converts a given ReviewDto object into a Review entity.
     * This method maps the properties of the dto to the entity,
     * ignoring the "item" field which must be set externally.
     *
     * @param dto the ReviewDto object containing the review details to be mapped
     * @return a Review entity populated with the mapped data from the provided dto
     */
    @org.mapstruct.Mapping(target = "item", ignore = true)
    Review toEntity(ReviewDto dto);

    /**
     * Converts a list of Review entities into a list of ReviewDto objects.
     * This method is used to map multiple Review entities into their corresponding
     * DTO representations for transferring data between the persistence and service or API layers.
     *
     * @param reviews the list of Review entities to be converted
     * @return a list of ReviewDto objects mapped from the provided list of Review entities
     */
    List<ReviewDto> toDtoList(List<Review> reviews);

}
