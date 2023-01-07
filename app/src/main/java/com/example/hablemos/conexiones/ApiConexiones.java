package com.example.hablemos.conexiones;

import com.example.hablemos.modelos.Estudiante;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiConexiones {

    @GET("/estudiantes")
    Call <ArrayList<Estudiante>> getEstudiantes();

    //COMPROBAR LOGIN ESTUDIANTE
    @GET("/api/estudiantes/login/:email/:contrasenya")
    Call <String> comprobarLogin(@Query("email") String email, @Query("contrasenya") String contrasenya);

    //IBTENER ESTUDIANTE CON EMAIl
    @GET("/api/estudiantes/access/{email}")
    Call <Estudiante> obtenerEstudiante(@Path("email") String email);

    //CREAR ESTUDIANTE
    @POST("/api/estudiantes")
    Call<Estudiante> crearEstudiante(@Body Estudiante estudiante);

    //ACTUALIZAR ESTUDIANTE
    @PATCH("api/estudiantes/update/{id}")
    Call<Estudiante> actualizarEstudiante(@Body Estudiante estudiante, @Path("id") int id);
}
