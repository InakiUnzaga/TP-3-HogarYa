package com.hogarya.servicios;

import com.hogarya.accesoDatos.PropiedadRepository;
import com.hogarya.entidades.Propiedad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PropiedadService {

    @Autowired
    private PropiedadRepository propiedadRepository;

    // 1. Listar todas las propiedades que no están marcadas como eliminadas
    @Transactional(readOnly = true)
    public List<Propiedad> listarPropiedadesActivas() {
        return propiedadRepository.findByEliminadaFalse();
    }

    // 2. Guardar una nueva propiedad
    @Transactional
    public void guardarPropiedad(Propiedad propiedad) {
        // Aquí podrías agregar validaciones antes de guardar
        propiedadRepository.save(propiedad);
    }

    // 3. Método para "borrado lógico" (cambiar estado a eliminada)
    @Transactional
    public void eliminarPropiedad(Long id) {
        propiedadRepository.findById(id).ifPresent(propiedad -> {
            propiedad.setEliminada(true);
            propiedadRepository.save(propiedad);
        });
    }
}