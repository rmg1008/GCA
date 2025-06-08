package com.gca.controller;

import com.gca.domain.User;
import com.gca.dto.LoginDTO;
import com.gca.dto.TokenDTO;
import com.gca.security.JwtUtil;
import com.gca.service.UserService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import javax.security.auth.login.LoginException;

/**
 * Rest Controller para gestionar el inicio de sesion y registro de usuarios.
 */
@RestController
public class LoginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Resource
    UserService userService;

    @Resource
    JwtUtil jwtUtil;

    @Resource
    private AuthenticationManager authManager;

    /**
     * Endpoint para registrar un nuevo usuario.
     *
     * @param usuario Objeto User que contiene la información del usuario a registrar.
     * @return Mensaje de éxito si el registro es correcto.
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User usuario){
        LOGGER.debug("Registrar usuario: {}", usuario);
        userService.save(usuario);
        return ResponseEntity.ok("Usuario registrado");
    }

    /**
     * Endpoint para iniciar sesión de un usuario.
     *
     * @param loginDTO Objeto LoginDTO que contiene el email y la contraseña del usuario.
     * @return TokenDTO con el token JWT si el inicio de sesión es exitoso.
     * @throws LoginException Si las credenciales son incorrectas.
     */
    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO loginDTO) throws LoginException{
        try {
            // Comprueba que las credenciales sean correctas
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
            );

        }catch (BadCredentialsException ex){
            throw new LoginException("Error de email o contraseña " + ex.getMessage());
        }

        final User userDetails = userService.findByEmail(loginDTO.getEmail());
        final String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new TokenDTO(token));
    }
}
