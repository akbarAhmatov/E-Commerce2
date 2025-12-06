package uz.pdp.ecommerce2.mapper;

import jdk.jfr.Category;
import org.mapstruct.*;
import uz.pdp.ecommerce2.dto.*;
import uz.pdp.ecommerce2.model.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", constant = "ROLE_USER")
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "cartItems", ignore = true)
    User registerRequestToUser(RegisterRequest request);
    
    @Mapping(target = "role", expression = "java(user.getRole().name())")
    AuthResponse userToAuthResponse(User user);
}

