package com.microservices.repository;


import com.microservices.dao.ReservaDAO;
import com.microservices.models.Reserva;

import java.util.List;


public class ReservaRepository implements IReservaRepository {
    private final ReservaDAO reservaDAO = new ReservaDAO();

    @Override
    public Reserva save(Reserva reserva) {
        return reservaDAO.save(reserva);
    }

    @Override
    public Reserva findById(String id) {
        return reservaDAO.findById(id);
    }

    @Override
    public List<Reserva> findAll() {
        return reservaDAO.findAll();
    }

    @Override
    public List<Reserva> findByUsuarioId(int usuarioId) {
        return reservaDAO.findByUsuarioId(usuarioId);
    }

    @Override
    public List<Reserva> findByHabitacionId(int habitacionId) {
        return reservaDAO.findByHabitacionId(habitacionId);
    }

    @Override
    public List<Reserva> findByEstado(String estado) {
        return reservaDAO.findByEstado(estado);
    }

    @Override
    public boolean updateEstado(String id, String nuevoEstado) {
        return reservaDAO.updateEstado(id, nuevoEstado);
    }

    @Override
    public boolean delete(String id) {
        return reservaDAO.delete(id);
    }

}
