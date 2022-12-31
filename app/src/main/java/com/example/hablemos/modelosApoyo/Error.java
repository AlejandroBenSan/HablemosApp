package com.example.hablemos.modelosApoyo;

import com.google.gson.annotations.SerializedName;

//MODELO EN EL QUE SE GUARDA EL FORMATO JSON EXTRAIDO EN APIERROR
public class Error
{
    @SerializedName(value = "message")
    private String message;
    @SerializedName(value = "code")
    private String code;
    @SerializedName(value = "errno")
    private int errno;
    @SerializedName(value = "sql")
    private String sql;
    @SerializedName(value = "sqlState")
    private String sqlState;
    @SerializedName(value = "sqlMessage")
    private String sqlMessage;

    public Error(){

    }

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getSqlState() {
        return sqlState;
    }

    public void setSqlState(String sqlState) {
        this.sqlState = sqlState;
    }

    public String getSqlMessage() {
        return sqlMessage;
    }

    public void setSqlMessage(String sqlMessage) {
        this.sqlMessage = sqlMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "message='" + message + '\'' +
                ", code='" + code + '\'' +
                ", errno=" + errno +
                ", sql='" + sql + '\'' +
                ", sqlState='" + sqlState + '\'' +
                ", sqlMessage='" + sqlMessage + '\'' +
                '}';
    }
}
