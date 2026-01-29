package pl.rentathing.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.rentathing.auth.dto.RegisterRequest;
import pl.rentathing.user.entity.User;

/**
 * Mapper interface responsible for converting {@link RegisterRequest} DTOs into {@link User} entities.
 * This conversion ensures that incoming registration data is accurately transformed
 * for persistence and subsequent application use.
 *
 * The mapping excludes specific fields in the {@link User} entity that are either
 * irrelevant during registration or should not be directly set through the registration request.
 *
 * Key functionality includes:
 * - Ignoring fields in the {@link User} entity that are managed by the system or set at later stages,
 *   such as `id`, `password`, `role`, and others.
 * - Transforming the {@link RegisterRequest} fields like `firstName`, `lastName`, and `email`
 *   directly into their corresponding {@link User} entity fields.
 *
 * This interface uses MapStruct to automatically generate the implementation based on the declared mappings.
 *
 * Annotations:
 * - {@code @Mapper}: Declares this interface as a MapStruct Mapper for generating the implementation.
 * - {@code @Mapping}: Specifies field-level mapping customizations, with ignored fields explicitly defined.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "locked", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "notifEmail", ignore = true)
    @Mapping(target = "notifSms", ignore = true)
    @Mapping(target = "newsletter", ignore = true)
    @Mapping(target = "address", ignore = true)
    User toEntity(RegisterRequest registerRequest);
}
