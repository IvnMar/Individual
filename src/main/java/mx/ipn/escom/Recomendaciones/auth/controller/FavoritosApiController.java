package mx.ipn.escom.Recomendaciones.auth.controller;

import mx.ipn.escom.Recomendaciones.auth.entity.Favorito;
import mx.ipn.escom.Recomendaciones.auth.service.FavoritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favoritos")
public class FavoritosApiController {

    @Autowired
    private FavoritoService favoritoService;

    /**
     * Agregar un contenido a favoritos
     */
    @PostMapping("/agregar")
    public ResponseEntity<Map<String, Object>> agregarFavorito(
            @RequestBody FavoritoRequest request,
            Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Debug: verificar qué devuelve authentication.getName()
            String authName = authentication.getName();
            System.out.println("DEBUG - Authentication name: " + authName);
            
            // Validar datos requeridos
            if (request.getTipo() == null || request.getContenidoId() == null || 
                request.getTitulo() == null || request.getTitulo().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Datos incompletos");
                return ResponseEntity.badRequest().body(response);
            }

            Favorito.TipoContenido tipoContenido;
            try {
                tipoContenido = Favorito.TipoContenido.valueOf(request.getTipo().toUpperCase());
            } catch (IllegalArgumentException e) {
                response.put("success", false);
                response.put("message", "Tipo de contenido inválido");
                return ResponseEntity.badRequest().body(response);
            }

            Favorito favorito = favoritoService.agregarFavorito(
                    authName, // Usar authName en lugar de authentication.getName()
                    tipoContenido,
                    request.getContenidoId(),
                    request.getTitulo(),
                    request.getAutorDirector(),
                    request.getImagenUrl(),
                    request.getDescripcion(),
                    request.getAnioPublicacion(),
                    request.getCalificacion()
            );

            response.put("success", true);
            response.put("message", "Agregado a favoritos exitosamente");
            response.put("favorito", favorito);
            
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            System.out.println("DEBUG - Error en agregarFavorito: " + e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            System.out.println("DEBUG - Error inesperado: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Eliminar un contenido de favoritos
     */
    @DeleteMapping("/eliminar")
    public ResponseEntity<Map<String, Object>> eliminarFavorito(
            @RequestParam String tipo,
            @RequestParam String contenidoId,
            Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String authName = authentication.getName();
            Favorito.TipoContenido tipoContenido = Favorito.TipoContenido.valueOf(tipo.toUpperCase());
            
            boolean eliminado = favoritoService.eliminarFavorito(
                    authName,
                    tipoContenido,
                    contenidoId
            );

            if (eliminado) {
                response.put("success", true);
                response.put("message", "Eliminado de favoritos exitosamente");
            } else {
                response.put("success", false);
                response.put("message", "No se encontró el favorito");
            }
            
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", "Tipo de contenido inválido");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Verificar si un contenido está en favoritos
     */
    @GetMapping("/verificar")
    public ResponseEntity<Map<String, Object>> verificarFavorito(
            @RequestParam String tipo,
            @RequestParam String contenidoId,
            Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String authName = authentication.getName();
            Favorito.TipoContenido tipoContenido = Favorito.TipoContenido.valueOf(tipo.toUpperCase());
            
            boolean esFavorito = favoritoService.esFavorito(
                    authName,
                    tipoContenido,
                    contenidoId
            );

            response.put("esFavorito", esFavorito);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("error", "Tipo de contenido inválido");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("error", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Obtener favoritos por tipo
     */
    @GetMapping("/listar")
    public ResponseEntity<Map<String, Object>> listarFavoritos(
            @RequestParam(required = false) String tipo,
            Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String authName = authentication.getName();
            List<Favorito> favoritos;
            
            if (tipo != null && !tipo.isEmpty()) {
                Favorito.TipoContenido tipoContenido = Favorito.TipoContenido.valueOf(tipo.toUpperCase());
                favoritos = favoritoService.obtenerFavoritosPorTipo(authName, tipoContenido);
            } else {
                favoritos = favoritoService.obtenerTodosFavoritos(authName);
            }

            response.put("favoritos", favoritos);
            response.put("total", favoritos.size());
            
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("error", "Tipo de contenido inválido");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("error", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Obtener estadísticas de favoritos
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<FavoritoService.FavoritoStats> obtenerEstadisticas(Authentication authentication) {
        try {
            String authName = authentication.getName();
            FavoritoService.FavoritoStats stats = favoritoService.obtenerEstadisticas(authName);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Buscar favoritos por título
     */
    @GetMapping("/buscar")
    public ResponseEntity<Map<String, Object>> buscarFavoritos(
            @RequestParam String titulo,
            Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String authName = authentication.getName();
            List<Favorito> favoritos = favoritoService.buscarFavoritosPorTitulo(
                    authName, titulo);

            response.put("favoritos", favoritos);
            response.put("total", favoritos.size());
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Clase para recibir requests de favoritos
     */
    public static class FavoritoRequest {
        private String tipo;
        private String contenidoId;
        private String titulo;
        private String autorDirector;
        private String imagenUrl;
        private String descripcion;
        private String anioPublicacion;
        private Double calificacion;

        // Getters y setters
        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }

        public String getContenidoId() { return contenidoId; }
        public void setContenidoId(String contenidoId) { this.contenidoId = contenidoId; }

        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }

        public String getAutorDirector() { return autorDirector; }
        public void setAutorDirector(String autorDirector) { this.autorDirector = autorDirector; }

        public String getImagenUrl() { return imagenUrl; }
        public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

        public String getAnioPublicacion() { return anioPublicacion; }
        public void setAnioPublicacion(String anioPublicacion) { this.anioPublicacion = anioPublicacion; }

        public Double getCalificacion() { return calificacion; }
        public void setCalificacion(Double calificacion) { this.calificacion = calificacion; }
    }
}