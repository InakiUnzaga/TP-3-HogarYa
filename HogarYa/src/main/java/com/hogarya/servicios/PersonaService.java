package com.hogarya.servicios;

import com.hogarya.accesoDatos.PersonaRepository;
import com.hogarya.entidades.Persona;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PersonaService {

    @Autowired
    private PersonaRepository personaRepository;

    @Transactional
    public void registrarPersona(Persona persona) {
        personaRepository.save(persona);
    }

    @Transactional(readOnly = true)
    public List<Persona> listarPersonas() {
        return personaRepository.findAll();
    }
}