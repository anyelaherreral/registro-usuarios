package com.ufps.registro.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.nio.charset.StandardCharsets;

public record RegistroDTO(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre admite maximo 100 caracteres")
        String nombre,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato valido")
        @Size(max = 150, message = "El email admite maximo 150 caracteres")
        String email,

        @NotBlank(message = "La contrasena es obligatoria")
        @Size(min = 8, message = "La contrasena debe tener minimo 8 caracteres")
        String password
) {

    /** BCrypt solo admite 72 bytes (una tilde o una ñ ocupan 2). */
    @AssertTrue(message = "La contrasena es demasiado larga (maximo 72 bytes)")
    public boolean isPasswordDentroDelLimite() {
        return password == null || password.getBytes(StandardCharsets.UTF_8).length <= 72;
    }

    /** Evita que la contrasena aparezca en los logs. */
    @Override
    public String toString() {
        return "RegistroDTO[nombre=" + nombre + ", email=" + email + ", password=****]";
    }
}
