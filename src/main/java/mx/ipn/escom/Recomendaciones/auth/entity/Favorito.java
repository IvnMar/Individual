package mx.ipn.escom.Recomendaciones.auth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "favoritos")
public class Favorito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_contenido", nullable = false)
    private TipoContenido tipoContenido;

    @Column(name = "contenido_id", nullable = false)
    private String contenidoId;

    @Column(nullable = false, length = 500)
    private String titulo;

    @Column(name = "autor_director")
    private String autorDirector;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "anio_publicacion")
    private String anioPublicacion;

    @Column
    private Double calificacion;

    @Column(name = "fecha_agregado")
    private LocalDateTime fechaAgregado;

    // Enum para tipo de contenido
    public enum TipoContenido {
        LIBRO, PELICULA
    }

    // Constructor vacío
    public Favorito() {
        this.fechaAgregado = LocalDateTime.now();
    }

    // Constructor con parámetros básicos
    public Favorito(Usuario usuario, TipoContenido tipoContenido, String contenidoId, String titulo) {
        this();
        this.usuario = usuario;
        this.tipoContenido = tipoContenido;
        this.contenidoId = contenidoId;
        this.titulo = titulo;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public TipoContenido getTipoContenido() {
        return tipoContenido;
    }

    public void setTipoContenido(TipoContenido tipoContenido) {
        this.tipoContenido = tipoContenido;
    }

    public String getContenidoId() {
        return contenidoId;
    }

    public void setContenidoId(String contenidoId) {
        this.contenidoId = contenidoId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutorDirector() {
        return autorDirector;
    }

    public void setAutorDirector(String autorDirector) {
        this.autorDirector = autorDirector;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getAnioPublicacion() {
        return anioPublicacion;
    }

    public void setAnioPublicacion(String anioPublicacion) {
        this.anioPublicacion = anioPublicacion;
    }

    public Double getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Double calificacion) {
        this.calificacion = calificacion;
    }

    public LocalDateTime getFechaAgregado() {
        return fechaAgregado;
    }

    public void setFechaAgregado(LocalDateTime fechaAgregado) {
        this.fechaAgregado = fechaAgregado;
    }

    @PrePersist
    protected void onCreate() {
        if (fechaAgregado == null) {
            fechaAgregado = LocalDateTime.now();
        }
    }

    @Override
    public String toString() {
        return "Favorito{" +
                "id=" + id +
                ", tipoContenido=" + tipoContenido +
                ", contenidoId='" + contenidoId + '\'' +
                ", titulo='" + titulo + '\'' +
                ", fechaAgregado=" + fechaAgregado +
                '}';
    }
}