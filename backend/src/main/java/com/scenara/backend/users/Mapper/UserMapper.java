package com.scenara.backend.users.Mapper;

import com.scenara.backend.users.dto.CreateUserResponse;
import com.scenara.backend.users.dto.UpdateUserRequest;
import com.scenara.backend.users.model.UserModel;
import org.mapstruct.*;

@Mapper(componentModel= "spring")
public interface UserMapper {


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update (UpdateUserRequest request, @MappingTarget UserModel user);

    CreateUserResponse toResponse(UserModel userModel);
}


