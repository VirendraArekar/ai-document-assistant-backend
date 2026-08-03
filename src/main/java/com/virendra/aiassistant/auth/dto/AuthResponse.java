package com.virendra.aiassistant.auth.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;

    private String type;

    private String fullName;

    private String email;

    private String role;

}