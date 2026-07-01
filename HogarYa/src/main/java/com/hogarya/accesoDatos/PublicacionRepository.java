package com.hogarya.accesoDatos;
 
import com.hogarya.entidades.Publicacion;
import com.hogarya.entidades.enums.EstadoPublicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
 
@Repository
public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {
 
    // Listado: solo las publicaciones no eliminadas
    List<Publicacion> findByEliminadaFalse();
 
    // Para validar que no haya otra publicacion activa de la misma propiedad
    Optional<Publicacion> findByPropiedad_IdAndEstadoAndEliminadaFalse(
            Long propiedadId, EstadoPublicacion estado
    );
}

