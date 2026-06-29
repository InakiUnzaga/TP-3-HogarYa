package com.hogarya.accesoDatos;

import com.hogarya.entidades.HistorialEstadoPropiedad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialEstadoPropiedadRepository
        extends JpaRepository<HistorialEstadoPropiedad, Long> {
}