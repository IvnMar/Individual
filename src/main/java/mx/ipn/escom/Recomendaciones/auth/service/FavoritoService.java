package mx.ipn.escom.Recomendaciones.auth.service;

import mx.ipn.escom.Recomendaciones.auth.entity.Favorito;
import mx.ipn.escom.Recomendaciones.auth.entity.Usuario;
import mx.ipn.escom.Recomendaciones.auth.repository.FavoritoRepository;
import mx.ipn.escom.Recomendaciones.auth.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FavoritoService {

    @Autowired
    private FavoritoRepository favoritoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Método helper para obtener usuario por email o nombre
     */
    private Usuario obtenerUsuario(String identificador) {
        System.out.println("DEBUG - Buscando usuario con identificador: " + identificador);
        
        // Primero intenta por email
        Usuario usuario = usuarioRepository.findByEmail(identificador);
        if (usuario != null) {
            System.out.println("DEBUG - Usuario encontrado por email: " + usuario.getEmail());
            return usuario;
        }
        
        // Si no encuentra por email, intenta por nombre
        usuario = usuarioRepository.findByNombre(identificador);
        if (usuario != null) {
            System.out.println("DEBUG - Usuario encontrado por nombre: " + usuario.getNombre());
            return usuario;
        }
        
        System.out.println("DEBUG - Usuario NO encontrado con identificador: " + identificador);
        return null;
    }

    /**
     * Agregar un contenido a favoritos
     */
    public Favorito agregarFavorito(String emailUsuario, Favorito.TipoContenido tipoContenido, 
                                  String contenidoId, String titulo, String autorDirector, 
                                  String imagenUrl, String descripcion, String anioPublicacion, 
                                  Double calificacion) {
        
        Usuario usuario = obtenerUsuario(emailUsuario);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        // Verificar si ya existe en favoritos
        if (favoritoRepository.existsByUsuarioAndTipoContenidoAndContenidoId(usuario, tipoContenido, contenidoId)) {
            throw new RuntimeException("El contenido ya está en favoritos");
        }

        Favorito favorito = new Favorito(usuario, tipoContenido, contenidoId, titulo);
        favorito.setAutorDirector(autorDirector);
        favorito.setImagenUrl(imagenUrl);
        favorito.setDescripcion(descripcion);
        favorito.setAnioPublicacion(anioPublicacion);
        favorito.setCalificacion(calificacion);

        return favoritoRepository.save(favorito);
    }

    /**
     * Eliminar un contenido de favoritos
     */
    public boolean eliminarFavorito(String emailUsuario, Favorito.TipoContenido tipoContenido, String contenidoId) {
        Usuario usuario = obtenerUsuario(emailUsuario);
        if (usuario == null) {
            return false;
        }

        Optional<Favorito> favorito = favoritoRepository.findByUsuarioAndTipoContenidoAndContenidoId(
                usuario, tipoContenido, contenidoId);
        
        if (favorito.isPresent()) {
            favoritoRepository.delete(favorito.get());
            return true;
        }
        return false;
    }

    /**
     * Verificar si un contenido está en favoritos
     */
    public boolean esFavorito(String emailUsuario, Favorito.TipoContenido tipoContenido, String contenidoId) {
        Usuario usuario = obtenerUsuario(emailUsuario);
        if (usuario == null) {
            return false;
        }

        return favoritoRepository.existsByUsuarioAndTipoContenidoAndContenidoId(usuario, tipoContenido, contenidoId);
    }

    /**
     * Obtener todos los favoritos de un usuario por tipo
     */
    @Transactional(readOnly = true)
    public List<Favorito> obtenerFavoritosPorTipo(String emailUsuario, Favorito.TipoContenido tipoContenido) {
        Usuario usuario = obtenerUsuario(emailUsuario);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        return favoritoRepository.findByUsuarioAndTipoContenidoOrderByFechaAgregadoDesc(usuario, tipoContenido);
    }

    /**
     * Obtener todos los favoritos de un usuario
     */
    @Transactional(readOnly = true)
    public List<Favorito> obtenerTodosFavoritos(String emailUsuario) {
        Usuario usuario = obtenerUsuario(emailUsuario);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        return favoritoRepository.findByUsuarioOrderByFechaAgregadoDesc(usuario);
    }

    /**
     * Buscar favoritos por título
     */
    @Transactional(readOnly = true)
    public List<Favorito> buscarFavoritosPorTitulo(String emailUsuario, String titulo) {
        Usuario usuario = obtenerUsuario(emailUsuario);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        return favoritoRepository.findByUsuarioAndTituloContainingIgnoreCase(usuario, titulo);
    }

    /**
     * Obtener estadísticas de favoritos
     */
    @Transactional(readOnly = true)
    public FavoritoStats obtenerEstadisticas(String emailUsuario) {
        Usuario usuario = obtenerUsuario(emailUsuario);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        Long totalLibros = favoritoRepository.countByUsuarioAndTipoContenido(usuario, Favorito.TipoContenido.LIBRO);
        Long totalPeliculas = favoritoRepository.countByUsuarioAndTipoContenido(usuario, Favorito.TipoContenido.PELICULA);

        return new FavoritoStats(totalLibros, totalPeliculas);
    }

    /**
     * Obtener favoritos recientes (últimos N)
     */
    @Transactional(readOnly = true)
    public List<Favorito> obtenerFavoritosRecientes(String emailUsuario, int limite) {
        Usuario usuario = obtenerUsuario(emailUsuario);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        return favoritoRepository.findTopNByUsuarioOrderByFechaAgregadoDesc(
                usuario, PageRequest.of(0, limite));
    }

    /**
     * Clase para estadísticas de favoritos
     */
    public static class FavoritoStats {
        private Long totalLibros;
        private Long totalPeliculas;

        public FavoritoStats(Long totalLibros, Long totalPeliculas) {
            this.totalLibros = totalLibros;
            this.totalPeliculas = totalPeliculas;
        }

        public Long getTotalLibros() { return totalLibros; }
        public Long getTotalPeliculas() { return totalPeliculas; }
        public Long getTotal() { return totalLibros + totalPeliculas; }
    }
}