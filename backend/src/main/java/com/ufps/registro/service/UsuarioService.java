package com.ufps.registro.service;

import com.ufps.registro.dto.RegistroDTO;
import com.ufps.registro.dto.RegistroResponse;
import com.ufps.registro.exception.EmailYaRegistradoException;
import com.ufps.registro.model.Usuario;
import com.ufps.registro.repository.UsuarioRepository;
import java.util.Locale;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public RegistroResponse registrar(RegistroDTO dto) {
        String email = dto.email().toLowerCase(Locale.ROOT);

        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailYaRegistradoException(email);
        }

        Usuario usuario = new Usuario(
                dto.nombre().trim(),
                email,
                passwordEncoder.encode(dto.password()));

        try {
            Usuario guardado = usuarioRepository.saveAndFlush(usuario);
            return new RegistroResponse(
                    guardado.getId(),
                    guardado.getNombre(),
                    guardado.getEmail(),
                    "Usuario registrado");
        } catch (DataIntegrityViolationException ex) {
            // Dos peticiones simultaneas con el mismo correo: la BD rechaza la segunda
            if (usuarioRepository.existsByEmail(email)) {
                throw new EmailYaRegistradoException(email);
            }
            throw ex;
        }
    }
}
