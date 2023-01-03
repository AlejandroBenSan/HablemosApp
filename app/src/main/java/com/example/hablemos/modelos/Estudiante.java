package com.example.hablemos.modelos;

import android.graphics.Bitmap;
import android.media.Image;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.sql.Date;

public class Estudiante implements Serializable
{
    @SerializedName(value = "id")
    private int id;
    @SerializedName(value = "nombre")
    private String nombre;
    @SerializedName(value = "apellidos")
    private String apellidos;
    @SerializedName(value = "email")
    private String email;
    @SerializedName(value = "contrasenya")
    private String contrasenya;
    @SerializedName(value = "edad")
    private String edad;
    @SerializedName(value = "info")
    private String info;
    @SerializedName(value = "foto")
    private String foto;

    public Estudiante(String nombre, String apellidos, String email, String contrasenya, String edad, String info, String foto) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.contrasenya = contrasenya;
        this.edad = edad;
        this.info = info;
        this.foto = foto;
    }

    public Estudiante(){

    }

    public Estudiante(int id, String nombre, String apellidos, String email, String contrasenya, String edad, String info, String foto) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.contrasenya = contrasenya;
        this.edad = edad;
        this.info = info;
        this.foto = foto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasenya() {
        return contrasenya;
    }

    public void setContrasenya(String contrasenya) {
        this.contrasenya = contrasenya;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "Estudiante{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", email='" + email + '\'' +
                ", contrasenya='" + contrasenya + '\'' +
                ", edad=" + edad +
                ", info='" + info + '\'' +
                ", foto=" + foto +
                '}';
    }
}
