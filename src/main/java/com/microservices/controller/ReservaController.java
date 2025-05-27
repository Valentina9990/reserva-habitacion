package com.microservices.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.dto.ReservaDTO;
import com.microservices.models.Reserva;
import com.microservices.repository.ReservaRepository;
import com.microservices.service.ReservaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@WebServlet("/api/reservas/*")
public class ReservaController extends HttpServlet {
    private ObjectMapper mapper;
    private ReservaService service;
    private SimpleDateFormat dateFormat;

    @Override
    public void init() throws ServletException {
        mapper = new ObjectMapper();
        ReservaRepository repository = new ReservaRepository();
        service = new ReservaService(repository);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    private void setCorsHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setCorsHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCorsHeaders(resp);
        try {
            Reserva reserva = mapper.readValue(req.getInputStream(), Reserva.class);
            ReservaDTO dto = service.save(reserva);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setContentType("application/json");
            mapper.writeValue(resp.getWriter(), dto);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCorsHeaders(resp);
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"ID de reserva requerido\"}");
            return;
        }

        try {
            String[] pathParts = pathInfo.substring(1).split("/");
            String id = pathParts[0];

            if (pathParts.length > 1) {
                String action = pathParts[1];
                handlePutAction(id, action, req, resp);
            } else {
                // PUT /api/reservas/{id} - Actualizar reserva completa
                handleFullUpdate(id, req, resp);
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCorsHeaders(resp);
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"ID de reserva requerido\"}");
            return;
        }

        try {
            String id = pathInfo.substring(1);
            boolean deleted = service.delete(id);

            if (deleted) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.setContentType("application/json");
                resp.getWriter().write("{\"error\":\"Reserva no encontrada\"}");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCorsHeaders(resp);
        resp.setContentType("application/json");

        try {
            String pathInfo = req.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                handleGetAll(req, resp);
            } else {
                String id = pathInfo.substring(1);
                ReservaDTO reserva = service.findById(id);

                if (reserva != null) {
                    mapper.writeValue(resp.getWriter(), reserva);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\":\"Reserva no encontrada\"}");
                }
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void handleGetAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String usuarioIdParam = req.getParameter("usuarioId");
        String habitacionIdParam = req.getParameter("habitacionId");
        String estado = req.getParameter("estado");

        List<ReservaDTO> reservas;

        if (usuarioIdParam != null && !usuarioIdParam.isEmpty()) {
            int usuarioId = Integer.parseInt(usuarioIdParam);
            reservas = service.findByUsuarioId(usuarioId);
        } else if (habitacionIdParam != null && !habitacionIdParam.isEmpty()) {
            int habitacionId = Integer.parseInt(habitacionIdParam);
            reservas = service.findByHabitacionId(habitacionId);
        } else if (estado != null && !estado.isEmpty()) {
            reservas = service.findByEstado(estado);
        } else {
            reservas = service.findAll();
        }

        mapper.writeValue(resp.getWriter(), reservas);
    }

    private void handlePutAction(String id, String action, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        boolean success = false;

        switch (action.toLowerCase()) {
            case "confirmar":
                success = service.confirmarReserva(id);
                break;
            case "cancelar":
                success = service.cancelarReserva(id);
                break;
            default:
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Acción no válida: " + action + "\"}");
                return;
        }

        if (success) {
            resp.getWriter().write("{\"message\":\"Reserva " + action + "ada exitosamente\"}");
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\":\"No se pudo " + action + " la reserva\"}");
        }
    }

    private void handleFullUpdate(String id, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> updates = mapper.readValue(req.getInputStream(), Map.class);

            if (updates.containsKey("estado")) {
                String nuevoEstado = (String) updates.get("estado");
                boolean success = service.updateEstado(id, nuevoEstado);

                if (success) {
                    ReservaDTO updated = service.findById(id);
                    resp.setContentType("application/json");
                    mapper.writeValue(resp.getWriter(), updated);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.setContentType("application/json");
                    resp.getWriter().write("{\"error\":\"Reserva no encontrada\"}");
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.setContentType("application/json");
                resp.getWriter().write("{\"error\":\"Solo se permite actualizar el estado\"}");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
