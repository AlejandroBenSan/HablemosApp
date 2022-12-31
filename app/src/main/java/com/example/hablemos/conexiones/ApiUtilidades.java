package com.example.hablemos.conexiones;

import com.example.hablemos.modelosApoyo.Constantes;

//PRUEBA DE HACER UN POST EN LA API
public class ApiUtilidades
{
    private ApiUtilidades(){}

    public static ApiConexiones getApiConexion(){
        return RetrofitObject.getConexion().create(ApiConexiones.class);
    }
}
