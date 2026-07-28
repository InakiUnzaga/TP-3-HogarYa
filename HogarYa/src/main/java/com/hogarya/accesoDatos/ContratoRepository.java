package com.hogarya.accesoDatos;

import com.hogarya.entidades.Contrato;
import com.hogarya.entidades.enums.EstadoContrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContratoRepository extends JpaRepository<Contrato, Long> {

    List<Contrato> findByEliminadoFalse();


    Optional<Contrato> findByPropiedad_IdAndEstadoAndEliminadoFalse(
            Long propiedadId, EstadoContrato estado
    );
}
