package com.example.hablemos.modelosApoyo;

//CLASE PARA LEER EL JSON QUE DA ERROR EN LA BASE DE DATOS AL CREAR UNA NUEVA CUENTA
public class ApiError
{
    private Error ApiError;

    public Error getApiError() {
        return ApiError;
    }

    public void setApiError(Error error) {
        this.ApiError = ApiError;
    }
}
