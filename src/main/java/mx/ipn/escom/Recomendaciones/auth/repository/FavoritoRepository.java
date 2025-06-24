package mx.ipn.escom.Recomendaciones.auth.repository;

import mx.ipn.escom.Recomendaciones.auth.entity.Favorito;
import mx.ipn.escom.Recomendaciones.auth.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
    
    /**
     * Buscar favoritos por usuario y tipo de contenido
     */
    List<Favorito> findByUsuarioAndTipoContenidoOrderByFechaAgregadoDesc(
            Usuario usuario, 
            Favorito.TipoContenido tipoContenido
    );
    
    /**
     * Buscar todos los favoritos de un usuario ordenados por fecha
     */
    List<Favorito> findByUsuarioOrderByFechaAgregadoDesc(Usuario usuario);
    
    /**
     * Verificar si existe un favorito específico
     */
    boolean existsByUsuarioAndTipoContenidoAndContenidoId(
            Usuario usuario, 
            Favorito.TipoContenido tipoContenido, 
            String contenidoId
    );
    
    /**
     * Buscar un favorito específico
     */
    Optional<Favorito> findByUsuarioAndTipoContenidoAndContenidoId(
            Usuario usuario, 
            Favorito.TipoContenido tipoContenido, 
            String contenidoId
    );
    
    /**
     * Contar favoritos por usuario y tipo
     */
    Long countByUsuarioAndTipoContenido(
            Usuario usuario, 
            Favorito.TipoContenido tipoContenido
    );
    
    /**
     * Buscar favoritos por título (búsqueda parcial)
     */
    @Query("SELECT f FROM Favorito f WHERE f.usuario = :usuario AND " +
           "LOWER(f.titulo) LIKE LOWER(CONCAT('%', :titulo, '%')) " +
           "ORDER BY f.fechaAgregado DESC")
    List<Favorito> findByUsuarioAndTituloContainingIgnoreCase(
            @Param("usuario") Usuario usuario, 
            @Param("titulo") String titulo
    );
    
    /**
     * Buscar los favoritos más recientes de un usuario (últimos N)
     */
    @Query("SELECT f FROM Favorito f WHERE f.usuario = :usuario " +
           "ORDER BY f.fechaAgregado DESC")
    List<Favorito> findTopNByUsuarioOrderByFechaAgregadoDesc(
            @Param("usuario") Usuario usuario, 
            org.springframework.data.domain.Pageable pageable
    );
    
    /**
     * Eliminar un favorito específico
     */
    void deleteByUsuarioAndTipoContenidoAndContenidoId(
            Usuario usuario, 
            Favorito.TipoContenido tipoContenido, 
            String contenidoId
    );
}