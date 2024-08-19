package com.ista.FacturaKardex;
import com.ista.FacturaKardex.models.entity.Rol;
import com.ista.FacturaKardex.models.entity.Usuario;
import com.ista.FacturaKardex.models.service.IUsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner dataLoader(IUsuarioService usuarioService, BCryptPasswordEncoder encoder) {
        return args -> {
            if (usuarioService.findByUsername("admin") == null) {
                Usuario admin = new Usuario();
                admin.setUsername("admin");
                String encodedPassword = encoder.encode("123");
                System.out.println("Encoded Password: " + encodedPassword); // Imprimir la contraseña encriptada
                admin.setPassword(encodedPassword);
                admin.setRol(Rol.ADMIN);
                usuarioService.save(admin);
            }
        };
    }
}

