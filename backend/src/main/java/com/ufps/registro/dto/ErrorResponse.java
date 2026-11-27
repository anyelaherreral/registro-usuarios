package com.ufps.registro.dto;

import java.util.Map;

public record ErrorResponse(int status, String mensaje, Map<String, String> errores) {
}
