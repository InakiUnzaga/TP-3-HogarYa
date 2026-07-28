package com.hogarya.servicios;


import com.hogarya.accesoDatos.ContratoRepository;
import com.hogarya.accesoDatos.HistorialEstadoContratoRepository;
import com.hogarya.accesoDatos.HistorialEstadoPropiedadRepository;
import com.hogarya.accesoDatos.PropiedadRepository;
import com.hogarya.entidades.Contrato;
import com.hogarya.entidades.HistorialEstadoContrato;
import com.hogarya.entidades.HistorialEstadoPropiedad;
import com.hogarya.entidades.Propiedad;
import com.hogarya.entidades.enums.EstadoContrato;
import com.hogarya.entidades.enums.EstadoDisponibilidad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ContratoService {

    @Autowired
    private ContratoRepository contratoRepository;

    @Autowired
    private HistorialEstadoContratoRepository historialRepository;

    @Autowired
    private PropiedadRepository propiedadRepository;

    @Autowired
    private HistorialEstadoPropiedadRepository historialPropiedadRepository;

    //  Listado
    public List<Contrato> listarActivos() {
        return contratoRepository.findByEliminadoFalse();
    }

    // Listado con filtros
    public List<Contrato> buscarConFiltros(Long propiedadId, Long inquilinoId,
                                           EstadoContrato estado, LocalDate fechaInicio) {

        List<Contrato> contratos = contratoRepository.findByEliminadoFalse();

        if (propiedadId != null) {
            contratos = contratos.stream()
                    .filter(c -> c.getPropiedad().getId().equals(propiedadId))
                    .collect(Collectors.toList());
        }
        if (inquilinoId != null) {
            contratos = contratos.stream()
                    .filter(c -> c.getInquilino().getId().equals(inquilinoId))
                    .collect(Collectors.toList());
        }
        if (estado != null) {
            contratos = contratos.stream()
                    .filter(c -> c.getEstado() == estado)
                    .collect(Collectors.toList());
        }
        if (fechaInicio != null) {
            contratos = contratos.stream()
                    .filter(c -> c.getFechaInicio().equals(fechaInicio))
                    .collect(Collectors.toList());
        }
        return contratos;
    }

    public Contrato buscarPorId(Long id) {
        return contratoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrato no encontrado"));
    }

    //  Alta
    public void guardar(Contrato contrato) {


        if (contrato.getEstado() == null) {
            contrato.setEstado(EstadoContrato.BORRADOR);
        }


        if (contrato.getEstado() == EstadoContrato.ACTIVO) {
            validarActivacion(contrato.getPropiedad().getId(), null);
        }

        contratoRepository.save(contrato);
        registrarHistorial(contrato, contrato.getEstado());


        if (contrato.getEstado() == EstadoContrato.ACTIVO) {
            cambiarEstadoPropiedad(contrato.getPropiedad(), EstadoDisponibilidad.ALQUILADA);
        }
    }


    public void eliminar(Long id) {
        Contrato contrato = buscarPorId(id);


        if (contrato.getEstado() != EstadoContrato.BORRADOR) {
            throw new RuntimeException(
                    "Solo se pueden eliminar contratos en estado borrador.");
        }

        contrato.setEliminado(true);
        contratoRepository.save(contrato);

    }

    // Modificacion
    public void modificar(Contrato contratoModificado) {
        Contrato original = buscarPorId(contratoModificado.getId());
        EstadoContrato estadoAnterior = original.getEstado();
        EstadoContrato nuevoEstado = contratoModificado.getEstado();

        validarTransicionEstado(estadoAnterior, nuevoEstado);


        if (nuevoEstado == EstadoContrato.ACTIVO && estadoAnterior != EstadoContrato.ACTIVO) {
            validarActivacion(original.getPropiedad().getId(), original.getId());
        }


        if (estadoAnterior != nuevoEstado) {
            registrarHistorial(original, nuevoEstado);
        }

        // Actualiza los datos
        original.setPropiedad(contratoModificado.getPropiedad());
        original.setInquilino(contratoModificado.getInquilino());
        original.setFechaInicio(contratoModificado.getFechaInicio());
        original.setDuracionMeses(contratoModificado.getDuracionMeses());
        original.setImporteMensual(contratoModificado.getImporteMensual());
        original.setDiaVencimientoMensual(contratoModificado.getDiaVencimientoMensual());
        original.setDescripcion(contratoModificado.getDescripcion());
        original.setEstado(nuevoEstado);

        contratoRepository.save(original);

        // Efecto sobre la propiedad
        if (nuevoEstado == EstadoContrato.ACTIVO && estadoAnterior != EstadoContrato.ACTIVO) {
            cambiarEstadoPropiedad(original.getPropiedad(), EstadoDisponibilidad.ALQUILADA);
        } else if ((nuevoEstado == EstadoContrato.FINALIZADO || nuevoEstado ==
                EstadoContrato.RESCINDIDO)
                && estadoAnterior == EstadoContrato.ACTIVO) {
            cambiarEstadoPropiedad(original.getPropiedad(), EstadoDisponibilidad.DISPONIBLE);
        }
    }


    private void validarActivacion(Long propiedadId, Long contratoIdExcluido) {
        Propiedad propiedad = propiedadRepository.findById(propiedadId)
                .orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));

        if (propiedad.getEstadoDisponibilidad() != EstadoDisponibilidad.DISPONIBLE) {
            throw new RuntimeException(
                    "No se puede activar el contrato: la propiedad no esta disponible.");
        }

        Optional<Contrato> existente = contratoRepository
                .findByPropiedad_IdAndEstadoAndEliminadoFalse(propiedadId, EstadoContrato.ACTIVO);

        if (existente.isPresent() &&
                (contratoIdExcluido == null || !existente.get().getId().equals(contratoIdExcluido))) {
            throw new RuntimeException("La propiedad ya tiene un contrato activo.");
        }
    }


    private void validarTransicionEstado(EstadoContrato anterior, EstadoContrato nuevo) {
        if ((anterior == EstadoContrato.FINALIZADO || anterior == EstadoContrato.RESCINDIDO)
                && nuevo == EstadoContrato.ACTIVO) {
            throw new RuntimeException(
                    "No se puede volver de '" + anterior + "' a 'activo'.");
        }
    }


    private void cambiarEstadoPropiedad(Propiedad propiedad, EstadoDisponibilidad nuevoEstado) {
        propiedad.setEstadoDisponibilidad(nuevoEstado);
        propiedadRepository.save(propiedad);
        HistorialEstadoPropiedad historial = new HistorialEstadoPropiedad();
        historial.setPropiedad(propiedad);
        historial.setEstado(nuevoEstado);
        historial.setFechaHora(LocalDateTime.now());
        historialPropiedadRepository.save(historial);
    }


    private void registrarHistorial(Contrato contrato, EstadoContrato estado) {
        HistorialEstadoContrato historial = new HistorialEstadoContrato();
        historial.setContrato(contrato);
        historial.setEstado(estado);
        historial.setFechaHora(LocalDateTime.now());
        historialRepository.save(historial);
    }
}

