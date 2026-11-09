package pe.edu.upeu.geovision_backend.controller.gestion_mantenimiento;

import jakarta.validation.ConstraintViolationException;
import java.util.LinkedHashSet;

import lombok.RequiredArgsConstructor;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.Categoria;
import pe.edu.upeu.geovision_backend.entity.security.Empleado;
import pe.edu.upeu.geovision_backend.entity.orden_trabajo.EnvioLaboratorio;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.Laboratorio;
import pe.edu.upeu.geovision_backend.entity.atencion.Lente;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.MaterialCristal;
import pe.edu.upeu.geovision_backend.entity.orden_trabajo.OrdenTrabajo;
import pe.edu.upeu.geovision_backend.entity.atencion.Paciente;
import pe.edu.upeu.geovision_backend.entity.atencion.Persona;
import pe.edu.upeu.geovision_backend.entity.inventario.Producto;
import pe.edu.upeu.geovision_backend.entity.atencion.Receta;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.Servicio;
import pe.edu.upeu.geovision_backend.entity.ventas.ServicioVendido;
import pe.edu.upeu.geovision_backend.entity.ventas.Venta;
import pe.edu.upeu.geovision_backend.enums.orden_trabajo.EstadoOrdenTrabajoEnum;
import pe.edu.upeu.geovision_backend.enums.ventas.EstadoVentaEnum;
import pe.edu.upeu.geovision_backend.enums.ventas.TipoComprobanteEnum;
import pe.edu.upeu.geovision_backend.enums.atencion.TipoDocumentoEnum;
import pe.edu.upeu.geovision_backend.repository.gestion_mantenimiento.MantenimientoLock;
import pe.edu.upeu.geovision_backend.repository.gestion_mantenimiento.MantenimientoLock.Catalogo;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.CategoriaDTO;
import pe.edu.upeu.geovision_backend.service.gestion_mantenimiento.MantenimientoService;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@WithMockUser(authorities = "MANTENIMIENTO_GESTIONAR")
@RequiredArgsConstructor
class MantenimientoControllerTest {

    // >>>> FIXTURES
    static final String BASE = "/api/v1/mantenimiento/";
    record Caso(String ruta, String campoId, String cuerpo, int longitudNombre, boolean estado) { }

    // >>>> HELPERS
    static Stream<Caso> catalogos() {
        return Stream.of(
                new Caso("categorias", "id", "{\"estado\":1,\"nombre\":\"Catálogo prueba\",\"descripcion\":\" Descripción \"}", 100, true),
                new Caso("materiales-cristal", "id", "{\"nombre\":\"Catálogo prueba\"}", 100, false),
                new Caso("tratamientos-cristal", "id", "{\"nombre\":\"Catálogo prueba\"}", 100, false),
                new Caso("servicios", "id", "{\"estado\":1,\"nombre\":\"Catálogo prueba\",\"descripcion\":\" Descripción \",\"precio\":25.50}", 200, true),
                new Caso("laboratorios", "id", "{\"estado\":1,\"nombre\":\"Catálogo prueba\",\"ruc\":\"20123456789\",\"telefono\":\" 999123456 \",\"direccion\":\" Lima \",\"correo\":\"contacto@example.com\"}", 200, true)
        );
    }

    // >>>> FIXTURES
    private final MockMvc mvc;
    private final ObjectMapper json;
    private final EntityManager em;
    private final MantenimientoService service;
    // Oracle table locks are not verified by this H2 test.
    @MockitoBean MantenimientoLock lock;

    // >>>> SETUP
    @BeforeEach
    void limpiarMock() { reset(lock); }

    // >>>> HELPERS
    ObjectNode cuerpo(Caso caso) { return (ObjectNode) json.readTree(caso.cuerpo()); }

    JsonNode crear(Caso caso, ObjectNode body) throws Exception {
        return json.readTree(mvc.perform(post(BASE + caso.ruta()).contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(body))
            )
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString()
        );
    }

    // >>>> TESTS
    @ParameterizedTest
    @MethodSource("catalogos")
    void cicloCompletoMantieneIdentidadYCamposSoportados(Caso caso) throws Exception {
        ObjectNode request = cuerpo(caso).put("nombre", " Catálogo prueba ").put(caso.campoId(), 90000000);
        JsonNode creado = crear(caso, request);
        long id = creado.get(caso.campoId()).asLong();
        assertThat(id).isNotEqualTo(90000000);
        assertThat(creado.get("nombre").asText()).isEqualTo("Catálogo prueba");
        if (caso.estado()) assertThat(creado.get("estado").asInt()).isEqualTo(1);
        else assertThat(creado.has("estado")).isFalse();
        if (creado.has("descripcion")) assertThat(creado.get("descripcion").asText()).isEqualTo("Descripción");
        if (caso.ruta().equals("laboratorios")) {
            assertThat(creado.get("telefono").asText()).isEqualTo("999123456");
            assertThat(creado.get("direccion").asText()).isEqualTo("Lima");
        }
        mvc.perform(get(BASE + caso.ruta() + "/" + id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Catálogo prueba"));
        request.put("nombre", "Editado").put(caso.campoId(), 90000001);
        JsonNode editado = json.readTree(mvc.perform(put(BASE + caso.ruta() + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(request))
            )
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString()
        );
        assertThat(editado.get(caso.campoId()).asLong()).isEqualTo(id);
        assertThat(editado.get("nombre").asText()).isEqualTo("Editado");
        if (caso.ruta().equals("categorias")) {
            assertThat(editado.get("fechaRegistro")).isEqualTo(creado.get("fechaRegistro"));
        }
        mvc.perform(get(BASE + caso.ruta()).param("termino", "EDITADO").param("size", "1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].nombre").value("Editado"));
        mvc.perform(get(BASE + caso.ruta()).param("termino", "inexistente"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.empty").value(true));
        mvc.perform(delete(BASE + caso.ruta() + "/" + id)).andExpect(status().isMethodNotAllowed());
    }

    @ParameterizedTest
    @MethodSource("catalogos")
    void rechazaDuplicadosYRenombradosPeroPermiteMismoNombrePropio(Caso caso) throws Exception {
        JsonNode primero = crear(caso, cuerpo(caso));
        long id = primero.get(caso.campoId()).asLong();
        ObjectNode duplicado = cuerpo(caso).put("nombre", " CATÁLOGO PRUEBA ");
        mvc.perform(post(BASE + caso.ruta()).contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(duplicado))
        )
                .andExpect(status().isConflict()).andExpect(jsonPath("$.message").exists());
        ObjectNode otro = cuerpo(caso).put("nombre", "Otro registro");
        long otroId = crear(caso, otro).get(caso.campoId()).asLong();
        mvc.perform(put(BASE + caso.ruta() + "/" + otroId).contentType(MediaType.APPLICATION_JSON)
                .content(caso.cuerpo())
        ).andExpect(status().isConflict());
        mvc.perform(put(BASE + caso.ruta() + "/" + id).contentType(MediaType.APPLICATION_JSON)
                .content(caso.cuerpo())
        ).andExpect(status().isOk());
        mvc.perform(get(BASE + caso.ruta() + "/" + otroId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Otro registro"));
    }

    @ParameterizedTest
    @MethodSource("catalogos")
    void validaCamposYParametros(Caso caso) throws Exception {
        for (String nombre : new String[]{" ", "x".repeat(caso.longitudNombre() + 1)}) {
            ObjectNode invalid = cuerpo(caso).put("nombre", nombre);
            mvc.perform(post(BASE + caso.ruta()).contentType(MediaType.APPLICATION_JSON)
                    .content(json.writeValueAsString(invalid))
            ).andExpect(status().isBadRequest());
        }
        if (caso.estado()) {
            for (Integer estado : new Integer[]{null, -1, 2}) {
                ObjectNode invalid = cuerpo(caso);
                if (estado == null) invalid.putNull("estado"); else invalid.put("estado", estado);
                mvc.perform(post(BASE + caso.ruta()).contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(invalid))
                ).andExpect(status().isBadRequest());
            }
            mvc.perform(get(BASE + caso.ruta()).param("estado", "2")).andExpect(status().isBadRequest());
        }
        for (String[] param : new String[][]{{"page", "-1"}, {"size", "0"}, {"size", "101"},
                {"page", "2147483647"}, {"termino", "x".repeat(201)}}
        ) {
            mvc.perform(get(BASE + caso.ruta()).param(param[0], param[1])).andExpect(status().isBadRequest());
        }
        mvc.perform(get(BASE + caso.ruta() + "/0")).andExpect(status().isBadRequest());
        mvc.perform(get(BASE + caso.ruta() + "/90000000")).andExpect(status().isNotFound());
        mvc.perform(put(BASE + caso.ruta() + "/90000000").contentType(MediaType.APPLICATION_JSON)
                .content(caso.cuerpo())
        ).andExpect(status().isNotFound());
        mvc.perform(post(BASE + caso.ruta()).contentType(MediaType.APPLICATION_JSON)
                .content("{")
        ).andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("catalogos")
    void buscaLiteralesYEstadoConPaginacionEstable(Caso caso) throws Exception {
        crear(caso, cuerpo(caso).put("nombre", "Literal%_!"));
        crear(caso, cuerpo(caso).put("nombre", "LiteralABC"));
        mvc.perform(get(BASE + caso.ruta()).param("termino", "%_!")).andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
        mvc.perform(get(BASE + caso.ruta()).param("termino", "Literal").param("size", "1").param("page", "1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.number").value(1)).andExpect(jsonPath("$.content.length()").value(1));
        if (caso.estado()) {
            ObjectNode inactivo = cuerpo(caso).put("nombre", "LiteralInactivo").put("estado", 0);
            long id = crear(caso, inactivo).get(caso.campoId()).asLong();
            mvc.perform(get(BASE + caso.ruta()).param("termino", "Literal").param("estado", "0"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1));
            inactivo.put("estado", 1);
            mvc.perform(put(BASE + caso.ruta() + "/" + id).contentType(MediaType.APPLICATION_JSON)
                    .content(json.writeValueAsString(inactivo))
            ).andExpect(status().isOk())
                    .andExpect(jsonPath("$.estado").value(1));
        }
    }

    @Test
    void validaPrecioYContactoSinAlterarElDdl() throws Exception {
        for (String precio : new String[]{"-1", "0.001", "10000000000", "null"}) {
            mvc.perform(post(BASE + "servicios").contentType(MediaType.APPLICATION_JSON)
                    .content("{\"estado\":1,\"nombre\":\"Servicio\",\"precio\":" + precio + "}")
            ).andExpect(status().isBadRequest());
        }
        for (String body : new String[]{
                "{\"estado\":1,\"nombre\":\"Laboratorio\",\"correo\":\"invalido\"}",
                "{\"estado\":1,\"nombre\":\"Laboratorio\",\"telefono\":\"" + "x".repeat(51) + "\"}",
                "{\"estado\":1,\"nombre\":\"Laboratorio\",\"ruc\":\"" + "x".repeat(21) + "\"}"}
        ) {
            mvc.perform(post(BASE + "laboratorios").contentType(MediaType.APPLICATION_JSON)
                    .content(body)
            ).andExpect(status().isBadRequest());
        }
        mvc.perform(post(BASE + "servicios").contentType(MediaType.APPLICATION_JSON)
                .content("{\"estado\":1,\"nombre\":\"Servicio gratuito\",\"precio\":0}")
        ).andExpect(status().isCreated());
    }

    @Test
    void bloqueaCategoriaConProductoActivoYPermiteDependenciaInactiva() throws Exception {
        Categoria categoria = persistir(new Categoria(null, "Categoría dependiente", null, true, null));
        Producto producto = persistir(new Producto(null, "PRUEBA-CUS22", "Producto", null, null, BigDecimal.TEN, 0, true, null, categoria));
        String body = "{\"nombre\":\"Categoría dependiente\",\"estado\":0}";
        mvc.perform(put(BASE + "categorias/" + categoria.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andExpect(status().isConflict());
        assertThat(categoria.getActivo()).isEqualTo(true);
        producto.setActivo(false); em.flush();
        mvc.perform(put(BASE + "categorias/" + categoria.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andExpect(status().isOk()).andExpect(jsonPath("$.estado").value(0));
        verify(lock, times(2)).bloquear(Catalogo.CATEGORIA, true);
    }

    @Test
    void conservaPrecioHistoricoYBloqueaServicioConVentaPendiente() throws Exception {
        Servicio servicio = persistir(new Servicio(null, "Servicio vendido", null, BigDecimal.TEN, true));
        Venta venta = persistir(new Venta(null, null, BigDecimal.TEN, EstadoVentaEnum.PENDIENTE, null, TipoComprobanteEnum.BOLETA, empleado(), null, null));
        ServicioVendido detalle = persistir(new ServicioVendido(null, BigDecimal.TEN, venta, servicio));
        String body = "{\"nombre\":\"Servicio vendido\",\"precio\":20,\"estado\":0}";
        mvc.perform(put(BASE + "servicios/" + servicio.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andExpect(status().isConflict());
        venta.setEstado(EstadoVentaEnum.PAGADA); em.flush();
        mvc.perform(put(BASE + "servicios/" + servicio.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andExpect(status().isOk()).andExpect(jsonPath("$.precio").value(20));
        em.flush(); em.clear();
        assertThat(em.find(ServicioVendido.class, detalle.getId()).getPrecio()).isEqualByComparingTo("10");
    }

    @Test
    void bloqueaLaboratorioHastaRecepcionYRevisionSinCambiarEnvios() throws Exception {
        Laboratorio laboratorio = persistir(new Laboratorio(null, "Laboratorio con envío", null, null, null, null, true));
        Empleado empleado = empleado();
        Paciente paciente = persistir(new Paciente(null, persona(), true, null, null));
        Receta receta = persistir(new Receta(null, null, 0, paciente, empleado));
        MaterialCristal material = persistir(new MaterialCristal(null, "Material envío"));
        Lente lente = persistir(new Lente(null, BigDecimal.TEN, receta, material, null, new LinkedHashSet<>(), new LinkedHashSet<>()));
        OrdenTrabajo orden = persistir(new OrdenTrabajo(null, null, null, null, EstadoOrdenTrabajoEnum.ENVIADO_LABORATORIO, null, lente));
        EnvioLaboratorio envio = persistir(new EnvioLaboratorio(null, null, null, null, null, null, null, null, orden, laboratorio));
        String body = "{\"nombre\":\"Laboratorio con envío\",\"estado\":0}";
        mvc.perform(put(BASE + "laboratorios/" + laboratorio.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andExpect(status().isConflict());
        envio.setFechaRecepcion(LocalDateTime.now()); em.flush();
        mvc.perform(put(BASE + "laboratorios/" + laboratorio.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andExpect(status().isConflict());
        envio.setFechaRevision(LocalDateTime.now()); em.flush();
        mvc.perform(put(BASE + "laboratorios/" + laboratorio.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andExpect(status().isOk());
        assertThat(envio.getLaboratorio().getId()).isEqualTo(laboratorio.getId());
    }

    @Test
    void validacionDelServicioNoSeOmiteFueraDelController() {
        assertThatThrownBy(() -> service.registrarCategoria(new CategoriaDTO(null, " ", null, 1, null)))
                .isInstanceOf(ConstraintViolationException.class);
        verifyNoInteractions(lock);
    }

    @Test
    void bloqueoOcupadoRetornaConflictoSinExponerSql() throws Exception {
        doThrow(new CannotAcquireLockException("detalle interno SQL")).when(lock).bloquear(Catalogo.CATEGORIA, false);
        mvc.perform(post(BASE + "categorias").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Ocupada\",\"estado\":1}")
        )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("El catálogo está siendo utilizado. Intente nuevamente."));
    }

    @ParameterizedTest
    @MethodSource("catalogos")
    @WithMockUser(authorities = "VENTAS_LEER")
    void requierePermisoParaTodasLasOperaciones(Caso caso) throws Exception {
        mvc.perform(get(BASE + caso.ruta())).andExpect(status().isForbidden());
        mvc.perform(get(BASE + caso.ruta() + "/1")).andExpect(status().isForbidden());
        mvc.perform(post(BASE + caso.ruta()).contentType(MediaType.APPLICATION_JSON).content(caso.cuerpo())).andExpect(status().isForbidden());
        mvc.perform(put(BASE + caso.ruta() + "/1").contentType(MediaType.APPLICATION_JSON).content(caso.cuerpo())).andExpect(status().isForbidden());
        verifyNoInteractions(lock);
    }

    @ParameterizedTest
    @MethodSource("catalogos")
    @WithAnonymousUser
    void requiereAutenticacion(Caso caso) throws Exception {
        mvc.perform(get(BASE + caso.ruta())).andExpect(status().isUnauthorized());
        mvc.perform(get(BASE + caso.ruta() + "/1")).andExpect(status().isUnauthorized());
        mvc.perform(post(BASE + caso.ruta()).contentType(MediaType.APPLICATION_JSON).content(caso.cuerpo())).andExpect(status().isUnauthorized());
        mvc.perform(put(BASE + caso.ruta() + "/1").contentType(MediaType.APPLICATION_JSON).content(caso.cuerpo())).andExpect(status().isUnauthorized());
    }

    // >>>> HELPERS
    private <T> T persistir(T entity) { em.persist(entity); em.flush(); return entity; }
    private Persona persona() {
        return persistir(new Persona(null, "Persona", "Prueba", null, null, null, null, UUID.randomUUID().toString(), TipoDocumentoEnum.DNI));
    }
    private Empleado empleado() { return persistir(new Empleado(null, persona(), true, null, null)); }
}
