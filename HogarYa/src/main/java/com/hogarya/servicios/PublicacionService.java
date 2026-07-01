package com.hogarya.servicios;
 
import com.hogarya.accesoDatos.HistorialEstadoPublicacionRepository;
import com.hogarya.accesoDatos.PublicacionRepository;
import com.hogarya.entidades.Publicacion;
import com.hogarya.entidades.HistorialEstadoPublicacion;
import com.hogarya.entidades.enums.EstadoDisponibilidad;
import com.hogarya.entidades.enums.EstadoPublicacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
 
@Service
@Transactional
public class PublicacionService {
 
    @Autowired
    private PublicacionRepository publicacionRepository;
 
    @Autowired
    private HistorialEstadoPublicacionRepository historialRepository;
 
    // EPIC 2.4 - Listado
    public List<Publicacion> listarActivas() {
        return publicacionRepository.findByEliminadaFalse();
    }
 
    // EPIC 2.4 - Listado con filtros
    public List<Publicacion> buscarConFiltros(Long propiedadId, Long ciudadId,
                                              EstadoPublicacion estado,
                                              java.math.BigDecimal precioMin,
                                              java.math.BigDecimal precioMax) {
 
        List<Publicacion> publicaciones = publicacionRepository.findByEliminadaFalse();
 
        // Filtra por propiedad
        if (propiedadId != null) {
            publicaciones = publicaciones.stream()
                    .filter(p -> p.getPropiedad().getId().equals(propiedadId))
                    .collect(Collectors.toList());
        }
 
        // Filtra por ciudad (de la propiedad)
        if (ciudadId != null) {
            publicaciones = publicaciones.stream()
            		 .filter(p -> p.getPropiedad().getCiudad().getId().equals(ciudadId))
                     .collect(Collectors.toList());
         }
  
         // Filtra por estado
         if (estado != null) {
             publicaciones = publicaciones.stream()
                     .filter(p -> p.getEstado() == estado)
                     .collect(Collectors.toList());
         }
  
         // Filtra por precio minimo
         if (precioMin != null) {
             publicaciones = publicaciones.stream()
                     .filter(p -> p.getPrecioMensual().compareTo(precioMin) >= 0)
                     .collect(Collectors.toList());
         }
  
         // Filtra por precio maximo
         if (precioMax != null) {
             publicaciones = publicaciones.stream()
                     .filter(p -> p.getPrecioMensual().compareTo(precioMax) <= 0)
                     .collect(Collectors.toList());
         }
  
         return publicaciones;
     }
  
     public Publicacion buscarPorId(Long id) {
         return publicacionRepository.findById(id)
                 .orElseThrow(() -> new RuntimeException("Publicacion no encontrada"));
     }
  
     // EPIC 2.1 - Alta
     public void guardar(Publicacion publicacion) {
  
         // Estado por defecto: ACTIVA
         if (publicacion.getEstado() == null) {
             publicacion.setEstado(EstadoPublicacion.ACTIVA);
         }
  
         // Solo se puede publicar una propiedad disponible
         if (publicacion.getPropiedad().getEstadoDisponibilidad()
                 != EstadoDisponibilidad.DISPONIBLE) {
             throw new RuntimeException(
                     "Solo se puede publicar una propiedad que este disponible.");
         }
  
         // No puede haber otra publicacion activa de la misma propiedad
         Optional<Publicacion> existente = publicacionRepository
                 .findByPropiedad_IdAndEstadoAndEliminadaFalse(
                         publicacion.getPropiedad().getId(),
                         EstadoPublicacion.ACTIVA
                 );
  
         if (existente.isPresent()) {
             throw new RuntimeException(
                     "Ya existe una publicacion activa para esa propiedad.");
         }
  
         publicacionRepository.save(publicacion);
         registrarHistorial(publicacion, publicacion.getEstado());
     }
  // AUXILIAR - registra un cambio de estado en el historial
     private void registrarHistorial(Publicacion publicacion, EstadoPublicacion estado) {
         HistorialEstadoPublicacion historial = new HistorialEstadoPublicacion();
         historial.setPublicacion(publicacion);
         historial.setEstado(estado);
         historial.setFechaHora(LocalDateTime.now());
         historialRepository.save(historial);
     }
  
     // EPIC 2.2 - Eliminacion logica
     public void eliminar(Long id) {
         Publicacion publicacion = buscarPorId(id);
  
         // Solo se pueden eliminar publicaciones activas
         if (publicacion.getEstado() != EstadoPublicacion.ACTIVA) {
             throw new RuntimeException(
                     "Solo se pueden eliminar publicaciones activas.");
         }
  
         publicacion.setEliminada(true);
         publicacionRepository.save(publicacion);
     }
  
     // EPIC 2.3 - Modificacion
     public void modificar(Publicacion publicacionModificada) {
  
         Publicacion original = buscarPorId(publicacionModificada.getId());
  
         EstadoPublicacion nuevoEstado = publicacionModificada.getEstado();
  
         // Si se quiere dejar ACTIVA, validar que no haya otra activa de la propiedad
         if (nuevoEstado == EstadoPublicacion.ACTIVA) {
             Optional<Publicacion> existente = publicacionRepository
                     .findByPropiedad_IdAndEstadoAndEliminadaFalse(
                             original.getPropiedad().getId(),
                             EstadoPublicacion.ACTIVA
                     );
             if (existente.isPresent() &&
                 !existente.get().getId().equals(original.getId())) {
                 throw new RuntimeException(
                         "Ya existe otra publicacion activa para esa propiedad.");
             }
         }
  
         // Si cambio el estado, registrarlo en el historial
         if (original.getEstado() != nuevoEstado) {
             registrarHistorial(original, nuevoEstado);
         }
  
         // Actualiza los datos (la propiedad NO se cambia, es de solo lectura)
         original.setPrecioMensual(publicacionModificada.getPrecioMensual());
         original.setCondiciones(publicacionModificada.getCondiciones());
         original.setDescripcion(publicacionModificada.getDescripcion());
         original.setFechaPublicacion(publicacionModificada.getFechaPublicacion());
         original.setEstado(nuevoEstado);
  
         publicacionRepository.save(original);
     }
 }
