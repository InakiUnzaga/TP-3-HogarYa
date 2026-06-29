package com.hogarya.entidades;

import com.hogarya.entidades.enums.EstadoFactura;
import com.hogarya.entidades.enums.MedioPago;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "factura")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private BigDecimal importe;

    @Enumerated(EnumType.STRING)
    private EstadoFactura estado;

    private boolean eliminada = false;
    private LocalDate fechaPago;

    @Enumerated(EnumType.STRING)
    private MedioPago medio;

    private BigDecimal importePagado;
    private BigDecimal interes;
    private String conceptoFacturado;

    @ManyToOne
    @JoinColumn(name = "contrato_id")
    private Contrato contrato;

    @OneToMany(mappedBy = "factura")
    private List<HistorialEstadoFactura> historialEstados;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public EstadoFactura getEstado() {
        return estado;
    }

    public void setEstado(EstadoFactura estado) {
        this.estado = estado;
    }

    public boolean isEliminada() {
        return eliminada;
    }

    public void setEliminada(boolean eliminada) {
        this.eliminada = eliminada;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public MedioPago getMedio() {
        return medio;
    }

    public void setMedio(MedioPago medio) {
        this.medio = medio;
    }

    public BigDecimal getImportePagado() {
        return importePagado;
    }

    public void setImportePagado(BigDecimal importePagado) {
        this.importePagado = importePagado;
    }

    public BigDecimal getInteres() {
        return interes;
    }

    public void setInteres(BigDecimal interes) {
        this.interes = interes;
    }

    public String getConceptoFacturado() {
        return conceptoFacturado;
    }

    public void setConceptoFacturado(String conceptoFacturado) {
        this.conceptoFacturado = conceptoFacturado;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public List<HistorialEstadoFactura> getHistorialEstados() {
        return historialEstados;
    }

    public void setHistorialEstados(List<HistorialEstadoFactura> historialEstados) {
        this.historialEstados = historialEstados;
    }

}