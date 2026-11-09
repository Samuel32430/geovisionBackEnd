package pe.edu.upeu.geovision_backend.service.reports;

import pe.edu.upeu.geovision_backend.dto.reports.FiltroReporteVentasDTO;
import pe.edu.upeu.geovision_backend.dto.reports.ReporteRankingComercialDTO;
import pe.edu.upeu.geovision_backend.dto.reports.ReporteVentasPeriodoDTO;

public interface ReporteVentaService {

    // >>>> QUERIES
    ReporteVentasPeriodoDTO obtenerReporteVentasPeriodo(FiltroReporteVentasDTO filtro);

    byte[] exportarReporteVentasPeriodoPdf(FiltroReporteVentasDTO filtro);

    ReporteRankingComercialDTO obtenerReporteRankingComercial(FiltroReporteVentasDTO filtro);

    byte[] exportarReporteRankingComercialPdf(FiltroReporteVentasDTO filtro);

}
