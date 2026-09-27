package com.ma.user.mapper;


import com.ma.user.dto.CreateUserRequest;
import com.ma.user.dto.UserResponse;
import com.ma.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(CreateUserRequest request) {
        User user = new User();
        user.setUsername(request.username());
        return user;
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername());
    }
}