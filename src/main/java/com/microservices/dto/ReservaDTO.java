package com.microservices.dto;


import java.util.Date;

public class ReservaDTO {
    private String id;
    private int habitacionId;
    private int usuarioId;
    private Date fechaCheckin;
    private Date fechaCheckout;
    private String estado;
    private double total;
    private Date fechaCreacion;

    public ReservaDTO() {}

    public ReservaDTO(String id, int habitacionId, int usuarioId,
                      Date fechaCheckin, Date fechaCheckout,
                      String estado, double total, Date fechaCreacion) {
        this.id = id;
        this.habitacionId = habitacionId;
        this.usuarioId = usuarioId;
        this.fechaCheckin = fechaCheckin;
        this.fechaCheckout = fechaCheckout;
        this.estado = estado;
        this.total = total;
        this.fechaCreacion = fechaCreacion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getHabitacionId() {
        return habitacionId;
    }

    public void setHabitacionId(int habitacionId) {
        this.habitacionId = habitacionId;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Date getFechaCheckin() {
        return fechaCheckin;
    }

    public void setFechaCheckin(Date fechaCheckin) {
        this.fechaCheckin = fechaCheckin;
    }

    public Date getFechaCheckout() {
        return fechaCheckout;
    }

    public void setFechaCheckout(Date fechaCheckout) {
        this.fechaCheckout = fechaCheckout;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}