package pe.edu.upeu.geovision_backend.security.service;

import lombok.RequiredArgsConstructor;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

/**
 * Servicio para la generación, firma y validación de tokens JWT.
 * El token se firma con algoritmo HMAC-SHA256/384 mediante una clave secreta Base64.
 */
@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${app.jwt.secret}")
    private final String secretoBase64;
    @Value("${app.jwt.expiracion-minutos:480}")
    private final long expiracionMinutos;

    @jakarta.annotation.PostConstruct
    void validarClave() {
        clave();
    }

    private SecretKey clave() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretoBase64));
    }

    /**
     * Genera un token JWT incluyendo subject (username) y lista de authorities (roles y permisos).
     */
    public String generarToken(UserDetails usuario) {
        Instant ahora = Instant.now();
        Instant expira = ahora.plus(expiracionMinutos, ChronoUnit.MINUTES);

        List<String> authorities = usuario.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(usuario.getUsername())
                .claim("authorities", authorities)
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(expira))
                .signWith(clave())
                .compact();
    }

    /**
     * Extrae el username de un token de acceso; los tokens con otro propósito no autentican peticiones.
     */
    public String extraerUsername(String token) {
        Claims claims = leerClaims(token);
        if (claims.get("purpose") != null) {
            throw new JwtException("El token no es un token de acceso");
        }
        return claims.getSubject();
    }

    /**
     * Lee y valida la firma y vigencia de las claims del token.
     */
    public Claims leerClaims(String token) {
        return Jwts.parser()
                .verifyWith(clave())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getExpiracionSegundos() {
        return expiracionMinutos * 60;
    }

    /**
     * Genera un token JWT temporal para recuperación de contraseña.
     */
    public String generarTokenRecuperacion(String email, long minutosExpiracion) {
        Instant ahora = Instant.now();
        Instant expira = ahora.plus(minutosExpiracion, ChronoUnit.MINUTES);

        return Jwts.builder()
                .subject(email)
                .claim("purpose", "PASSWORD_RESET")
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(expira))
                .signWith(clave())
                .compact();
    }

    /**
     * Valida que un token sea legítimo, no haya expirado y tenga propósito de recuperación.
     */
    public boolean validarTokenRecuperacion(String token) {
        try {
            Claims claims = leerClaims(token);
            return "PASSWORD_RESET".equals(claims.get("purpose", String.class));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extrae el email o identificador del usuario de un token de recuperación validado.
     */
    public String extraerEmailRecuperacion(String token) {
        Claims claims = leerClaims(token);
        if (!"PASSWORD_RESET".equals(claims.get("purpose", String.class))) {
            throw new IllegalArgumentException("El token no es un token de recuperación válido");
        }
        return claims.getSubject();
    }
}
