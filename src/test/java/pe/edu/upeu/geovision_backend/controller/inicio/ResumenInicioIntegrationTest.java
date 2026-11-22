package pe.edu.upeu.geovision_backend.controller.inicio;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashSet;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovision_backend.entity.atencion.Lente;
import pe.edu.upeu.geovision_backend.entity.atencion.Paciente;
import pe.edu.upeu.geovision_backend.entity.atencion.Persona;
import pe.edu.upeu.geovision_backend.entity.atencion.Receta;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.MaterialCristal;
import pe.edu.upeu.geovision_backend.entity.orden_trabajo.OrdenTrabajo;
import pe.edu.upeu.geovision_backend.entity.security.Empleado;
import pe.edu.upeu.geovision_backend.entity.security.Usuario;
import pe.edu.upeu.geovision_backend.entity.ventas.Venta;
import pe.edu.upeu.geovision_backend.enums.atencion.TipoDocumentoEnum;
import pe.edu.upeu.geovision_backend.enums.orden_trabajo.EstadoOrdenTrabajoEnum;
import pe.edu.upeu.geovision_backend.enums.ventas.EstadoVentaEnum;
import pe.edu.upeu.geovision_backend.enums.ventas.TipoComprobanteEnum;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:iniciotest;MODE=Oracle;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ResumenInicioIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired EntityManager em;

    private final LocalDateTime hoy = LocalDate.now(ZoneId.of("America/Lima")).atStartOfDay();
    private Empleado empleado;

    @BeforeEach
    void crearEmpleado() {
        var persona = persona("Operador");
        var usuario = guardar(new Usuario(null, "inicio-test", "fixture", "inicio@test.invalid", true, persona));
        empleado = guardar(new Empleado(null, persona, true, null, usuario));
    }

    @Test
    @WithMockUser(authorities = "VENTAS_LEER")
    void resumeTodasLasVentasVigentesDelDiaAunqueSeanMasDeCinco() throws Exception {
        for (int i = 0; i < 6; i++) {
            venta(hoy.plusMinutes(i), "10.50", EstadoVentaEnum.PAGADA);
        }
        venta(hoy.plusHours(1), "8.25", EstadoVentaEnum.PENDIENTE);
        venta(hoy.plusHours(2), "100.00", EstadoVentaEnum.ANULADA);
        venta(hoy.minusSeconds(1), "100.00", EstadoVentaEnum.PAGADA);
        venta(hoy.plusDays(1), "100.00", EstadoVentaEnum.PAGADA);

        mvc.perform(get("/api/v1/inicio/ventas")).andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidad").value(7))
                .andExpect(jsonPath("$.total").value(71.25));
        mvc.perform(get("/api/v1/inicio/ordenes")).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/inicio/atenciones")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ORDENES_TRABAJO_LEER")
    void cuentaLasOrdenesActivasPorEstadoSinIncluirLasCerradas() throws Exception {
        var receta = examen(paciente("Ordenes"), hoy, 0);
        var material = guardar(new MaterialCristal(null, "Material de prueba"));
        for (var estado : EstadoOrdenTrabajoEnum.values()) {
            var lente = guardar(new Lente(null, BigDecimal.TEN, receta, material, null,
                    new LinkedHashSet<>(), new LinkedHashSet<>()));
            guardar(new OrdenTrabajo(null, hoy.minusDays(1), null, null, estado, null, lente));
        }

        mvc.perform(get("/api/v1/inicio/ordenes")).andExpect(status().isOk())
                .andExpect(jsonPath("$.enProceso").value(5))
                .andExpect(jsonPath("$.listasParaEntrega").value(1));
    }

    @Test
    @WithMockUser(authorities = "PACIENTES_LEER")
    void distinguePacientesDeConsultasYExcluyeRecetasExternas() throws Exception {
        var recurrente = paciente("Recurrente");
        var nuevo = paciente("Nuevo");
        var externo = paciente("Externo");
        examen(recurrente, hoy.minusDays(1), 0);
        examen(nuevo, hoy.minusDays(1), 1);
        examen(nuevo, hoy, 0);
        examen(nuevo, hoy.plusHours(1), 0);
        examen(externo, hoy.plusHours(2), 1);
        var ultimo = examen(recurrente, hoy.plusHours(3), 0);
        examen(nuevo, hoy.plusDays(1), 0);

        mvc.perform(get("/api/v1/inicio/atenciones")).andExpect(status().isOk())
                .andExpect(jsonPath("$.pacientes").value(2))
                .andExpect(jsonPath("$.primerasVeces").value(1))
                .andExpect(jsonPath("$.totalConsultas").value(3))
                .andExpect(jsonPath("$.recientes.length()").value(3))
                .andExpect(jsonPath("$.recientes[0].idReceta").value(ultimo.getId()))
                .andExpect(jsonPath("$.recientes[0].paciente").value("Recurrente Sintetico"));
    }

    private <T> T guardar(T entidad) {
        em.persist(entidad);
        return entidad;
    }

    private Persona persona(String nombre) {
        return guardar(new Persona(null, nombre, "Sintetico", null, null, null, null,
                UUID.randomUUID().toString(), TipoDocumentoEnum.DNI));
    }

    private Paciente paciente(String nombre) {
        return guardar(new Paciente(null, persona(nombre), true, null, null));
    }

    private Receta examen(Paciente paciente, LocalDateTime fecha, int externo) {
        return guardar(new Receta(null, fecha, externo, paciente, empleado));
    }

    private void venta(LocalDateTime fecha, String total, EstadoVentaEnum estado) {
        guardar(new Venta(null, fecha, new BigDecimal(total), estado, null, TipoComprobanteEnum.BOLETA,
                empleado, null, null));
    }
}
