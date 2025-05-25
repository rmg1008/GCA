package com.gca.security.filter;

import com.gca.domain.Role;
import com.gca.domain.User;
import com.gca.security.JwtUtil;
import com.gca.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
class JWTFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private AuthenticationManager authManager;

    private String jwtToken;
    private final SecretKey key = Keys.hmacShaKeyFor("my-secret-key-my-secret-key-my-secret-key".getBytes());

    @BeforeEach
    void setUp() {
        User mockUser = new User();
        mockUser.setEmail("test@test.com");
        mockUser.setPassword("1234");
        Role role = new Role();
        role.setName("ROLE_USER");
        role.setId(1L);
        mockUser.setRoles(List.of(role));
        when(userService.findByEmail("test@test.com")).thenReturn(mockUser);
        when(jwtUtil.getSignInKey()).thenReturn(key);

        jwtToken = Jwts.builder()
                .setSubject("test@test.com")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        when(jwtUtil.generateToken(any())).thenReturn(jwtToken);
    }

    @Test
    void testRequestWithValidJWTToken() throws Exception {
        mockMvc.perform(get("/device/1")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGenerateToken() throws Exception {
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "test@test.com",
                                  "password": "1234"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }
}
