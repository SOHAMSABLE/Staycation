package com.staycation.Staycation.dto;

import com.staycation.Staycation.entity.User;
import com.staycation.Staycation.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.Data;

@Data

public class GuestDto {

    private Long id;

    private User user;

    private String name;

    private Gender gender;

    private Integer age;
}
