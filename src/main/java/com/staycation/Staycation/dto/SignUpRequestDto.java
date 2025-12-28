package com.staycation.Staycation.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class SignUpRequestDto {
    private  String email;
    private  String password;
    private String name;
}
