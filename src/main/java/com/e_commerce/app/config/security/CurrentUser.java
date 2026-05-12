package com.e_commerce.app.config.security;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {

    public String getEmail(JwtAuthenticationToken token) {
        return token.getToken().getClaimAsString("email");
    }

    public String getName(JwtAuthenticationToken token) {
        return token.getToken().getClaimAsString("name");
    }

    public String getPicture(JwtAuthenticationToken token) {
        return token.getToken().getClaimAsString("picture");
    }
}
