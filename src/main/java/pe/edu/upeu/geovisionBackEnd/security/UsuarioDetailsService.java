package pe.edu.upeu.geovisionBackEnd.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servicio de carga de detalles del usuario para Spring Security.
 * En espera de la implementación de repositorios en la arquitectura por capas.
 */
@Service
public class UsuarioDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        throw new UsernameNotFoundException("Servicio en migración: " + usernameOrEmail);
    }
}
