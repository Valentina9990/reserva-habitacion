package com.microservices.mapper;


import com.microservices.dto.ReservaDTO;
import com.microservices.models.Reserva;
import org.bson.types.ObjectId;

public class ReservaMapper {

    public static ReservaDTO toDto(Reserva reserva) {
        if (reserva == null) {
            return null;
        }

        ReservaDTO dto = new ReservaDTO();
        dto.setId(reserva.getId() != null ? reserva.getId().toString() : null);
        dto.setHabitacionId(reserva.getHabitacionId());
        dto.setUsuarioId(reserva.getUsuarioId());
        dto.setFechaCheckin(reserva.getFechaCheckin());
        dto.setFechaCheckout(reserva.getFechaCheckout());
        dto.setEstado(reserva.getEstado());
        dto.setTotal(reserva.getTotal());
        dto.setFechaCreacion(reserva.getFechaCreacion());

        return dto;
    }

    public static Reserva toEntity(ReservaDTO dto) {
        if (dto == null) {
            return null;
        }

        Reserva reserva = new Reserva();
        if (dto.getId() != null && !dto.getId().isEmpty()) {
            reserva.setId(new ObjectId(dto.getId()));
        }
        reserva.setHabitacionId(dto.getHabitacionId());
        reserva.setUsuarioId(dto.getUsuarioId());
        reserva.setFechaCheckin(dto.getFechaCheckin());
        reserva.setFechaCheckout(dto.getFechaCheckout());
        reserva.setEstado(dto.getEstado());
        reserva.setTotal(dto.getTotal());
        reserva.setFechaCreacion(dto.getFechaCreacion());

        return reserva;
    }
}
