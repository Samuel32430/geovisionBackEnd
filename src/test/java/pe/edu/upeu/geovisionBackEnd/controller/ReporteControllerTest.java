package pe.edu.upeu.geovisionBackEnd.controller;

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
import pe.edu.upeu.geovisionBackEnd.dto.reporte.ventas.ReporteRankingComercialDTO;
import pe.edu.upeu.geovisionBackEnd.dto.reporte.ventas.ReporteVentasPeriodoDTO;
import pe.edu.upeu.geovisionBackEnd.exception.GlobalExceptionHandler;
import pe.edu.upeu.geovisionBackEnd.service.service.ReporteVentaService;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReporteControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReporteVentaService reporteVentaService;

    @InjectMocks
    private ReporteController reporteController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reporteController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/reportes/ventas/periodo: Retorna 200 OK con datos consolidados")
    void obtenerReporteVentasPeriodo_retorna200Ok() throws Exception {
        ReporteVentasPeriodoDTO dto = ReporteVentasPeriodoDTO.builder()
                .totalVendido(new BigDecimal("1500.00"))
                .totalCobrado(new BigDecimal("1000.00"))
                .totalSaldoPendiente(new BigDecimal("500.00"))
                .totalVentas(5)
                .ticketPromedio(new BigDecimal("300.00"))
                .ventas(Collections.emptyList())
                .kpis(Collections.emptyList())
                .build();

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
        ReporteRankingComercialDTO dto = ReporteRankingComercialDTO.builder()
                .totalUnidadesVendidas(12L)
                .totalRecaudado(new BigDecimal("2400.00"))
                .items(Collections.emptyList())
                .kpis(Collections.emptyList())
                .build();

        when(reporteVentaService.obtenerReporteRankingComercial(any())).thenReturn(dto);

        mockMvc.perform(get("/api/v1/reportes/ventas/ranking-comercial"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUnidadesVendidas").value(12))
                .andExpect(jsonPath("$.totalRecaudado").value(2400.00));
    }
}
