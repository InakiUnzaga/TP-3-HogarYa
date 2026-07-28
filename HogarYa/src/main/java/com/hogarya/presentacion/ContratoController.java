package com.hogarya.presentacion;

import com.hogarya.accesoDatos.PersonaRepository;
import com.hogarya.accesoDatos.PropiedadRepository;
import com.hogarya.entidades.Contrato;
import com.hogarya.entidades.enums.EstadoContrato;
import com.hogarya.servicios.ContratoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/contratos")
public class ContratoController {

    @Autowired
    private ContratoService contratoService;

    @Autowired
    private PropiedadRepository propiedadRepository;

    @Autowired
    private PersonaRepository personaRepository;

    //  Listado con filtros
    @GetMapping
    public String listar(
            @RequestParam(required = false) Long propiedadId,
            @RequestParam(required = false) Long inquilinoId,
            @RequestParam(required = false) EstadoContrato estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate
                    fechaInicio,
            Model model) {

        model.addAttribute("contratos",
                contratoService.buscarConFiltros(propiedadId, inquilinoId, estado, fechaInicio));

        model.addAttribute("propiedades", propiedadRepository.findByEliminadaFalse());
        model.addAttribute("inquilinos", personaRepository.findByEliminadaFalse());
        model.addAttribute("estados", EstadoContrato.values());

        return "contratos/listado";
    }

    //  Formulario de alta
    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("contrato", new Contrato());
        cargarDatosFormulario(model);
        return "contratos/formulario";
    }
//  Guardar
@PostMapping("/nuevo")
public String guardar(@ModelAttribute Contrato contrato,
                      RedirectAttributes redirectAttributes) {
    try {
        contratoService.guardar(contrato);
        redirectAttributes.addFlashAttribute("exito",
                "Contrato registrado correctamente.");
        return "redirect:/contratos";
    } catch (RuntimeException e) {
        redirectAttributes.addFlashAttribute("error", e.getMessage());
        return "redirect:/contratos/nuevo";
    }
}

    // EPIC 3.3 - Formulario de edicion
    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model model,
                                   RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("contrato", contratoService.buscarPorId(id));
            cargarDatosFormulario(model);
            return "contratos/formulario";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/contratos";
        }
    }

    // Guardar cambios
    @PostMapping("/editar/{id}")
    public String modificar(@PathVariable Long id,
                            @ModelAttribute Contrato contrato,
                            RedirectAttributes redirectAttributes) {
        contrato.setId(id);
        try {
            contratoService.modificar(contrato);
            redirectAttributes.addFlashAttribute("exito",
                    "Contrato modificado correctamente.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/contratos";
    }

    // Eliminar
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,
                           RedirectAttributes redirectAttributes) {
        try {
            contratoService.eliminar(id);
            redirectAttributes.addFlashAttribute("exito",
                    "Contrato eliminado correctamente.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/contratos";
    }

    private void cargarDatosFormulario(Model model) {
        model.addAttribute("propiedades", propiedadRepository.findByEliminadaFalse());
        model.addAttribute("inquilinos", personaRepository.findByEliminadaFalse());
        model.addAttribute("estados", EstadoContrato.values());
    }
}
