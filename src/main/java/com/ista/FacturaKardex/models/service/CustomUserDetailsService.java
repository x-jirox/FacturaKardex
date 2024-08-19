package com.ista.FacturaKardex.models.service;
import com.ista.FacturaKardex.models.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private IUsuarioService usuarioService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioService.findByUsername(username);
        if (usuario == null) {
            System.out.println("Usuario no encontrado: " + username);
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
        System.out.println("Usuario encontrado: " + usuario.getUsername());
        System.out.println("Roles: " + usuario.getRol().getAuthorities());
        return new org.springframework.security.core.userdetails.User(
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.getRol().getAuthorities()
        );
    }


}

