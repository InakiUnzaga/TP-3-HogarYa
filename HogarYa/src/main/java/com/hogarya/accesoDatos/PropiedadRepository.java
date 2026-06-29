package com.hogarya.accesoDatos;

import com.hogarya.entidades.Propiedad;
import com.hogarya.entidades.enums.EstadoDisponibilidad;
import com.hogarya.entidades.enums.TipoPropiedad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PropiedadRepository extends JpaRepository<Propiedad, Long> {

    List<Propiedad> findByEliminadaFalse();

    Optional<Propiedad> findByDireccionAndCiudad_IdAndEliminadaFalse(
            String direccion, Long ciudadId
    );

    List<Propiedad> findByEliminadaFalseAndTipo(TipoPropiedad tipo);

    List<Propiedad> findByEliminadaFalseAndEstadoDisponibilidad(
            EstadoDisponibilidad estado
    );
}