package com.microservices.dao;



import com.microservices.config.MongoDatabaseConnection;
import com.microservices.models.Reserva;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReservaDAO {
    private final MongoCollection<Document> collection;

    public ReservaDAO() {
        MongoDatabase database = MongoDatabaseConnection.getInstance().getDatabase();
        this.collection = database.getCollection("reservas");
    }

    public Reserva save(Reserva reserva) {
        Document doc = new Document();

        if (reserva.getId() != null) {
            doc.append("_id", reserva.getId());
        }

        doc.append("habitacionId", reserva.getHabitacionId())
                .append("usuarioId", reserva.getUsuarioId())
                .append("fechaCheckin", reserva.getFechaCheckin())
                .append("fechaCheckout", reserva.getFechaCheckout())
                .append("estado", reserva.getEstado())
                .append("total", reserva.getTotal())
                .append("fechaCreacion", reserva.getFechaCreacion() != null ? reserva.getFechaCreacion() : new Date());

        if (reserva.getId() == null) {
            collection.insertOne(doc);
            reserva.setId(doc.getObjectId("_id"));
        } else {
            collection.replaceOne(Filters.eq("_id", reserva.getId()), doc);
        }

        return reserva;
    }

    public Reserva findById(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }

        try {
            ObjectId objectId = new ObjectId(id);
            Document doc = collection.find(Filters.eq("_id", objectId)).first();
            return documentToReserva(doc);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public List<Reserva> findAll() {
        List<Reserva> reservas = new ArrayList<>();
        for (Document doc : collection.find()) {
            reservas.add(documentToReserva(doc));
        }
        return reservas;
    }

    public List<Reserva> findByUsuarioId(int usuarioId) {
        List<Reserva> reservas = new ArrayList<>();
        for (Document doc : collection.find(Filters.eq("usuarioId", usuarioId))) {
            reservas.add(documentToReserva(doc));
        }
        return reservas;
    }

    public List<Reserva> findByHabitacionId(int habitacionId) {
        List<Reserva> reservas = new ArrayList<>();
        for (Document doc : collection.find(Filters.eq("habitacionId", habitacionId))) {
            reservas.add(documentToReserva(doc));
        }
        return reservas;
    }

    public List<Reserva> findByEstado(String estado) {
        List<Reserva> reservas = new ArrayList<>();
        for (Document doc : collection.find(Filters.eq("estado", estado))) {
            reservas.add(documentToReserva(doc));
        }
        return reservas;
    }

    public boolean updateEstado(String id, String nuevoEstado) {
        if (id == null || id.isEmpty()) {
            return false;
        }

        try {
            ObjectId objectId = new ObjectId(id);
            return collection.updateOne(
                    Filters.eq("_id", objectId),
                    Updates.set("estado", nuevoEstado)
            ).getModifiedCount() > 0;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean delete(String id) {
        if (id == null || id.isEmpty()) {
            return false;
        }

        try {
            ObjectId objectId = new ObjectId(id);
            return collection.deleteOne(Filters.eq("_id", objectId)).getDeletedCount() > 0;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private Reserva documentToReserva(Document doc) {
        if (doc == null) {
            return null;
        }

        Reserva reserva = new Reserva();
        reserva.setId(doc.getObjectId("_id"));
        reserva.setHabitacionId(doc.getInteger("habitacionId"));
        reserva.setUsuarioId(doc.getInteger("usuarioId"));
        reserva.setFechaCheckin(doc.getDate("fechaCheckin"));
        reserva.setFechaCheckout(doc.getDate("fechaCheckout"));
        reserva.setEstado(doc.getString("estado"));
        reserva.setTotal(doc.getDouble("total"));
        reserva.setFechaCreacion(doc.getDate("fechaCreacion"));

        return reserva;
    }
}