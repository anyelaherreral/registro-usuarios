package com.ufps.registro.exception;

public class EmailYaRegistradoException extends RuntimeException {

    public EmailYaRegistradoException(String email) {
        super("El correo " + email + " ya esta registrado");
    }
}
