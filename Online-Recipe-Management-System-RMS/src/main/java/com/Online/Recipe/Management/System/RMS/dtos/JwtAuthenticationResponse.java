package com.Online.Recipe.Management.System.RMS.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthenticationResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private UserDTO user;

    public JwtAuthenticationResponse(String accessToken, UserDTO user) {
        this.accessToken = accessToken;
        this.user = user;
    }
}
