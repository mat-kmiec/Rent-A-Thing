package pl.rentathing.user.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.rentathing.auth.dto.RegisterRequest;
import pl.rentathing.user.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "locked", ignore = true)
    User toEntity(RegisterRequest registerRequest);
}
