package com.example.hablemos.conexiones;

import com.example.hablemos.modelosApoyo.Constantes;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitObject {

    public static Retrofit getConexion(){
        return new Retrofit.Builder().
                //MODO DEV
                baseUrl(Constantes.DB_HOST_DEV).
                addConverterFactory(GsonConverterFactory.create()).
                build();
    }

}
