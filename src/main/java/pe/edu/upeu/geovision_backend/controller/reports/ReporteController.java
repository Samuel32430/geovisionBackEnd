package pe.edu.upeu.geovision_backend.controller.reports;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.geovision_backend.dto.reports.FiltroReporteVentasDTO;
import pe.edu.upeu.geovision_backend.dto.reports.ReporteRankingComercialDTO;
import pe.edu.upeu.geovision_backend.dto.reports.ReporteVentasPeriodoDTO;
import pe.edu.upeu.geovision_backend.service.reports.ReporteVentaService;

@RestController
@RequestMapping("/api/v1/reportes/ventas")
@RequiredArgsConstructor
@Tag(name = "Reportes de Ventas", description = "Endpoints para generación de reportes analíticos de ventas y rendimiento comercial")
public class ReporteController {

    // >>>> CONSTANTS
    private static final Logger log = LoggerFactory.getLogger(ReporteController.class);

    // >>>> DEPENDENCIES
    private final ReporteVentaService reporteVentaService;

    // >>>> QUERIES
    @GetMapping("/periodo")
    @PreAuthorize("hasAnyAuthority('REPORTES_VER', 'VENTAS_LEER') or hasRole('ADMINISTRADOR')")
    @Operation(summary = "Reporte de ventas por periodo en formato JSON para dashboards y tablas")
    public ResponseEntity<ReporteVentasPeriodoDTO> obtenerReporteVentasPeriodo(
        @ModelAttribute FiltroReporteVentasDTO filtro
    ) {
        log.info("Petición REST recibida para reporte de ventas por periodo: {}", filtro);
        ReporteVentasPeriodoDTO response = reporteVentaService.obtenerReporteVentasPeriodo(filtro);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/periodo/pdf")
    @PreAuthorize("hasAnyAuthority('REPORTES_VER', 'VENTAS_LEER') or hasRole('ADMINISTRADOR')")
    @Operation(summary = "Descarga de reporte de ventas por periodo en formato PDF")
    public ResponseEntity<byte[]> exportarReporteVentasPeriodoPdf(
        @ModelAttribute FiltroReporteVentasDTO filtro
    ) {
        log.info("Petición REST recibida para exportar PDF de ventas por periodo: {}", filtro);
        byte[] pdfBytes = reporteVentaService.exportarReporteVentasPeriodoPdf(filtro);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"reporte-ventas-periodo.pdf\"")
                .body(pdfBytes);
    }

    @GetMapping("/ranking-comercial")
    @PreAuthorize("hasAnyAuthority('REPORTES_VER', 'VENTAS_LEER') or hasRole('ADMINISTRADOR')")
    @Operation(summary = "Reporte de ranking comercial de productos y servicios más vendidos en JSON")
    public ResponseEntity<ReporteRankingComercialDTO> obtenerReporteRankingComercial(
        @ModelAttribute FiltroReporteVentasDTO filtro
    ) {
        log.info("Petición REST recibida para ranking comercial: {}", filtro);
        ReporteRankingComercialDTO response = reporteVentaService.obtenerReporteRankingComercial(filtro);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ranking-comercial/pdf")
    @PreAuthorize("hasAnyAuthority('REPORTES_VER', 'VENTAS_LEER') or hasRole('ADMINISTRADOR')")
    @Operation(summary = "Descarga de ranking comercial en formato PDF")
    public ResponseEntity<byte[]> exportarReporteRankingComercialPdf(
        @ModelAttribute FiltroReporteVentasDTO filtro
    ) {
        log.info("Petición REST recibida para exportar PDF de ranking comercial: {}", filtro);
        byte[] pdfBytes = reporteVentaService.exportarReporteRankingComercialPdf(filtro);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"ranking-comercial.pdf\"")
                .body(pdfBytes);
    }

}
