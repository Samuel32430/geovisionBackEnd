package pe.edu.upeu.geovision_backend.controller.atencion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovision_backend.dto.atencion.LenteRequestDTO;
import pe.edu.upeu.geovision_backend.entity.security.Empleado;
import pe.edu.upeu.geovision_backend.entity.atencion.Lente;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.MaterialCristal;
import pe.edu.upeu.geovision_backend.entity.atencion.Medida;
import pe.edu.upeu.geovision_backend.entity.atencion.MedidaOjo;
import pe.edu.upeu.geovision_backend.entity.inventario.Montura;
import pe.edu.upeu.geovision_backend.entity.atencion.Paciente;
import pe.edu.upeu.geovision_backend.entity.atencion.Persona;
import pe.edu.upeu.geovision_backend.entity.inventario.Producto;
import pe.edu.upeu.geovision_backend.entity.atencion.Receta;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.TratamientoCristal;
import pe.edu.upeu.geovision_backend.entity.ventas.Venta;
import pe.edu.upeu.geovision_backend.enums.ventas.EstadoVentaEnum;
import pe.edu.upeu.geovision_backend.enums.inventario.MaterialMonturaEnum;
import pe.edu.upeu.geovision_backend.enums.inventario.ModeloMonturaEnum;
import pe.edu.upeu.geovision_backend.enums.ventas.TipoComprobanteEnum;
import pe.edu.upeu.geovision_backend.enums.atencion.TipoDocumentoEnum;
import pe.edu.upeu.geovision_backend.enums.atencion.TipoMedidaEnum;
import pe.edu.upeu.geovision_backend.repository.security.EmpleadoRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.LenteRepository;
import pe.edu.upeu.geovision_backend.repository.gestion_mantenimiento.MaterialCristalRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.MedidaOjoRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.MedidaRepository;
import pe.edu.upeu.geovision_backend.repository.inventario.MonturaRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.PacienteRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.PersonaRepository;
import pe.edu.upeu.geovision_backend.repository.inventario.ProductoRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.RecetaRepository;
import pe.edu.upeu.geovision_backend.repository.gestion_mantenimiento.TratamientoCristalRepository;
import pe.edu.upeu.geovision_backend.repository.ventas.VentaRepository;
import pe.edu.upeu.geovision_backend.service.atencion.RecetaService;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@WithMockUser(authorities = {"RECETAS_REGISTRAR", "PACIENTES_LEER"})
class ConfiguracionLenteControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired JdbcTemplate jdbc;
    @Autowired jakarta.persistence.EntityManager em;
    @Autowired RecetaService service;
    @Autowired RecetaRepository prescriptions;
    @Autowired PacienteRepository patients;
    @Autowired EmpleadoRepository employees;
    @Autowired PersonaRepository people;
    @Autowired MedidaRepository measures;
    @Autowired MedidaOjoRepository eyes;
    @Autowired MaterialCristalRepository materials;
    @Autowired TratamientoCristalRepository treatments;
    @Autowired MonturaRepository frames;
    @Autowired ProductoRepository products;
    @MockitoSpyBean LenteRepository lenses;
    @Autowired VentaRepository sales;
    Paciente patient;
    Empleado employee;
    Receta recipe;
    MaterialCristal material;
    TratamientoCristal treatment;
    Montura frame;
    List<Medida> clinical;

    Persona person(String name) {
        return people.saveAndFlush(new Persona(null, name, "Prueba", null, null, null, null, UUID.randomUUID().toString(), TipoDocumentoEnum.DNI));
    }

    Montura frame(int stock, Boolean state) {
        Producto product = products.saveAndFlush(new Producto(null, UUID.randomUUID().toString(), "Montura de prueba", null, null, new BigDecimal("120.50"), stock, state, null, null));
        return frames.saveAndFlush(new Montura(null, product, "Negro", ModeloMonturaEnum.ARO_COMPLETO, MaterialMonturaEnum.ACETATO));
    }

    Medida measure(Receta recipe, TipoMedidaEnum type) {
        MedidaOjo right = eyes.saveAndFlush(new MedidaOjo(null, "20/20", new BigDecimal("-1.25"), null, 90, null));
        MedidaOjo left = eyes.saveAndFlush(new MedidaOjo(null, "20/25", new BigDecimal("2.50"), null, 80, null));
        return measures.saveAndFlush(new Medida(null, type, new BigDecimal("62.50"), null, null, recipe, right, left, null));
    }

    @BeforeEach
    void fixture() {
        patient = patients.saveAndFlush(new Paciente(null, person("Paciente"), true, null, null));
        employee = employees.saveAndFlush(new Empleado(null, person("Profesional"), true, null, null));
        recipe = prescriptions.saveAndFlush(new Receta(null, null, 0, patient, employee));
        clinical = List.of(measure(recipe, TipoMedidaEnum.LEJOS), measure(recipe, TipoMedidaEnum.CERCA));
        material = materials.saveAndFlush(new MaterialCristal(null, "Resina " + UUID.randomUUID()));
        treatment = treatments.saveAndFlush(new TratamientoCristal(null, "Filtro " + UUID.randomUUID()));
        frame = frame(5, true);
    }

    LenteRequestDTO request() {
        return new LenteRequestDTO(recipe.getId(), material.getId(), null, new BigDecimal("380.00"), List.of(treatment.getId()), null);
    }

    void expectPost(Long recipeId, LenteRequestDTO request, int status) throws Exception {
        mvc.perform(post("/api/v1/recetas/{id}/lentes", recipeId).contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(request))).andExpect(status().is(status));
    }

    @Test
    void monturaPropiaPersisteNullYAsociaTodasLasMedidasSinCambiarDatosClinicos() throws Exception {
        long eyeCount = eyes.count();
        String response = mvc.perform(post("/api/v1/recetas/{id}/lentes", recipe.getId())
                        .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(request())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idReceta").value(recipe.getId()))
                .andExpect(jsonPath("$.idProductoMontura").doesNotExist())
                .andExpect(jsonPath("$.nombreMaterialCristal").value(material.getNombre()))
                .andExpect(jsonPath("$.subTotal").value(380.00))
                .andExpect(jsonPath("$.tratamientos[0].nombre").value(treatment.getNombre()))
                .andExpect(jsonPath("$.medidas.length()").value(2))
                .andExpect(jsonPath("$.medidas[0].ojoDerecho.agudezaVisual").value("20/20"))
                .andExpect(jsonPath("$.medidas[0].ojoIzquierdo.esfera").value(2.50))
                .andReturn().getResponse().getContentAsString();
        long id = json.readTree(response).get("id").asLong();
        assertThat(jdbc.queryForObject("select id_producto from LENTE where id_lente = ?", Long.class, id)).isNull();
        assertThat(lenses.findByIdWithDetails(id).orElseThrow().getMedidas()).hasSize(2);
        assertThat(lenses.findByIdWithDetails(id).orElseThrow().getTratamientosCristal()).hasSize(1);
        assertThat(eyes.count()).isEqualTo(eyeCount);
        assertThat(measures.findByRecetaId(recipe.getId())).hasSize(2);
        assertThat(eyes.findById(clinical.getFirst().getMedidaOjoDerecha().getId()).orElseThrow().getEsfera())
                .isEqualByComparingTo("-1.25");
    }

    @Test
    void monturaDeTiendaAsociaSubconjuntoDeMedidasYNoReservaStock() throws Exception {
        var request = request();
        request = new LenteRequestDTO(request.idReceta(), request.idMaterialCristal(), frame.getId(), request.subTotal(), request.tratamientosCristalIds(), request.medidasIds());
        request = new LenteRequestDTO(request.idReceta(), request.idMaterialCristal(), request.idProducto(), request.subTotal(), request.tratamientosCristalIds(), List.of(clinical.getLast().getId()));
        mvc.perform(post("/api/v1/recetas/{id}/lentes", recipe.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idProductoMontura").value(frame.getId()))
                .andExpect(jsonPath("$.codigoMontura").value(frame.getProducto().getCodigo()))
                .andExpect(jsonPath("$.nombreMontura").value("Montura de prueba"))
                .andExpect(jsonPath("$.medidas.length()").value(1))
                .andExpect(jsonPath("$.medidas[0].tipoMedida").value("CERCA"));
        assertThat(products.findById(frame.getId()).orElseThrow().getStock()).isEqualTo(5);
        // La receta admite otro lente antes de estar vinculada a una venta.
        request = new LenteRequestDTO(request.idReceta(), request.idMaterialCristal(), request.idProducto(), request.subTotal(), request.tratamientosCristalIds(), List.of());
        request = new LenteRequestDTO(request.idReceta(), request.idMaterialCristal(), request.idProducto(), request.subTotal(), null, request.medidasIds());
        expectPost(recipe.getId(), request, 201);
        assertThat(lenses.findByRecetaIdOrderByIdAsc(recipe.getId())).hasSize(2);
    }

    @Test
    void rechazaRecetaInconsistenteYReferenciasInexistentes() throws Exception {
        var request = request();
        expectPost(recipe.getId() + 99999, request, 409);
        request = new LenteRequestDTO(99999999L, request.idMaterialCristal(), request.idProducto(), request.subTotal(), request.tratamientosCristalIds(), request.medidasIds());
        expectPost(99999999L, request, 404);
        request = request();
        request = new LenteRequestDTO(request.idReceta(), 99999999L, request.idProducto(), request.subTotal(), request.tratamientosCristalIds(), request.medidasIds());
        expectPost(recipe.getId(), request, 404);
        request = request();
        request = new LenteRequestDTO(request.idReceta(), request.idMaterialCristal(), 99999999L, request.subTotal(), request.tratamientosCristalIds(), request.medidasIds());
        expectPost(recipe.getId(), request, 404);
        request = request();
        request = new LenteRequestDTO(request.idReceta(), request.idMaterialCristal(), request.idProducto(), request.subTotal(), List.of(99999999L), request.medidasIds());
        expectPost(recipe.getId(), request, 404);
        assertThat(lenses.findByRecetaIdOrderByIdAsc(recipe.getId())).isEmpty();
    }

    @Test
    void rechazaMonturaInactivaSinStockYProductoQueNoEsMontura() throws Exception {
        var request = request();
        request = new LenteRequestDTO(request.idReceta(), request.idMaterialCristal(), frame.getId(), request.subTotal(), request.tratamientosCristalIds(), request.medidasIds());
        frame.getProducto().setStock(0);
        products.saveAndFlush(frame.getProducto());
        expectPost(recipe.getId(), request, 409);
        frame.getProducto().setStock(5);
        frame.getProducto().setActivo(false);
        products.saveAndFlush(frame.getProducto());
        expectPost(recipe.getId(), request, 409);
        Producto other = products.saveAndFlush(new Producto(null, UUID.randomUUID().toString(), "Otro producto", null, null, BigDecimal.ONE, 5, true, null, null));
        request = new LenteRequestDTO(request.idReceta(), request.idMaterialCristal(), other.getId(), request.subTotal(), request.tratamientosCristalIds(), request.medidasIds());
        expectPost(recipe.getId(), request, 404);
    }

    @Test
    void rechazaMedidasAjenasInexistentesYDuplicados() throws Exception {
        Receta other = prescriptions.saveAndFlush(new Receta(null, null, 0, patient, employee));
        Medida foreign = measure(other, TipoMedidaEnum.LEJOS);
        var request = request();
        request = new LenteRequestDTO(request.idReceta(), request.idMaterialCristal(), request.idProducto(), request.subTotal(), request.tratamientosCristalIds(), List.of(clinical.getFirst().getId(), foreign.getId()));
        expectPost(recipe.getId(), request, 409);
        request = new LenteRequestDTO(request.idReceta(), request.idMaterialCristal(), request.idProducto(), request.subTotal(), request.tratamientosCristalIds(), List.of(99999999L));
        expectPost(recipe.getId(), request, 409);
        request = new LenteRequestDTO(request.idReceta(), request.idMaterialCristal(), request.idProducto(), request.subTotal(), request.tratamientosCristalIds(), List.of(clinical.getFirst().getId(), clinical.getFirst().getId()));
        expectPost(recipe.getId(), request, 409);
        request = new LenteRequestDTO(request.idReceta(), request.idMaterialCristal(), request.idProducto(), request.subTotal(), request.tratamientosCristalIds(), null);
        request = new LenteRequestDTO(request.idReceta(), request.idMaterialCristal(), request.idProducto(), request.subTotal(), List.of(treatment.getId(), treatment.getId()), request.medidasIds());
        expectPost(recipe.getId(), request, 409);
        assertThat(lenses.findByRecetaIdOrderByIdAsc(recipe.getId())).isEmpty();
    }

    @Test
    void recetaSinMedidasNoAdmiteLente() throws Exception {
        Receta empty = prescriptions.saveAndFlush(new Receta(null, null, 0, patient, employee));
        var request = request();
        request = new LenteRequestDTO(empty.getId(), request.idMaterialCristal(), request.idProducto(), request.subTotal(), request.tratamientosCristalIds(), request.medidasIds());
        expectPost(empty.getId(), request, 409);
    }

    @Test
    void medidaPreexistenteSinAmbosOjosNoAdmiteLente() throws Exception {
        clinical.getFirst().setMedidaOjoIzquierda(null);
        measures.saveAndFlush(clinical.getFirst());
        expectPost(recipe.getId(), request(), 409);
        assertThat(lenses.findByRecetaIdOrderByIdAsc(recipe.getId())).isEmpty();
    }

    @Test
    void ventaExistenteBloqueaAdicionesYConservaLentePrevio() throws Exception {
        var first = service.configurarLente(recipe.getId(), request());
        sales.saveAndFlush(new Venta(null, null, new BigDecimal("380.00"), EstadoVentaEnum.PENDIENTE, null, TipoComprobanteEnum.BOLETA, employee, patient, recipe));
        expectPost(recipe.getId(), request(), 409);
        assertThat(lenses.findByRecetaIdOrderByIdAsc(recipe.getId()))
                .extracting(Lente::getId).containsExactly(first.id());
        mvc.perform(put("/api/v1/recetas/{id}/lentes", recipe.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(request()))).andExpect(status().isMethodNotAllowed());
        mvc.perform(delete("/api/v1/recetas/{id}/lentes", recipe.getId()))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void validaSubtotalIdsYElementosDeListas() throws Exception {
        for (BigDecimal invalid : List.of(new BigDecimal("-1"), new BigDecimal("1.001"), new BigDecimal("10000000000"))) {
            var request = request();
            request = new LenteRequestDTO(request.idReceta(), request.idMaterialCristal(), request.idProducto(), invalid, request.tratamientosCristalIds(), request.medidasIds());
            expectPost(recipe.getId(), request, 400);
        }
        var request = request();
        request = new LenteRequestDTO(request.idReceta(), request.idMaterialCristal(), request.idProducto(), request.subTotal(), request.tratamientosCristalIds(), Arrays.asList((Long) null));
        expectPost(recipe.getId(), request, 400);
        request = request();
        request = new LenteRequestDTO(request.idReceta(), request.idMaterialCristal(), request.idProducto(), request.subTotal(), List.of(0L), request.medidasIds());
        expectPost(recipe.getId(), request, 400);
        request = request();
        request = new LenteRequestDTO(request.idReceta(), null, request.idProducto(), request.subTotal(), request.tratamientosCristalIds(), request.medidasIds());
        expectPost(recipe.getId(), request, 400);
        request = request();
        request = new LenteRequestDTO(null, request.idMaterialCristal(), request.idProducto(), request.subTotal(), request.tratamientosCristalIds(), request.medidasIds());
        expectPost(recipe.getId(), request, 400);
        request = request();
        request = new LenteRequestDTO(request.idReceta(), request.idMaterialCristal(), -1L, request.subTotal(), request.tratamientosCristalIds(), request.medidasIds());
        expectPost(recipe.getId(), request, 400);
        expectPost(-1L, request(), 400);
    }

    @Test
    void catalogosDevuelvenDatosUtilesYMonturasActivasConStockEnPaginasEstables() throws Exception {
        Montura second = frame(2, true);
        frame(0, true);
        frame(4, false);
        mvc.perform(get("/api/v1/catalogos-opticos/materiales-cristal"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].nombre").value(material.getNombre()));
        mvc.perform(get("/api/v1/catalogos-opticos/tratamientos-cristal"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].nombre").value(treatment.getNombre()));
        mvc.perform(get("/api/v1/catalogos-opticos/monturas").param("size", "1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].id").value(frame.getId()))
                .andExpect(jsonPath("$.content[0].codigo").value(frame.getProducto().getCodigo()))
                .andExpect(jsonPath("$.content[0].nombre").value("Montura de prueba"))
                .andExpect(jsonPath("$.content[0].precio").value(120.50))
                .andExpect(jsonPath("$.content[0].stock").value(5))
                .andExpect(jsonPath("$.content[0].modeloDescripcion").value("Aro Completo"));
        mvc.perform(get("/api/v1/catalogos-opticos/monturas").param("size", "1").param("page", "1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].id").value(second.getId()));
    }

    @Test
    void validaLimitesDePaginacionDeCatalogo() throws Exception {
        for (String[] invalid : new String[][]{{"page", "-1"}, {"size", "0"}, {"size", "101"}}) {
            mvc.perform(get("/api/v1/catalogos-opticos/monturas").param(invalid[0], invalid[1]))
                    .andExpect(status().isBadRequest());
        }
        mvc.perform(get("/api/v1/catalogos-opticos/monturas").param("page", "2147483647").param("size", "100"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "VENTAS_LEER")
    void exigePermisosDelModulo() throws Exception {
        expectPost(recipe.getId(), request(), 403);
        for (String catalog : List.of("materiales-cristal", "tratamientos-cristal", "monturas")) {
            mvc.perform(get("/api/v1/catalogos-opticos/" + catalog)).andExpect(status().isForbidden());
        }
    }

    @Test
    @WithMockUser(authorities = "EXAMENES_REGISTRAR")
    void permisoDeExamenPermiteCatalogosPeroNoConfigurarLente() throws Exception {
        mvc.perform(get("/api/v1/catalogos-opticos/materiales-cristal")).andExpect(status().isOk());
        expectPost(recipe.getId(), request(), 403);
    }

    @Test
    @WithAnonymousUser
    void exigeAutenticacion() throws Exception {
        expectPost(recipe.getId(), request(), 401);
        mvc.perform(get("/api/v1/catalogos-opticos/monturas")).andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void falloDeTratamientosRevierteLenteYVinculosDeMedidas() {
        long beforeLenses = lenses.count(), beforeLinks = contarVinculosMedida(), beforeTreatments = contarVinculosTratamiento();
        doAnswer(invocation -> {
            em.persist(invocation.getArgument(0, Lente.class));
            em.flush();
            assertThat(lenses.count()).isEqualTo(beforeLenses + 1);
            assertThat(contarVinculosMedida()).isEqualTo(beforeLinks + 2);
            assertThat(contarVinculosTratamiento()).isEqualTo(beforeTreatments + 1);
            throw new DataAccessResourceFailureException("Fallo simulado en tratamientos");
        }).when(lenses).saveAndFlush(any());
        try {
            assertThatThrownBy(() -> service.configurarLente(recipe.getId(), request()))
                    .isInstanceOf(DataAccessResourceFailureException.class);
            assertThat(lenses.count()).isEqualTo(beforeLenses);
            assertThat(contarVinculosMedida()).isEqualTo(beforeLinks);
            assertThat(contarVinculosTratamiento()).isEqualTo(beforeTreatments);
            assertThat(measures.findByRecetaId(recipe.getId())).hasSize(2);
            assertThat(products.findById(frame.getId()).orElseThrow().getStock()).isEqualTo(5);
        } finally {
            measures.deleteAllById(clinical.stream().map(Medida::getId).toList());
            eyes.deleteAllById(clinical.stream().flatMap(m -> List.of(m.getMedidaOjoDerecha().getId(),
                    m.getMedidaOjoIzquierda().getId()).stream()).toList());
            prescriptions.deleteById(recipe.getId());
            patients.deleteById(patient.getId());
            employees.deleteById(employee.getId());
            people.deleteAllById(List.of(patient.getId(), employee.getId()));
            frames.deleteById(frame.getId());
            products.deleteById(frame.getId());
            materials.deleteById(material.getId());
            treatments.deleteById(treatment.getId());
        }
    }

    private long contarVinculosTratamiento() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM LENTE_TRATAMIENTO_CRISTAL", Long.class);
    }

    private long contarVinculosMedida() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM LENTE_MEDIDA", Long.class);
    }
}
