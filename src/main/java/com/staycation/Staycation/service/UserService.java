package com.staycation.Staycation.service;

import com.staycation.Staycation.dto.ProfileUpdateRequestDto;
import com.staycation.Staycation.dto.UserDto;
import com.staycation.Staycation.entity.User;

public interface UserService {

    User getUserById(Long id);

    User changeUserRole(Long userId, String role);

    void updateProfile(ProfileUpdateRequestDto profileUpdateRequestDto);

    UserDto getMyProfile();
}
