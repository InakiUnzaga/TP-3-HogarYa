package com.hogarya.controladores;

import com.hogarya.entidades.Persona;
import com.hogarya.servicios.PersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/persona")
public class PersonaControlador {

    @Autowired
    private PersonaService personaService;

    @PostMapping("/registrar")
    public String registrar(@RequestBody Persona persona) {
        personaService.registrarPersona(persona);
        return "Persona registrada correctamente";
    }

    @GetMapping("/listar")
    public List<Persona> listar() {
        return personaService.listarPersonas();
    }
}