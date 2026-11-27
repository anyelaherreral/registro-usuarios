package com.ufps.registro.exception;

import com.ufps.registro.dto.ErrorResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 400: los datos no cumplen las validaciones. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> datosInvalidos(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errores.putIfAbsent(e.getField(), e.getDefaultMessage()));
        return construir(HttpStatus.BAD_REQUEST, "Datos invalidos", errores);
    }

    /** 400: JSON mal escrito o cuerpo vacio. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> jsonInvalido(HttpMessageNotReadableException ex) {
        return construir(HttpStatus.BAD_REQUEST, "El cuerpo de la solicitud no es un JSON valido", Map.of());
    }

    /** 409: el correo ya existe. */
    @ExceptionHandler(EmailYaRegistradoException.class)
    public ResponseEntity<ErrorResponse> emailDuplicado(EmailYaRegistradoException ex) {
        return construir(HttpStatus.CONFLICT, ex.getMessage(), Map.of());
    }

    private ResponseEntity<ErrorResponse> construir(HttpStatus status, String mensaje, Map<String, String> errores) {
        return ResponseEntity.status(status).body(new ErrorResponse(status.value(), mensaje, errores));
    }
}
