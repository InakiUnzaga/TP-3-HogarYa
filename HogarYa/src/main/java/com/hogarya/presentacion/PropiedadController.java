package com.hogarya.presentacion;

import com.hogarya.accesoDatos.CiudadRepository;
import com.hogarya.accesoDatos.PersonaRepository;
import com.hogarya.servicios.PropiedadService;
import com.hogarya.entidades.Propiedad;
import com.hogarya.entidades.enums.TipoPropiedad;
import com.hogarya.entidades.enums.EstadoDisponibilidad;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/propiedades")
public class PropiedadController {

    @Autowired
    private PropiedadService propiedadService;

    @Autowired
    private CiudadRepository ciudadRepository;

    @Autowired
    private PersonaRepository personaRepository;

    // EPIC 1.4 - Listado con filtros
    @GetMapping
    public String listar(
            @RequestParam(required = false) String direccion,
            @RequestParam(required = false) Long ciudadId,
            @RequestParam(required = false) TipoPropiedad tipo,
            @RequestParam(required = false) EstadoDisponibilidad estado,
            Model model) {

        model.addAttribute("propiedades",
                propiedadService.buscarConFiltros(direccion, ciudadId, tipo, estado));

        // Datos para llenar los desplegables de filtro
        model.addAttribute("ciudades", ciudadRepository.findAll());
        model.addAttribute("tipos", TipoPropiedad.values());
        model.addAttribute("estados", EstadoDisponibilidad.values());

        return "propiedades/listado";
    }

    // EPIC 1.1
    @GetMapping("/nueva")
    public String formularioNueva(Model model) {
        model.addAttribute("propiedad", new Propiedad());
        cargarDatosFormulario(model);
        return "propiedades/formulario";
    }

    // EPIC 1.1 - Recibe los datos y lo guarda - FormularioNuevo
    @PostMapping("/nueva")
    public String guardar(@ModelAttribute Propiedad propiedad, RedirectAttributes redirectAttributes) {
        try {
            propiedadService.guardar(propiedad);
            redirectAttributes.addFlashAttribute("exito",
                    "Propiedad registrada correctamente.");
            return "redirect:/propiedades";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/propiedades/nueva";
        }
    }

    // EPIC 1.3 - Muestra el formulario sin
    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("propiedad", propiedadService.buscarPorId(id));
            cargarDatosFormulario(model);
            return "propiedades/formulario";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/propiedades";
        }
    }

    // Historia 1.3 - Recibe los datos editados y los guarda
    @PostMapping("/editar/{id}")
    public String modificar(@PathVariable Long id, @ModelAttribute Propiedad propiedad, RedirectAttributes redirectAttributes) {
        propiedad.setId(id);
        try {
            propiedadService.modificar(propiedad);
            redirectAttributes.addFlashAttribute("exito",
                    "Propiedad modificada correctamente.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/propiedades";
    }

    // Historia 1.2 - Elimina (lógicamente) una propiedad
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            propiedadService.eliminar(id);
            redirectAttributes.addFlashAttribute("exito",
                    "Propiedad eliminada correctamente.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/propiedades";
    }

    // Carga los datos
    private void cargarDatosFormulario(Model model) {
        model.addAttribute("ciudades", ciudadRepository.findAll());
        model.addAttribute("propietarios", personaRepository.findByEliminadaFalse());
        model.addAttribute("tipos", TipoPropiedad.values());
        model.addAttribute("estados", EstadoDisponibilidad.values());
    }
}