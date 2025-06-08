package com.gca.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Utilidad para generar JWT (JSON Web Tokens).
 * Proporciona métodos para crear un token a partir de los detalles del usuario
 * y para extraer la clave de firma del token.
 */
@Component
public class JwtUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    /**
     * Genera un token JWT para el usuario proporcionado.
     * @param userDetails Detalles del usuario para el cual se generará el token.
     * @return Un token JWT como cadena de texto.
     */
    public String generateToken(UserDetails userDetails) {
        LOGGER.debug("Generando token para usuario: {}", userDetails.getUsername());
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("roles", userDetails.getAuthorities())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Decodifica la clave para firmar el JWT.
     * @return La clave secreta como SecretKey.
     */
    public SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }


}
