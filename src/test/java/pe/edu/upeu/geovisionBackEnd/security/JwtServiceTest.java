package pe.edu.upeu.geovisionBackEnd.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    // 256-bit secret key in Base64
    private static final String SECRET_BASE64 = "w9Z6dP/dzykNH/uiI3Z/KGjBlP0H4Ji+Tuq4fm2cTNuLD4jWafu1gBL74V2SJEvy";
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET_BASE64, 60);
    }

    @Test
    @DisplayName("Debe generar token válido y extraer username correctamente")
    void generarYExtraerToken() {
        UserDetails user = User.withUsername("carlos.optometra")
                .password("hash")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_OPTOMETRA"), new SimpleGrantedAuthority("PACIENTES_LEER")))
                .build();

        String token = jwtService.generarToken(user);
        assertThat(token).isNotBlank();

        String username = jwtService.extraerUsername(token);
        assertThat(username).isEqualTo("carlos.optometra");
    }

    @Test
    @DisplayName("Debe generar y validar token de recuperación de contraseña")
    void generarYValidarTokenRecuperacion() {
        String email = "usuario@geovision.com";
        String tokenRecuperacion = jwtService.generarTokenRecuperacion(email, 15);

        assertThat(tokenRecuperacion).isNotBlank();
        assertThat(jwtService.validarTokenRecuperacion(tokenRecuperacion)).isTrue();
        assertThat(jwtService.extraerEmailRecuperacion(tokenRecuperacion)).isEqualTo(email);
    }

    @Test
    @DisplayName("Debe rechazar token normal como token de recuperación")
    void rechazarTokenNormalComoRecuperacion() {
        UserDetails user = User.withUsername("admin")
                .password("hash")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .build();

        String tokenAuth = jwtService.generarToken(user);

        assertThat(jwtService.validarTokenRecuperacion(tokenAuth)).isFalse();
        assertThatThrownBy(() -> jwtService.extraerEmailRecuperacion(tokenAuth))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
