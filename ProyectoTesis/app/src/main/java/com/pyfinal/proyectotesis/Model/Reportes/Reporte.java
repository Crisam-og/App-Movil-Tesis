package com.pyfinal.proyectotesis.Model.Reportes;

import com.google.gson.annotations.SerializedName;

public class Reporte {
    @SerializedName("id")
    private int id;

    @SerializedName("cliente")
    private int clienteId;

    @SerializedName("distrito")
    private int distritoId;

    @SerializedName("imagen")
    private String imagenUrl;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("lat")
    private float lat;

    @SerializedName("long")
    private float lon;

    @SerializedName("direccion")
    private String direccion;


    public Reporte(int id, int clienteId, int distritoId, String imagenUrl, String createdAt, String descripcion, float lat, float lon, String direccion) {
        this.id = id;
        this.clienteId = clienteId;
        this.distritoId = distritoId;
        this.imagenUrl = imagenUrl;
        this.createdAt = createdAt;
        this.descripcion = descripcion;
        this.lat = lat;
        this.lon = lon;
        this.direccion = direccion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    public int getDistritoId() {
        return distritoId;
    }

    public void setDistritoId(int distritoId) {
        this.distritoId = distritoId;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
