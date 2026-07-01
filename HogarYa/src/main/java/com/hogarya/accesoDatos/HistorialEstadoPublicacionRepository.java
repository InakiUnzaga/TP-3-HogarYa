package com.hogarya.accesoDatos;
 
import com.hogarya.entidades.HistorialEstadoPublicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
@Repository
public interface HistorialEstadoPublicacionRepository
        extends JpaRepository<HistorialEstadoPublicacion, Long> {
}
