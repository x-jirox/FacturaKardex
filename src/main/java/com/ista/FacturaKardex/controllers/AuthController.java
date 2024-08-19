package com.ista.FacturaKardex.controllers;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final Set<String> blacklistedTokens = new HashSet<>();

    public AuthController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/user/login")
    public ResponseEntity<Map<String, String>> authenticateUser(@RequestParam("username") String username,
                                                                @RequestParam("password") String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            User user = (User) authentication.getPrincipal();

            String jwt = Jwts.builder()
                    .setSubject(user.getUsername())
                    .claim("authorities", user.getAuthorities())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // Token válido por 24 horas
                    .signWith(SignatureAlgorithm.HS256, "SecretKeyToGenJWTs")
                    .compact();

            Map<String, String> response = new HashMap<>();
            response.put("token", jwt);

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", "Invalid credentials"));
        }
    }
    @PostMapping("/user/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestParam("token") String token) {
        if (token != null) {
            blacklistedTokens.add(token); // Agrega el token a la lista negra
            return ResponseEntity.ok(Collections.singletonMap("message", "Logged out successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", "Invalid token"));
        }
    }


    @GetMapping("/some-secure-page")
    public String someSecurePage(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            // El usuario no está autenticado, redirigir al login
            return "redirect:/login";
        }
        // El usuario está autenticado, proceder normalmente
        return "securePage";
    }


}