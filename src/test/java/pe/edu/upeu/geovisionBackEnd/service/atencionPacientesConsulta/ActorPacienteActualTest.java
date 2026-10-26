package pe.edu.upeu.geovisionBackEnd.service.atencionPacientesConsulta;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.*;
import pe.edu.upeu.geovisionBackEnd.repository.controlAccesos.*;
import pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.ActorPacienteActual;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ActorPacienteActualTest {
    final UsuarioRepository users = mock(UsuarioRepository.class);
    final EmpleadoRepository employees = mock(EmpleadoRepository.class);
    final UsuarioRolRepository roles = mock(UsuarioRolRepository.class);
    final RolPermisoRepository permissions = mock(RolPermisoRepository.class);
    final ActorPacienteActual actor = new ActorPacienteActual(users, employees, roles, permissions);

    @AfterEach void clear() { SecurityContextHolder.clearContext(); }

    void authenticate() {
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated("ana", "unused", List.of()));
    }

    void activeAccount() {
        var user = new Usuario();
        user.setIdUsuario(1L);
        var employee = new Empleado();
        employee.setIdPersona(2L);
        when(users.findByNombre("ana")).thenReturn(Optional.of(user));
        when(employees.findByUsuarioIdUsuario(1L)).thenReturn(Optional.of(employee));
    }

    @Test void anonymousIsRejected() {
        assertThrows(AuthenticationCredentialsNotFoundException.class, actor::lectura);
        verifyNoInteractions(users);
    }

    @Test void inactiveUserIsRejected() {
        authenticate();
        var user = new Usuario();
        user.setEstado(0);
        when(users.findByNombre("ana")).thenReturn(Optional.of(user));
        assertThrows(AccessDeniedException.class, actor::escritura);
        verifyNoInteractions(employees);
    }

    @Test void unconfiguredPermissionDeniesAccess() {
        authenticate();
        activeAccount();
        assertThrows(AccessDeniedException.class, actor::lectura);
    }

    @Test void configuredPermissionStillNeedsDatabaseGrant() {
        authenticate();
        activeAccount();
        ReflectionTestUtils.setField(actor, "permisoLectura", "PERMISO_EXISTENTE");
        when(roles.findByUsuarioIdUsuario(1L)).thenReturn(List.of());
        assertThrows(AccessDeniedException.class, actor::lectura);
    }

    @Test void grantedPermissionResolvesEmployeeAndUser() {
        authenticate();
        activeAccount();
        ReflectionTestUtils.setField(actor, "permisoEscritura", "PERMISO_EXISTENTE");
        var role = new Rol();
        role.setIdRol(4L);
        var link = new UsuarioRol();
        link.setRol(role);
        when(roles.findByUsuarioIdUsuario(1L)).thenReturn(List.of(link));
        when(permissions.findNombresPermisosByRolId(4L)).thenReturn(List.of("PERMISO_EXISTENTE"));
        assertEquals(new ActorPacienteActual.Actor(2L, 1L), actor.escritura());
    }
}

