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
import pe.edu.upeu.geovision_backend.repository.atencion.LenteMedidaRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.LenteRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.LenteTratamientoCristalRepository;
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
    @Autowired LenteRepository lenses;
    @Autowired LenteMedidaRepository lensMeasures;
    @MockitoSpyBean LenteTratamientoCristalRepository lensTreatments;
    @Autowired VentaRepository sales;
    Paciente patient;
    Empleado employee;
    Receta recipe;
    MaterialCristal material;
    TratamientoCristal treatment;
    Montura frame;
    List<Medida> clinical;

    Persona person(String name) {
        return people.saveAndFlush(Persona.builder().nombres(name).apellidos("Prueba")
                .numeroDocumento(UUID.randomUUID().toString()).tipoDocumento(TipoDocumentoEnum.DNI).build());
    }

    Montura frame(int stock, Boolean state) {
        Producto product = products.saveAndFlush(Producto.builder().codigo(UUID.randomUUID().toString())
                .nombre("Montura de prueba").precio(new BigDecimal("120.50")).stock(stock).activo(state).build());
        return frames.saveAndFlush(Montura.builder().producto(product).color("Negro")
                .modelo(ModeloMonturaEnum.ARO_COMPLETO).material(MaterialMonturaEnum.ACETATO).build());
    }

    Medida measure(Receta recipe, TipoMedidaEnum type) {
        MedidaOjo right = eyes.saveAndFlush(MedidaOjo.builder().esfera(new BigDecimal("-1.25"))
                .agudezaVisual("20/20").eje(90).build());
        MedidaOjo left = eyes.saveAndFlush(MedidaOjo.builder().esfera(new BigDecimal("2.50"))
                .agudezaVisual("20/25").eje(80).build());
        return measures.saveAndFlush(Medida.builder().receta(recipe).tipoMedida(type)
                .distanciaPupilar(new BigDecimal("62.50")).medidaOjoDerecha(right).medidaOjoIzquierda(left).build());
    }

    @BeforeEach
    void fixture() {
        patient = patients.saveAndFlush(Paciente.builder().persona(person("Paciente")).build());
        employee = employees.saveAndFlush(Empleado.builder().persona(person("Profesional")).build());
        recipe = prescriptions.saveAndFlush(Receta.builder().paciente(patient).empleado(employee).build());
        clinical = List.of(measure(recipe, TipoMedidaEnum.LEJOS), measure(recipe, TipoMedidaEnum.CERCA));
        material = materials.saveAndFlush(MaterialCristal.builder().nombre("Resina " + UUID.randomUUID()).build());
        treatment = treatments.saveAndFlush(TratamientoCristal.builder().nombre("Filtro " + UUID.randomUUID()).build());
        frame = frame(5, true);
    }

    LenteRequestDTO request() {
        return LenteRequestDTO.builder().idReceta(recipe.getIdReceta()).idMaterialCristal(material.getIdMaterialCristal())
                .subTotal(new BigDecimal("380.00")).tratamientosCristalIds(List.of(treatment.getIdTratamientoCristal()))
                .build();
    }

    void expectPost(Long recipeId, LenteRequestDTO request, int status) throws Exception {
        mvc.perform(post("/api/v1/recetas/{id}/lentes", recipeId).contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(request))).andExpect(status().is(status));
    }

    @Test
    void monturaPropiaPersisteNullYAsociaTodasLasMedidasSinCambiarDatosClinicos() throws Exception {
        long eyeCount = eyes.count();
        String response = mvc.perform(post("/api/v1/recetas/{id}/lentes", recipe.getIdReceta())
                        .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(request())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idReceta").value(recipe.getIdReceta()))
                .andExpect(jsonPath("$.idProductoMontura").doesNotExist())
                .andExpect(jsonPath("$.nombreMaterialCristal").value(material.getNombre()))
                .andExpect(jsonPath("$.subTotal").value(380.00))
                .andExpect(jsonPath("$.tratamientos[0].nombre").value(treatment.getNombre()))
                .andExpect(jsonPath("$.medidas.length()").value(2))
                .andExpect(jsonPath("$.medidas[0].ojoDerecho.agudezaVisual").value("20/20"))
                .andExpect(jsonPath("$.medidas[0].ojoIzquierdo.esfera").value(2.50))
                .andReturn().getResponse().getContentAsString();
        long id = json.readTree(response).get("idLente").asLong();
        assertThat(jdbc.queryForObject("select id_producto from LENTE where id_lente = ?", Long.class, id)).isNull();
        assertThat(lensMeasures.findByLenteIdLente(id)).hasSize(2);
        assertThat(lensTreatments.findByLenteIdLente(id)).hasSize(1);
        assertThat(eyes.count()).isEqualTo(eyeCount);
        assertThat(measures.findByRecetaIdReceta(recipe.getIdReceta())).hasSize(2);
        assertThat(eyes.findById(clinical.getFirst().getMedidaOjoDerecha().getIdMedidaOjo()).orElseThrow().getEsfera())
                .isEqualByComparingTo("-1.25");
    }

    @Test
    void monturaDeTiendaAsociaSubconjuntoDeMedidasYNoReservaStock() throws Exception {
        var request = request();
        request = request.toBuilder().idProducto(frame.getIdProducto()).build();
        request = request.toBuilder().medidasIds(List.of(clinical.getLast().getIdMedida())).build();
        mvc.perform(post("/api/v1/recetas/{id}/lentes", recipe.getIdReceta()).contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idProductoMontura").value(frame.getIdProducto()))
                .andExpect(jsonPath("$.codigoMontura").value(frame.getProducto().getCodigo()))
                .andExpect(jsonPath("$.nombreMontura").value("Montura de prueba"))
                .andExpect(jsonPath("$.medidas.length()").value(1))
                .andExpect(jsonPath("$.medidas[0].tipoMedida").value("CERCA"));
        assertThat(products.findById(frame.getIdProducto()).orElseThrow().getStock()).isEqualTo(5);
        // La receta admite otro lente antes de estar vinculada a una venta.
        request = request.toBuilder().medidasIds(List.of()).build();
        request = request.toBuilder().tratamientosCristalIds(null).build();
        expectPost(recipe.getIdReceta(), request, 201);
        assertThat(lenses.findByRecetaIdRecetaOrderByIdLenteAsc(recipe.getIdReceta())).hasSize(2);
    }

    @Test
    void rechazaRecetaInconsistenteYReferenciasInexistentes() throws Exception {
        var request = request();
        expectPost(recipe.getIdReceta() + 99999, request, 409);
        request = request.toBuilder().idReceta(99999999L).build();
        expectPost(99999999L, request, 404);
        request = request();
        request = request.toBuilder().idMaterialCristal(99999999L).build();
        expectPost(recipe.getIdReceta(), request, 404);
        request = request();
        request = request.toBuilder().idProducto(99999999L).build();
        expectPost(recipe.getIdReceta(), request, 404);
        request = request();
        request = request.toBuilder().tratamientosCristalIds(List.of(99999999L)).build();
        expectPost(recipe.getIdReceta(), request, 404);
        assertThat(lenses.findByRecetaIdRecetaOrderByIdLenteAsc(recipe.getIdReceta())).isEmpty();
    }

    @Test
    void rechazaMonturaInactivaSinStockYProductoQueNoEsMontura() throws Exception {
        var request = request();
        request = request.toBuilder().idProducto(frame.getIdProducto()).build();
        frame.getProducto().setStock(0);
        products.saveAndFlush(frame.getProducto());
        expectPost(recipe.getIdReceta(), request, 409);
        frame.getProducto().setStock(5);
        frame.getProducto().setActivo(false);
        products.saveAndFlush(frame.getProducto());
        expectPost(recipe.getIdReceta(), request, 409);
        Producto other = products.saveAndFlush(Producto.builder().codigo(UUID.randomUUID().toString())
                .nombre("Otro producto").precio(BigDecimal.ONE).stock(5).build());
        request = request.toBuilder().idProducto(other.getIdProducto()).build();
        expectPost(recipe.getIdReceta(), request, 404);
    }

    @Test
    void rechazaMedidasAjenasInexistentesYDuplicados() throws Exception {
        Receta other = prescriptions.saveAndFlush(Receta.builder().paciente(patient).empleado(employee).build());
        Medida foreign = measure(other, TipoMedidaEnum.LEJOS);
        var request = request();
        request = request.toBuilder().medidasIds(List.of(clinical.getFirst().getIdMedida(), foreign.getIdMedida())).build();
        expectPost(recipe.getIdReceta(), request, 409);
        request = request.toBuilder().medidasIds(List.of(99999999L)).build();
        expectPost(recipe.getIdReceta(), request, 409);
        request = request.toBuilder().medidasIds(List.of(clinical.getFirst().getIdMedida(), clinical.getFirst().getIdMedida())).build();
        expectPost(recipe.getIdReceta(), request, 409);
        request = request.toBuilder().medidasIds(null).build();
        request = request.toBuilder().tratamientosCristalIds(List.of(treatment.getIdTratamientoCristal(), treatment.getIdTratamientoCristal())).build();
        expectPost(recipe.getIdReceta(), request, 409);
        assertThat(lenses.findByRecetaIdRecetaOrderByIdLenteAsc(recipe.getIdReceta())).isEmpty();
    }

    @Test
    void recetaSinMedidasNoAdmiteLente() throws Exception {
        Receta empty = prescriptions.saveAndFlush(Receta.builder().paciente(patient).empleado(employee).build());
        var request = request();
        request = request.toBuilder().idReceta(empty.getIdReceta()).build();
        expectPost(empty.getIdReceta(), request, 409);
    }

    @Test
    void medidaPreexistenteSinAmbosOjosNoAdmiteLente() throws Exception {
        clinical.getFirst().setMedidaOjoIzquierda(null);
        measures.saveAndFlush(clinical.getFirst());
        expectPost(recipe.getIdReceta(), request(), 409);
        assertThat(lenses.findByRecetaIdRecetaOrderByIdLenteAsc(recipe.getIdReceta())).isEmpty();
    }

    @Test
    void ventaExistenteBloqueaAdicionesYConservaLentePrevio() throws Exception {
        var first = service.configurarLente(recipe.getIdReceta(), request());
        sales.saveAndFlush(Venta.builder().receta(recipe).paciente(patient).empleado(employee)
                .total(new BigDecimal("380.00")).estado(EstadoVentaEnum.PENDIENTE)
                .tipoComprobante(TipoComprobanteEnum.BOLETA).build());
        expectPost(recipe.getIdReceta(), request(), 409);
        assertThat(lenses.findByRecetaIdRecetaOrderByIdLenteAsc(recipe.getIdReceta()))
                .extracting(Lente::getIdLente).containsExactly(first.idLente());
        mvc.perform(put("/api/v1/recetas/{id}/lentes", recipe.getIdReceta()).contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(request()))).andExpect(status().isMethodNotAllowed());
        mvc.perform(delete("/api/v1/recetas/{id}/lentes", recipe.getIdReceta()))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void validaSubtotalIdsYElementosDeListas() throws Exception {
        for (BigDecimal invalid : List.of(new BigDecimal("-1"), new BigDecimal("1.001"), new BigDecimal("10000000000"))) {
            var request = request();
            request = request.toBuilder().subTotal(invalid).build();
            expectPost(recipe.getIdReceta(), request, 400);
        }
        var request = request();
        request = request.toBuilder().medidasIds(Arrays.asList((Long) null)).build();
        expectPost(recipe.getIdReceta(), request, 400);
        request = request();
        request = request.toBuilder().tratamientosCristalIds(List.of(0L)).build();
        expectPost(recipe.getIdReceta(), request, 400);
        request = request();
        request = request.toBuilder().idMaterialCristal(null).build();
        expectPost(recipe.getIdReceta(), request, 400);
        request = request();
        request = request.toBuilder().idReceta(null).build();
        expectPost(recipe.getIdReceta(), request, 400);
        request = request();
        request = request.toBuilder().idProducto(-1L).build();
        expectPost(recipe.getIdReceta(), request, 400);
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
                .andExpect(jsonPath("$.content[0].idProducto").value(frame.getIdProducto()))
                .andExpect(jsonPath("$.content[0].codigo").value(frame.getProducto().getCodigo()))
                .andExpect(jsonPath("$.content[0].nombre").value("Montura de prueba"))
                .andExpect(jsonPath("$.content[0].precio").value(120.50))
                .andExpect(jsonPath("$.content[0].stock").value(5))
                .andExpect(jsonPath("$.content[0].modeloDescripcion").value("Aro Completo"));
        mvc.perform(get("/api/v1/catalogos-opticos/monturas").param("size", "1").param("page", "1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].idProducto").value(second.getIdProducto()));
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
        expectPost(recipe.getIdReceta(), request(), 403);
        for (String catalog : List.of("materiales-cristal", "tratamientos-cristal", "monturas")) {
            mvc.perform(get("/api/v1/catalogos-opticos/" + catalog)).andExpect(status().isForbidden());
        }
    }

    @Test
    @WithMockUser(authorities = "EXAMENES_REGISTRAR")
    void permisoDeExamenPermiteCatalogosPeroNoConfigurarLente() throws Exception {
        mvc.perform(get("/api/v1/catalogos-opticos/materiales-cristal")).andExpect(status().isOk());
        expectPost(recipe.getIdReceta(), request(), 403);
    }

    @Test
    @WithAnonymousUser
    void exigeAutenticacion() throws Exception {
        expectPost(recipe.getIdReceta(), request(), 401);
        mvc.perform(get("/api/v1/catalogos-opticos/monturas")).andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void falloDeTratamientosRevierteLenteYVinculosDeMedidas() {
        long beforeLenses = lenses.count(), beforeLinks = lensMeasures.count(), beforeTreatments = lensTreatments.count();
        doAnswer(invocation -> {
            assertThat(lenses.count()).isEqualTo(beforeLenses + 1);
            assertThat(lensMeasures.count()).isEqualTo(beforeLinks + 2);
            throw new DataAccessResourceFailureException("Fallo simulado en tratamientos");
        }).when(lensTreatments).saveAllAndFlush(any());
        try {
            assertThatThrownBy(() -> service.configurarLente(recipe.getIdReceta(), request()))
                    .isInstanceOf(DataAccessResourceFailureException.class);
            assertThat(lenses.count()).isEqualTo(beforeLenses);
            assertThat(lensMeasures.count()).isEqualTo(beforeLinks);
            assertThat(lensTreatments.count()).isEqualTo(beforeTreatments);
            assertThat(measures.findByRecetaIdReceta(recipe.getIdReceta())).hasSize(2);
            assertThat(products.findById(frame.getIdProducto()).orElseThrow().getStock()).isEqualTo(5);
        } finally {
            measures.deleteAllById(clinical.stream().map(Medida::getIdMedida).toList());
            eyes.deleteAllById(clinical.stream().flatMap(m -> List.of(m.getMedidaOjoDerecha().getIdMedidaOjo(),
                    m.getMedidaOjoIzquierda().getIdMedidaOjo()).stream()).toList());
            prescriptions.deleteById(recipe.getIdReceta());
            patients.deleteById(patient.getIdPersona());
            employees.deleteById(employee.getIdPersona());
            people.deleteAllById(List.of(patient.getIdPersona(), employee.getIdPersona()));
            frames.deleteById(frame.getIdProducto());
            products.deleteById(frame.getIdProducto());
            materials.deleteById(material.getIdMaterialCristal());
            treatments.deleteById(treatment.getIdTratamientoCristal());
        }
    }
}
