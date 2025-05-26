package com.microservices.service;

import com.microservices.dto.ReservaDTO;
import com.microservices.mapper.ReservaMapper;
import com.microservices.models.Reserva;
import com.microservices.repository.ReservaRepository;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ReservaService {
    private final ReservaRepository repository;

    public ReservaService(ReservaRepository repository) {
        this.repository = repository;
    }

    public ReservaDTO save(Reserva reserva) throws Exception {
        if (reserva.getHabitacionId() <= 0) {
            throw new IllegalArgumentException("ID de habitación inválido");
        }

        if (reserva.getUsuarioId() <= 0) {
            throw new IllegalArgumentException("ID de usuario inválido");
        }

        if (reserva.getEstado() == null || reserva.getEstado().isEmpty()) {
            reserva.setEstado("pendiente");
        }

        if (reserva.getFechaCreacion() == null) {
            reserva.setFechaCreacion(new Date());
        }

        Reserva saved = repository.save(reserva);
        return ReservaMapper.toDto(saved);
    }

    public ReservaDTO findById(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }

        Reserva reserva = repository.findById(id);
        return ReservaMapper.toDto(reserva);
    }

    public List<ReservaDTO> findAll() {
        return repository.findAll().stream()
                .map(ReservaMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ReservaDTO> findByUsuarioId(int usuarioId) {
        if (usuarioId <= 0) {
            throw new IllegalArgumentException("ID de usuario inválido");
        }

        return repository.findByUsuarioId(usuarioId).stream()
                .map(ReservaMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ReservaDTO> findByHabitacionId(int habitacionId) {
        if (habitacionId <= 0) {
            throw new IllegalArgumentException("ID de habitación inválido");
        }

        return repository.findByHabitacionId(habitacionId).stream()
                .map(ReservaMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ReservaDTO> findByEstado(String estado) {
        if (estado == null || estado.isEmpty()) {
            throw new IllegalArgumentException("Estado no puede ser nulo o vacío");
        }

        return repository.findByEstado(estado).stream()
                .map(ReservaMapper::toDto)
                .collect(Collectors.toList());
    }

    public boolean updateEstado(String id, String nuevoEstado) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ID de reserva inválido");
        }

        if (nuevoEstado == null || nuevoEstado.isEmpty()) {
            throw new IllegalArgumentException("Estado no puede ser nulo o vacío");
        }

        // Validar estados permitidos
        String[] estadosValidos = {"pendiente", "confirmada", "cancelada", "completada"};
        boolean estadoValido = false;
        for (String estado : estadosValidos) {
            if (estado.equals(nuevoEstado)) {
                estadoValido = true;
                break;
            }
        }

        if (!estadoValido) {
            throw new IllegalArgumentException("Estado no válido: " + nuevoEstado);
        }

        return repository.updateEstado(id, nuevoEstado);
    }

    public boolean cancelarReserva(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ID de reserva inválido");
        }

        // Verificar que la reserva existe y se puede cancelar
        Reserva reserva = repository.findById(id);
        if (reserva == null) {
            return false;
        }

        if ("cancelada".equals(reserva.getEstado()) || "completada".equals(reserva.getEstado())) {
            throw new IllegalArgumentException("No se puede cancelar una reserva en estado: " + reserva.getEstado());
        }

        return repository.updateEstado(id, "cancelada");
    }

    public boolean confirmarReserva(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ID de reserva inválido");
        }

        Reserva reserva = repository.findById(id);
        if (reserva == null) {
            return false;
        }

        if (!"pendiente".equals(reserva.getEstado())) {
            throw new IllegalArgumentException("Solo se pueden confirmar reservas en estado pendiente");
        }

        return repository.updateEstado(id, "confirmada");
    }

    public boolean delete(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ID de reserva inválido");
        }

        return repository.delete(id);
    }

}