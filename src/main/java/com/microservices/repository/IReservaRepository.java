package com.microservices.repository;

import com.microservices.models.Reserva;

import java.util.List;

public interface IReservaRepository {
    Reserva save(Reserva reserva);
    Reserva findById(String id);
    List<Reserva> findAll();
    List<Reserva> findByUsuarioId(int usuarioId);
    List<Reserva> findByHabitacionId(int habitacionId);
    List<Reserva> findByEstado(String estado);
    boolean updateEstado(String id, String nuevoEstado);
    boolean delete(String id);

}