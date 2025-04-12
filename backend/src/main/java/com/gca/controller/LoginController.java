package com.gca.controller;

import com.gca.domain.User;
import com.gca.dto.LoginDTO;
import com.gca.security.JwtUtil;
import com.gca.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {

    @Resource
    UserService userService;

    @Resource
    JwtUtil jwtUtil;

    @Resource
    private AuthenticationManager authManager;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User usuario){
        userService.save(usuario);
        return ResponseEntity.ok("Usuario registrado");
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) throws Exception{
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
            );

        }catch (BadCredentialsException ex){
            throw new Exception("Error de email o contraseña " + ex.getMessage());
        }

        final User userDetails = userService.findByEmail(loginDTO.getEmail());
        final String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(token);
    }
}
