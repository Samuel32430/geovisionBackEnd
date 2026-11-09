package pe.edu.upeu.geovision_backend.controller.reports;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pe.edu.upeu.geovision_backend.dto.reports.ReporteRankingComercialDTO;
import pe.edu.upeu.geovision_backend.dto.reports.ReporteVentasPeriodoDTO;
import pe.edu.upeu.geovision_backend.exception.GlobalExceptionHandler;
import pe.edu.upeu.geovision_backend.service.reports.ReporteVentaService;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReporteControllerTest {

    // >>>> FIXTURES
    private MockMvc mockMvc;

    @Mock
    private ReporteVentaService reporteVentaService;

    @InjectMocks
    private ReporteController reporteController;

    // >>>> SETUP
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reporteController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // >>>> TESTS
    @Test
    @DisplayName("GET /api/v1/reportes/ventas/periodo: Retorna 200 OK con datos consolidados")
    void obtenerReporteVentasPeriodo_retorna200Ok() throws Exception {
        ReporteVentasPeriodoDTO dto = new ReporteVentasPeriodoDTO(
                Collections.emptyList(),
                new BigDecimal("1500.00"),
                new BigDecimal("1000.00"),
                new BigDecimal("500.00"),
                5L,
                new BigDecimal("300.00"),
                Collections.emptyMap(),
                Collections.emptyList()
        );

        when(reporteVentaService.obtenerReporteVentasPeriodo(any())).thenReturn(dto);

        mockMvc.perform(get("/api/v1/reportes/ventas/periodo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalVendido").value(1500.00))
                .andExpect(jsonPath("$.totalCobrado").value(1000.00))
                .andExpect(jsonPath("$.totalSaldoPendiente").value(500.00))
                .andExpect(jsonPath("$.totalVentas").value(5));
    }

    @Test
    @DisplayName("GET /api/v1/reportes/ventas/periodo/pdf: Retorna 200 OK con Content-Type application/pdf")
    void exportarReporteVentasPeriodoPdf_retornaPdf() throws Exception {
        byte[] pdfBytes = "%PDF-1.4 Mock PDF".getBytes();
        when(reporteVentaService.exportarReporteVentasPeriodoPdf(any())).thenReturn(pdfBytes);

        mockMvc.perform(get("/api/v1/reportes/ventas/periodo/pdf"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition", "inline; filename=\"reporte-ventas-periodo.pdf\""));
    }

    @Test
    @DisplayName("GET /api/v1/reportes/ventas/ranking-comercial: Retorna 200 OK con lista de items")
    void obtenerReporteRankingComercial_retorna200Ok() throws Exception {
        ReporteRankingComercialDTO dto = new ReporteRankingComercialDTO(
                Collections.emptyList(),
                12L,
                new BigDecimal("2400.00"),
                Collections.emptyList()
        );

        when(reporteVentaService.obtenerReporteRankingComercial(any())).thenReturn(dto);

        mockMvc.perform(get("/api/v1/reportes/ventas/ranking-comercial"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUnidadesVendidas").value(12))
                .andExpect(jsonPath("$.totalRecaudado").value(2400.00));
    }
}
