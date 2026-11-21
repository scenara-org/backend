package com.scenara.backend.shared.security;

import com.scenara.backend.auth.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

       String authenticationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

       if (authenticationHeader == null || !authenticationHeader.startsWith("Bearer ")) {
           filterChain.doFilter(request, response);
           return;
       }

       String token = authenticationHeader.substring(7);

       try {
           String userId = jwtService.extractUserId(token);

           var authorization = new UsernamePasswordAuthenticationToken(
                   userId,
                   null,
                   List.of()
           );

           SecurityContextHolder.getContext().setAuthentication(authorization);
        } catch (JwtException | IllegalArgumentException exception) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
