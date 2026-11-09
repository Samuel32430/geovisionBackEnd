package pe.edu.upeu.geovision_backend.security.service;

import jakarta.annotation.PostConstruct;

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

@Service
@RequiredArgsConstructor
public class JwtService {

    // >>>> CONFIGURATION
    @Value("${app.jwt.secret}")
    private final String secretoBase64;
    @Value("${app.jwt.expiracion-minutos:480}")
    private final long expiracionMinutos;

    // >>>> QUERIES
    public String extraerUsername(String token) {
        Claims claims = leerClaims(token);
        if (claims.get("purpose") != null) {
            throw new JwtException("El token no es un token de acceso");
        }
        return claims.getSubject();
    }

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

    // >>>> PASSWORD RECOVERY
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

    public boolean validarTokenRecuperacion(String token) {
        try {
            Claims claims = leerClaims(token);
            return "PASSWORD_RESET".equals(claims.get("purpose", String.class));
        } catch (Exception e) {
            return false;
        }
    }

    public String extraerEmailRecuperacion(String token) {
        Claims claims = leerClaims(token);
        if (!"PASSWORD_RESET".equals(claims.get("purpose", String.class))) {
            throw new IllegalArgumentException("El token no es un token de recuperación válido");
        }
        return claims.getSubject();
    }

    // >>>> TOKEN CREATION
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

    // >>>> TOKEN VALIDATION
    @PostConstruct
    void validarClave() {
        clave();
    }

    // >>>> HELPERS
    private SecretKey clave() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretoBase64));
    }

}
