package pe.edu.upeu.geovision_backend.service.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.upeu.geovision_backend.dto.security.PermisoDTO;
import pe.edu.upeu.geovision_backend.dto.security.RolDTO;
import pe.edu.upeu.geovision_backend.entity.security.Permiso;
import pe.edu.upeu.geovision_backend.entity.security.Rol;
import pe.edu.upeu.geovision_backend.enums.security.ModuloEnum;
import pe.edu.upeu.geovision_backend.exception.RecursosNoEncontradosException;
import pe.edu.upeu.geovision_backend.mapper.security.PermisoMapper;
import pe.edu.upeu.geovision_backend.mapper.security.RolMapper;
import pe.edu.upeu.geovision_backend.repository.security.PermisoRepository;
import pe.edu.upeu.geovision_backend.repository.security.RolPermisoRepository;
import pe.edu.upeu.geovision_backend.repository.security.RolRepository;
import pe.edu.upeu.geovision_backend.service.security.impl.RolServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RolServiceTest {

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PermisoRepository permisoRepository;

    @Mock
    private RolPermisoRepository rolPermisoRepository;

    @Mock
    private RolMapper rolMapper;

    @Mock
    private PermisoMapper permisoMapper;

    @InjectMocks
    private RolServiceImpl rolService;

    private Rol testRol;
    private Permiso permisoVentas;
    private Permiso permisoPacientes;

    @BeforeEach
    void setUp() {
        testRol = Rol.builder()
                .idRol(1L)
                .nombre("VENDEDOR")
                .build();

        permisoVentas = Permiso.builder()
                .idPermiso(10L)
                .nombre("VENTAS_REGISTRAR")
                .modulo(ModuloEnum.VENTAS)
                .build();

        permisoPacientes = Permiso.builder()
                .idPermiso(20L)
                .nombre("PACIENTES_REGISTRAR")
                .modulo(ModuloEnum.ATENCION_PACIENTES)
                .build();
    }

    @Test
    @DisplayName("CUS18: Asignación de permisos a un rol exitosa, permitiendo permisos de múltiples módulos (RF-90)")
    void asignarPermisosExitosoCrossModulo() {
        Set<Long> permisoIds = Set.of(10L, 20L);

        when(rolRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(testRol));
        when(permisoRepository.findAllById(permisoIds)).thenReturn(List.of(permisoVentas, permisoPacientes));
        when(permisoMapper.toDTO(permisoVentas)).thenReturn(PermisoDTO.builder().idPermiso(10L).nombre("VENTAS_REGISTRAR").build());
        when(permisoMapper.toDTO(permisoPacientes)).thenReturn(PermisoDTO.builder().idPermiso(20L).nombre("PACIENTES_REGISTRAR").build());
        when(rolMapper.toDTO(eq(testRol), any())).thenReturn(RolDTO.builder().idRol(1L).nombre("VENDEDOR").build());

        RolDTO response = rolService.asignarPermisos(1L, permisoIds);

        assertThat(response).isNotNull();
        verify(rolPermisoRepository).deleteAll(any());
        verify(rolPermisoRepository).saveAllAndFlush(any());
    }

    @Test
    @DisplayName("CUS18: Asignación de permisos falla si un permiso no existe (404)")
    void asignarPermisosPermisoInexistente() {
        Set<Long> permisoIds = Set.of(10L, 999L);

        when(rolRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(testRol));
        when(permisoRepository.findAllById(permisoIds)).thenReturn(List.of(permisoVentas));

        assertThatThrownBy(() -> rolService.asignarPermisos(1L, permisoIds))
                .isInstanceOf(RecursosNoEncontradosException.class)
                .hasMessageContaining("Permisos no encontrados");

        verify(rolPermisoRepository, never()).deleteAll(any());
    }
}
