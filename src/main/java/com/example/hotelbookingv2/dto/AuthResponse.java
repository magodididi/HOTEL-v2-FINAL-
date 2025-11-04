package com.example.hotelbookingv2.dto;

public record AuthResponse(String token, String username, String[] roles) {}
