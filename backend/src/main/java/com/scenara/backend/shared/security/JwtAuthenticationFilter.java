package com.scenara.backend.shared.security;

import com.scenara.backend.auth.service.JwtService;
import com.scenara.backend.auth.service.SessionService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
    private final SessionService sessionService;

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
           Claims claims = jwtService.parseClaims(token);
           String userId = claims.getSubject();

           // Token válido mas sessão encerrada (logout, conta excluída ou expirada no Redis): não autentica.
           if (!sessionService.estaAtiva(claims.getId(), userId)) {
               SecurityContextHolder.clearContext();
               filterChain.doFilter(request, response);
               return;
           }

           var authorization = new UsernamePasswordAuthenticationToken(
                   userId,
                   null,
                   List.of()
           );

           SecurityContextHolder.getContext().setAuthentication(authorization);
        } catch (JwtException | IllegalArgumentException exception) {
            SecurityContextHolder.clearContext();
        } catch (DataAccessException exception) {
            // Redis fora do ar: responde 503 em vez de 401, para o cliente não descartar uma sessão válida.
            SecurityContextHolder.clearContext();
            response.sendError(HttpStatus.SERVICE_UNAVAILABLE.value(), "Serviço de sessão indisponível.");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
