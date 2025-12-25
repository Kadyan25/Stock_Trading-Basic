package com.example.trading.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDto {
    private Long userId;
    private String token; // simple token = userId as string for now
    private String name;
    private String email;
}
