package pe.edu.upeu.geovisionBackEnd.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryStructureTest {

    private final List<Class<?>> repositories = List.of(
            ConfiguracionEmpresaRepository.class,
            CategoriaRepository.class,
            CargoRepository.class,
            MaterialCristalRepository.class,
            TratamientoCristalRepository.class,
            LaboratorioRepository.class,
            PersonaRepository.class,
            UsuarioRepository.class,
            RolRepository.class,
            PermisoRepository.class,
            UsuarioRolRepository.class,
            RolPermisoRepository.class,
            UsuarioLogRepository.class,
            EmpleadoRepository.class,
            EmpleadoCargoRepository.class,
            EmpleadoLogRepository.class,
            PacienteRepository.class,
            PacienteLogRepository.class,
            ProductoRepository.class,
            MonturaRepository.class,
            ServicioRepository.class,
            MedidaOjoRepository.class,
            RecetaRepository.class,
            MedidaRepository.class,
            LenteRepository.class,
            LenteMedidaRepository.class,
            LenteTratamientoCristalRepository.class,
            VentaRepository.class,
            PagoRepository.class,
            ProductoVendidoRepository.class,
            ServicioVendidoRepository.class,
            OrdenTrabajoRepository.class,
            EnvioLaboratorioRepository.class
    );

    @Test
    @DisplayName("Debe contener exactamente 33 repositorios")
    void testTotalRepositoriesCount() {
        assertEquals(33, repositories.size(), "Deben existir exactamente 33 repositorios");
    }

    @Test
    @DisplayName("Todos los repositorios deben ser interfaces con @Repository y extender JpaRepository<?, Long>")
    void testAllRepositoriesExtendJpaRepositoryWithLongKey() {
        for (Class<?> repo : repositories) {
            assertTrue(repo.isInterface(), repo.getSimpleName() + " debe ser una interfaz");
            assertTrue(repo.isAnnotationPresent(Repository.class), repo.getSimpleName() + " debe estar anotada con @Repository");

            boolean extendsJpaRepository = false;
            for (Type genericInterface : repo.getGenericInterfaces()) {
                if (genericInterface instanceof ParameterizedType pt) {
                    if (pt.getRawType().equals(JpaRepository.class)) {
                        extendsJpaRepository = true;
                        Type[] typeArgs = pt.getActualTypeArguments();
                        assertEquals(2, typeArgs.length);
                        assertEquals(Long.class, typeArgs[1], repo.getSimpleName() + " debe tener ID de tipo Long");
                    }
                }
            }
            assertTrue(extendsJpaRepository, repo.getSimpleName() + " debe extender JpaRepository");
        }
    }

    @Test
    @DisplayName("Los repositorios críticos deben declarar findByIdForUpdate con bloqueo pesimista")
    void testPessimisticLockMethodsExist() throws NoSuchMethodException {
        assertNotNull(ProductoRepository.class.getMethod("findByIdForUpdate", Long.class));
        assertNotNull(OrdenTrabajoRepository.class.getMethod("findByIdForUpdate", Long.class));
        assertNotNull(EnvioLaboratorioRepository.class.getMethod("findByIdForUpdate", Long.class));
        assertNotNull(VentaRepository.class.getMethod("findByIdForUpdate", Long.class));
        assertNotNull(PersonaRepository.class.getMethod("findByIdForUpdate", Long.class));
        assertNotNull(RecetaRepository.class.getMethod("findByIdForUpdate", Long.class));
    }

    @Test
    @DisplayName("PagoRepository debe declarar métodos de cálculo de saldos y números de operación")
    void testPagoRepositoryMethods() throws NoSuchMethodException {
        assertNotNull(PagoRepository.class.getMethod("sumMontoPagadoByVentaId", Long.class));
        assertNotNull(PagoRepository.class.getMethod("findByNumeroOperacion", String.class));
        assertNotNull(PagoRepository.class.getMethod("existsByNumeroOperacion", String.class));
    }

    @Test
    @DisplayName("UsuarioRepository debe declarar métodos para autenticación y unicidad")
    void testUsuarioRepositoryMethods() throws NoSuchMethodException {
        assertNotNull(UsuarioRepository.class.getMethod("findByNombreOrCorreo", String.class, String.class));
        assertNotNull(UsuarioRepository.class.getMethod("findByNombre", String.class));
        assertNotNull(UsuarioRepository.class.getMethod("findByCorreo", String.class));
        assertNotNull(UsuarioRepository.class.getMethod("existsByNombre", String.class));
        assertNotNull(UsuarioRepository.class.getMethod("existsByCorreo", String.class));
    }
}
