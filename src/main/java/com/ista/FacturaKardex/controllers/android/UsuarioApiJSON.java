package com.ista.FacturaKardex.controllers.android;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/users")
public class UsuarioApiJSON {

    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();
    private static final String SECRET_KEY = "SecretKeyToGenJWTs";
    private static final long EXPIRATION_TIME = 86400000L; // 24 horas


    @Autowired
    private AuthenticationManager authenticationManager; // Inyectar AuthenticationManager



    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Autenticación del usuario
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // Generación del JWT
            String token = Jwts.builder()
                    .setSubject(loginRequest.getUsername())
                    .claim("authorities", authentication.getAuthorities().stream()
                            .map(Object::toString).collect(Collectors.toList()))
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                    .compact();

            // Construcción de la respuesta
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("username", loginRequest.getUsername());
            response.put("role", authentication.getAuthorities().stream()
                    .map(Object::toString).collect(Collectors.toList()).get(0)); // Asegúrate de que el rol esté incluido

            return ResponseEntity.ok(response);

        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Invalid username or password"));
        }
    }

}
