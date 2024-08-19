package com.ista.FacturaKardex.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private final String secretKey = "SecretKeyToGenJWTs";
    private final Set<String> blacklistedTokens;

    public JWTAuthenticationFilter(Set<String> blacklistedTokens) {
        this.blacklistedTokens = blacklistedTokens;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7); // Usa substring(7) para obtener el token sin "Bearer "

        // Verifica si el token está en la lista negra
        if (blacklistedTokens.contains(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey("SecretKeyToGenJWTs")
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();

            if (username != null) {
                // Aquí, puedes agregar roles o autoridades si están disponibles en los claims
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        username, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            // Maneja excepciones específicas si es necesario
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private boolean validateToken(String token) {
        try {
            // Parsea y valida el token utilizando la clave secreta
            Jwts.parser()
                    .setSigningKey("SecretKeyToGenJWTs")
                    .parseClaimsJws(token);
            return true;  // Si el token es válido, retorna true
        } catch (Exception e) {
            // Si ocurre alguna excepción durante la validación, retorna false
            return false;
        }
    }

    private Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey("SecretKeyToGenJWTs")
                .parseClaimsJws(token)
                .getBody();

        // Obtiene el nombre de usuario del token
        String username = claims.getSubject();

        // Obtiene los roles o autoridades del token
        List<GrantedAuthority> authorities = ((List<?>) claims.get("authorities")).stream()
                .map(authority -> new SimpleGrantedAuthority((String) authority))
                .collect(Collectors.toList());

        // Retorna un objeto UsernamePasswordAuthenticationToken que contiene el usuario y sus roles
        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }
}
