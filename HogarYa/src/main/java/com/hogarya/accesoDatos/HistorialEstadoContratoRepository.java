package com.hogarya.accesoDatos;


import com.hogarya.entidades.HistorialEstadoContrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialEstadoContratoRepository
        extends JpaRepository<HistorialEstadoContrato, Long> {
}
