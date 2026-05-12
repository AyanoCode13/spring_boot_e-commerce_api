package com.e_commerce.app.config.security;

import com.e_commerce.app.business.services.auth.JwtService;
import com.e_commerce.app.business.services.auth.TokenBlacklistService;
import com.e_commerce.app.business.services.auth.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    private final TokenBlacklistService blacklistService;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        if (blacklistService.isBlacklisted(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("""
            {"message":"Token has been invalidated","status":401}
        """);
            return;
        }



        try {
            final String email = jwtService.extractUsername(token);
            log.debug("JWT filter - extracted email: {}", email);

            if (email == null) {
                log.warn("JWT filter - could not extract email from token");
                filterChain.doFilter(request, response);
                return;
            }
            if (token != null && blacklistService.isBlacklisted(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return; // Reject the request
            }

            // Reject refresh tokens used as access tokens
            if (jwtService.isRefreshToken(token)) {
                log.warn("JWT filter - refresh token used as access token for: {}", email);
                log.debug("isRefreshToken: {}", jwtService.isRefreshToken(token));
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"Invalid token type\",\"status\":401}");
                response.getWriter().flush();
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails;
                try {
                    userDetails = userDetailsService.loadUserByUsername(email);
                    log.debug("JWT filter - loaded user: {}, authorities: {}",
                            userDetails.getUsername(), userDetails.getAuthorities());
                } catch (UsernameNotFoundException e) {
                    log.warn("JWT filter - user not found: {}", email);
                    filterChain.doFilter(request, response);
                    return;
                }

                if (jwtService.isTokenValid(token, userDetails)) {
                    log.debug("JWT filter - token valid for: {}", email);
                    log.debug("isTokenValid: {}", jwtService.isTokenValid(token, userDetails));
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    log.warn("JWT filter - token invalid for user: {}", email);
                }
            }
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.warn("JWT filter - token expired");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Token expired\",\"status\":401}");
            response.getWriter().flush();
            return;
        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.error("JWT filter - invalid signature: {}", e.getMessage());
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            log.error("JWT filter - malformed token: {}", e.getMessage());
        } catch (Exception e) {
            log.error("JWT filter - unexpected error: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        log.error("PATH = {}", path);

        return path.startsWith("api/auth");
    }

}