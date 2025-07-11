package com.gca.security.filter;

import com.gca.domain.User;
import com.gca.security.JwtUtil;
import com.gca.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Date;
import java.util.function.Function;

/*
 * Filtro para validar el JWT en cada petición HTTP.
 */
@Component
public class JWTFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JWTFilter.class);

    private final UserService userService;

    private final JwtUtil jwtUtil;

    public JWTFilter(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * Se ejecuta en cada petición HTTP para validar el JWT.
     * Si el token es válido, se establece la autenticación en el contexto de seguridad.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String token = extractBearerToken(request);
        LOGGER.debug("Filtro JWT");
        if (token != null && validateToken(token)) {
            String username = extractUserName(token);
            User user = userService.findByEmail(username);
            UsernamePasswordAuthenticationToken userPassAuthToken =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

            userPassAuthToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );
            // Establece la autenticación en el contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(userPassAuthToken);
        }
        filterChain.doFilter(request, response); // Continúa con la cadena de filtros
    }

    private String extractBearerToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        LOGGER.warn("No se ha encontrado el token en la cabecera de la petición");
        return null;
    }

    /**
     *
     * Comprueba si el token no está vacío, si no ha expirado y si es válido para el usuario.
     *
     * @param token El token JWT a validar.
     * @return true si el token es válido, false en caso contrario.
     */
    public Boolean validateToken(String token) {
        String userEmail = extractUserName(token);
        if (io.micrometer.common.util.StringUtils.isNotEmpty(userEmail) && isTokenExpired(token)) {
            UserDetails userDetails = this.userService.findByEmail(userEmail);
            return isTokenValid(token, userDetails);
        }
        LOGGER.debug("El token no es válido");
        return false;
    }

    public Boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        return (username.equals(userDetails.getUsername()) && isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return !extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwtUtil.getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
