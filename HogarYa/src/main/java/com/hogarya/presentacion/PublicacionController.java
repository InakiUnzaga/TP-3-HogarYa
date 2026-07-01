package com.hogarya.presentacion;
 
import com.hogarya.accesoDatos.CiudadRepository;
import com.hogarya.accesoDatos.PropiedadRepository;
import com.hogarya.entidades.Publicacion;
import com.hogarya.entidades.enums.EstadoPublicacion;
import com.hogarya.servicios.PublicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
 
import java.math.BigDecimal;
 
@Controller
@RequestMapping("/publicaciones")
public class PublicacionController {
 
    @Autowired
    private PublicacionService publicacionService;
 
    @Autowired
    private PropiedadRepository propiedadRepository;
 
    @Autowired
    private CiudadRepository ciudadRepository;
 
    // EPIC 2.4 - Listado con filtros
    @GetMapping
    public String listar(
            @RequestParam(required = false) Long propiedadId,
            @RequestParam(required = false) Long ciudadId,
            @RequestParam(required = false) EstadoPublicacion estado,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax,
            Model model) {
 
        model.addAttribute("publicaciones",
                publicacionService.buscarConFiltros(propiedadId, ciudadId,
                        estado, precioMin, precioMax));
 
        model.addAttribute("propiedades", propiedadRepository.findByEliminadaFalse());
        model.addAttribute("ciudades", ciudadRepository.findAll());
        model.addAttribute("estados", EstadoPublicacion.values());
 
        return "publicaciones/listado";
    }
 
    // EPIC 2.1 - Formulario de alta
    @GetMapping("/nueva")
    public String formularioNueva(Model model) {
    	 model.addAttribute("publicacion", new Publicacion());
         cargarDatosFormulario(model);
         return "publicaciones/formulario";
     }
  
     // EPIC 2.1 - Guardar
     @PostMapping("/nueva")
     public String guardar(@ModelAttribute Publicacion publicacion,
                           RedirectAttributes redirectAttributes) {
         try {
             publicacionService.guardar(publicacion);
             redirectAttributes.addFlashAttribute("exito",
                     "Publicacion registrada correctamente.");
             return "redirect:/publicaciones";
         } catch (RuntimeException e) {
             redirectAttributes.addFlashAttribute("error", e.getMessage());
             return "redirect:/publicaciones/nueva";
         }
     }
  
     // EPIC 2.3 - Formulario de edicion
     @GetMapping("/editar/{id}")
     public String formularioEditar(@PathVariable Long id, Model model,
                                    RedirectAttributes redirectAttributes) {
         try {
             model.addAttribute("publicacion", publicacionService.buscarPorId(id));
             cargarDatosFormulario(model);
             return "publicaciones/formulario";
         } catch (RuntimeException e) {
             redirectAttributes.addFlashAttribute("error", e.getMessage());
             return "redirect:/publicaciones";
         }
     }
  
     // EPIC 2.3 - Guardar cambios
     @PostMapping("/editar/{id}")
     public String modificar(@PathVariable Long id,
                             @ModelAttribute Publicacion publicacion,
                             RedirectAttributes redirectAttributes) {
         publicacion.setId(id);
         try {
             publicacionService.modificar(publicacion);
             redirectAttributes.addFlashAttribute("exito",
                     "Publicacion modificada correctamente.");
         } catch (RuntimeException e) {
             redirectAttributes.addFlashAttribute("error", e.getMessage());
         }
         return "redirect:/publicaciones";
     }
  
     // EPIC 2.2 - Eliminar
     @PostMapping("/eliminar/{id}")
     public String eliminar(@PathVariable Long id,
                            RedirectAttributes redirectAttributes) {
         try {
             publicacionService.eliminar(id);
             redirectAttributes.addFlashAttribute("exito",
                     "Publicacion eliminada correctamente.");
         } catch (RuntimeException e) {
             redirectAttributes.addFlashAttribute("error", e.getMessage());
         }
         return "redirect:/publicaciones";
     }
     // Carga los datos de los desplegables
     private void cargarDatosFormulario(Model model) {
         // Solo propiedades disponibles se pueden publicar
         model.addAttribute("propiedades", propiedadRepository.findByEliminadaFalse());
         model.addAttribute("estados", EstadoPublicacion.values());
     }
 }
