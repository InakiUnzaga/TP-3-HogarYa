package com.hogarya.controladores;

import com.hogarya.entidades.Propiedad;
import com.hogarya.servicios.PropiedadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/propiedad")
public class PropiedadControlador {

    @Autowired
    private PropiedadService propiedadService;

 
    @GetMapping("/listar")
    public List<Propiedad> listar() {
        return propiedadService.listarPropiedadesActivas();
    }

 
    @PostMapping("/guardar")
    public String guardar(@RequestBody Propiedad propiedad) {
        propiedadService.guardarPropiedad(propiedad);
        return "Propiedad guardada exitosamente";
    }
}