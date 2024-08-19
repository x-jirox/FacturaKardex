package com.ista.FacturaKardex.controllers;

import com.ista.FacturaKardex.models.entity.Rol;
import com.ista.FacturaKardex.models.entity.Usuario;
import com.ista.FacturaKardex.models.service.IProductsService;
import com.ista.FacturaKardex.models.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UsuarioController {

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private IProductsService productsService;


    @GetMapping("/index")
    public String index(@RequestParam(defaultValue = "0") int page, Model model, Principal principal) {
        UserDetails userDetails = (UserDetails) ((Authentication) principal).getPrincipal();
        model.addAttribute("userPrincipal", userDetails);
        List<String> categorias = List.of("Botas", "Zandalias", "Casuales", "Deportivos", "Escolares");
        model.addAttribute("categorias", categorias);
        return "index";
    }

    @GetMapping("/registro")
    public String formRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "user/registro";
    }

    @PostMapping("/registro")
    public String registrarCliente(@ModelAttribute("usuario") Usuario usuario, Model model) {
        try {
            String encodedPassword = passwordEncoder.encode(usuario.getPassword());
            usuario.setPassword(encodedPassword);
            usuario.setRol(Rol.CLIENT);
            usuarioService.save(usuario);
            return "redirect:/user/login";
        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar el usuario: " + e.getMessage());
            return "user/registro";
        }
    }

    @GetMapping("/registroEmpleados")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "user/registroEmpleados";
    }

    @RequestMapping(value = "/registroEmpleados", method = RequestMethod.POST)
    public String registrarEmployee(@ModelAttribute("usuario") Usuario usuario, Model model) {
        try {
            String encodedPassword = passwordEncoder.encode(usuario.getPassword());
            usuario.setPassword(encodedPassword);
            usuario.setRol(Rol.PERSON);
            usuarioService.save(usuario);
            return "redirect:/user/registroEmpleados";
        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar el empleado: " + e.getMessage());
            return "user/registroEmpleados"; // Asegúrate de que esta vista existe
        }
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "user/login";
    }

    @GetMapping("/accessDenied")
    public String accessDenied(Model model) {
        model.addAttribute("error", "Access Denied");
        return "user/accessDenied";
    }
    @RequestMapping(value = "listarUsuario", method = RequestMethod.GET)
    public String listarUsuario(@RequestParam(name = "nombres", required = false, defaultValue = "") String nombres,
                                Model model) {
        // Recupera todos los usuarios
        List<Usuario> allUsuarios = usuarioService.findAll();

        // Filtra los usuarios por el rol "PERSON"
        List<Usuario> filteredUsuarios = allUsuarios.stream()
                .filter(usuario -> Rol.PERSON.equals(usuario.getRol()))
                .collect(Collectors.toList());

        // Filtra por nombre si se proporciona uno
        if (!nombres.isEmpty()) {
            filteredUsuarios = filteredUsuarios.stream()
                    .filter(usuario -> usuario.getNombres().toLowerCase().contains(nombres.toLowerCase()))
                    .collect(Collectors.toList());
        }

        model.addAttribute("usuarios", filteredUsuarios);
        model.addAttribute("nombres", nombres); // Para mantener el texto de búsqueda en el formulario

        return "user/listarUsuario";
    }

    //buscar los producto en el inventario
    @GetMapping("/buscarUsuario")
    public String buscarUsario(@RequestParam("nombre") String nombre, Model model) {
        List<Usuario> usuarios = usuarioService.findByNombreContainingIgnoreCase(nombre);
        model.addAttribute("usuarios", usuarios);
        return "resultadoBusquedaUs";
    }

}
