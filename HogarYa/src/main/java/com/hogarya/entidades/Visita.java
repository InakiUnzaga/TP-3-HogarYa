package com.hogarya.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
public class Visita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

 
    @ManyToOne
    @JoinColumn(name = "publicacion_id")
    private Publicacion publicacion;


    public Visita() {
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Publicacion getPublicacion() { return publicacion; }
    public void setPublicacion(Publicacion publicacion) { this.publicacion = publicacion; }
}