package mx.ipn.escom.Recomendaciones.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.Authentication;

@Controller
@RequestMapping("/libros")
public class LibrosController {
    
    /**
     * Página principal de búsqueda de libros
     */
    @GetMapping
    public String libros(Model model, Authentication authentication) {
        if (authentication != null) {
            model.addAttribute("usuario", authentication.getName());
        }
        return "libros";  
    }

    /**
     * Página de favoritos de libros del usuario
     */
    @GetMapping("/favoritos")
    public String favoritosLibros(Model model, Authentication authentication) {
        if (authentication != null) {
            model.addAttribute("usuario", authentication.getName());
        }
        return "libros-favoritos";
    }

    /**
     * Página de recomendaciones de libros (funcionalidad futura)
     */
    @GetMapping("/recomendaciones")
    public String recomendacionesLibros(Model model, Authentication authentication) {
        if (authentication != null) {
            model.addAttribute("usuario", authentication.getName());
        }
        return "libros-recomendaciones";
    }
}