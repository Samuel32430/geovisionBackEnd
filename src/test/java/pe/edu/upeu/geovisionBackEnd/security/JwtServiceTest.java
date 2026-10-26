package pe.edu.upeu.geovisionBackEnd.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias - JwtService")
class JwtServiceTest {

    // Clave HMAC Base64 válida (48 bytes / 384 bits)
    private static final String SECRETO_TEST = "w9Z6dP/dzykNH/uiI3Z/KGjBlP0H4Ji+Tuq4fm2cTNuLD4jWafu1gBL74V2SJEvy";
    private static final long EXPIRACION_MINUTOS = 60;

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRETO_TEST, EXPIRACION_MINUTOS);
    }

    @Test
    @DisplayName("Debe generar un token JWT válido con username y authorities")
    void generarToken_DebeGenerarTokenValido() {
        UserDetails user = User.withUsername("cajero01")
                .password("password123")
                .authorities(List.of(
                        new SimpleGrantedAuthority("ROLE_VENDEDOR"),
                        new SimpleGrantedAuthority("VENTAS_REGISTRAR")
                ))
                .build();

        String token = jwtService.generarToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertTrue(token.split("\\.").length == 3, "El token debe tener 3 partes (header.payload.signature)");

        String usernameExtraido = jwtService.extraerUsername(token);
        assertEquals("cajero01", usernameExtraido);

        Claims claims = jwtService.leerClaims(token);
        assertEquals("cajero01", claims.getSubject());
        assertNotNull(claims.get("authorities"));

        @SuppressWarnings("unchecked")
        List<String> authorities = (List<String>) claims.get("authorities");
        assertTrue(authorities.contains("ROLE_VENDEDOR"));
        assertTrue(authorities.contains("VENTAS_REGISTRAR"));
    }

    @Test
    @DisplayName("Debe retornar los segundos de expiración configurados")
    void getExpiracionSegundos_DebeCalcularSegundosCorrectos() {
        assertEquals(3600L, jwtService.getExpiracionSegundos());
    }
}
