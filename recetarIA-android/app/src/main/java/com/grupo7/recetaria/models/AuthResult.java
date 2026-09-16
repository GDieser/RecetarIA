package com.grupo7.recetaria.models;

public class AuthResult {

    private Status status;
    private String errorMessage;
    private boolean is_verificado;

    public enum Status {
        LOADING, SUCCESS, ERROR
    }
    // Constructor completo para uso interno
    public AuthResult(Status status, String error, boolean is_verificado){
        this.status = status;
        this.errorMessage = error;
        this.is_verificado = is_verificado;
    }

    public static AuthResult loading() {
        return new AuthResult(Status.LOADING, null, false);
    }

    public static AuthResult success(boolean is_verificado) {
        return new AuthResult(Status.SUCCESS, null, is_verificado);
    }

    public static AuthResult error(String message) {
        return new AuthResult(Status.ERROR, message, false);
    }

    // Getters y Helpers
    public boolean isVerificado() {return this.is_verificado;}
    public boolean isLoading() { return this.status == Status.LOADING; }
    public boolean isSuccess() { return this.status == Status.SUCCESS; }
    public boolean isError() { return this.status == Status.ERROR; }
    public Status getStatus() { return status; }
    public String getErrorMessage() { return errorMessage; }
}