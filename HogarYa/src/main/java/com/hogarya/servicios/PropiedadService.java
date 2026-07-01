package com.hogarya.servicios;

import com.hogarya.accesoDatos.HistorialEstadoPropiedadRepository;
import com.hogarya.accesoDatos.PropiedadRepository;
import com.hogarya.entidades.Propiedad;
import com.hogarya.entidades.enums.EstadoContrato;
import com.hogarya.entidades.enums.TipoPropiedad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hogarya.entidades.enums.EstadoDisponibilidad;
import com.hogarya.entidades.HistorialEstadoPropiedad;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

import java.util.List;

@Service
@Transactional
public class PropiedadService {

    @Autowired
    private PropiedadRepository propiedadRepository;

    @Autowired
    private HistorialEstadoPropiedadRepository historialRepository;
    //EPIC 1.4
    public List<Propiedad> listarActivas() {
        return propiedadRepository.findByEliminadaFalse();
    }

    // Historia 1.4 - Listado con filtros
    public List<Propiedad> buscarConFiltros(String direccion, Long ciudadId,
                                            TipoPropiedad tipo, EstadoDisponibilidad estado) {

        // Parte de todas las propiedades activas
        List<Propiedad> propiedades = propiedadRepository.findByEliminadaFalse();

        // Filtra por dirección (si se ingresó)
        if (direccion != null && !direccion.isEmpty()) {
            propiedades = propiedades.stream()
                    .filter(p -> p.getDireccion().toLowerCase().contains(direccion.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Filtra por ciudad (si se seleccionó)
        if (ciudadId != null) {
            propiedades = propiedades.stream()
                    .filter(p -> p.getCiudad().getId().equals(ciudadId))
                    .collect(Collectors.toList());
        }

        // Filtra por tipo (si se seleccionó)
        if (tipo != null) {
            propiedades = propiedades.stream()
                    .filter(p -> p.getTipo() == tipo)
                    .collect(Collectors.toList());
        }

        // Filtra por estado (si se seleccionó)
        if (estado != null) {
            propiedades = propiedades.stream()
                    .filter(p -> p.getEstadoDisponibilidad() == estado)
                    .collect(Collectors.toList());
        }

        return propiedades;
    }

    public Propiedad buscarPorId(Long id) {
        return propiedadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));
    }
    //EPIC 1.1
    public void guardar(Propiedad propiedad) {

        if (propiedad.getEstadoDisponibilidad() == null) {
            propiedad.setEstadoDisponibilidad(EstadoDisponibilidad.DISPONIBLE);
        }
        //Validación de propiedad (Dirección && Cuidad)
        Optional<Propiedad> existente = propiedadRepository
                .findByDireccionAndCiudad_IdAndEliminadaFalse(
                        propiedad.getDireccion(),
                        propiedad.getCiudad().getId()
                );

        if (existente.isPresent()) {
            throw new RuntimeException(
                    "Ya existe una propiedad activa en esa dirección y cuidad.");
        }

        propiedadRepository.save(propiedad);

        // Se registra la propiedad
        registrarHistorial(propiedad, propiedad.getEstadoDisponibilidad());
    }
    //AUXILIAR
    private void registrarHistorial(Propiedad propiedad, EstadoDisponibilidad estado) {
        HistorialEstadoPropiedad historial = new HistorialEstadoPropiedad();
        historial.setPropiedad(propiedad);
        historial.setEstado(estado);
        historial.setFechaHora(LocalDateTime.now());
        historialRepository.save(historial);
    }
    //EPIC 1.2
    public void eliminar(Long id) {
        Propiedad propiedad = propiedadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));

        // Validación Contrato
        boolean tieneContratoActivo = propiedad.getContratos().stream()
                .anyMatch(c -> c.getEstado() == EstadoContrato.ACTIVO && !c.isEliminado());

        if (tieneContratoActivo) {
            throw new RuntimeException(
                    "No se puede eliminar la propiedad porque tiene un contrato activo.");
        }

        // ELIMINACIóN LOGICA
        propiedad.setEliminada(true);
        propiedadRepository.save(propiedad);
    }
    //EPIC 1.3
    public void modificar(Propiedad propiedadModificada) {

        // Busqueda de propiedad
        Propiedad original = propiedadRepository.findById(propiedadModificada.getId())
                .orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));


        Optional<Propiedad> existente = propiedadRepository
                .findByDireccionAndCiudad_IdAndEliminadaFalse(
                        propiedadModificada.getDireccion(),
                        propiedadModificada.getCiudad().getId()
                );
        // Validación duplicados
        if (existente.isPresent() && !existente.get().getId().equals(original.getId())) {
            throw new RuntimeException(
                    "Ya existe otra propiedad activa con esa dirección y ciudad.");
        }

        // Validación Contrato activo
        boolean tieneContratoActivo = original.getContratos().stream().anyMatch(c -> c.getEstado() == EstadoContrato.ACTIVO && !c.isEliminado());

        EstadoDisponibilidad nuevoEstado = propiedadModificada.getEstadoDisponibilidad();

        if (tieneContratoActivo &&
                (nuevoEstado == EstadoDisponibilidad.DISPONIBLE ||
                        nuevoEstado == EstadoDisponibilidad.INACTIVA)) {
            throw new RuntimeException(
                    "No se puede cambiar el estado: la propiedad tiene un contrato activo.");
        }

        // Si cambió el estado, se registra en el historial
        if (original.getEstadoDisponibilidad() != nuevoEstado) {
            registrarHistorial(original, nuevoEstado);
        }

        // Se actualiza la base de datos
        original.setDireccion(propiedadModificada.getDireccion());
        original.setCiudad(propiedadModificada.getCiudad());
        original.setTipo(propiedadModificada.getTipo());
        original.setCantidadAmbientes(propiedadModificada.getCantidadAmbientes());
        original.setMetrosCuadrados(propiedadModificada.getMetrosCuadrados());
        original.setDescripcion(propiedadModificada.getDescripcion());
        original.setComodidades(propiedadModificada.getComodidades());
        original.setPropietario(propiedadModificada.getPropietario());
        original.setEstadoDisponibilidad(nuevoEstado);

        propiedadRepository.save(original);
    }

}