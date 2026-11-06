package pe.edu.upeu.geovisionBackEnd.service.service;

import pe.edu.upeu.geovisionBackEnd.dto.reporte.ventas.FiltroReporteVentasDTO;
import pe.edu.upeu.geovisionBackEnd.dto.reporte.ventas.ReporteRankingComercialDTO;
import pe.edu.upeu.geovisionBackEnd.dto.reporte.ventas.ReporteVentasPeriodoDTO;

public interface ReporteVentaService {

    /**
     * CUN 8 / CU27 (RF-101, RF-102): Genera el reporte estructurado de ventas por periodo,
     * calculando importes totales, pagos recibidos, saldos pendientes y desglose por método de pago.
     */
    ReporteVentasPeriodoDTO obtenerReporteVentasPeriodo(FiltroReporteVentasDTO filtro);

    /**
     * CUN 8 / RF-107: Exporta a PDF el reporte de ventas y recaudación por periodo.
     */
    byte[] exportarReporteVentasPeriodoPdf(FiltroReporteVentasDTO filtro);

    /**
     * Genera el reporte de rendimiento comercial con el ranking de productos físicos y
     * servicios más vendidos en el periodo.
     */
    ReporteRankingComercialDTO obtenerReporteRankingComercial(FiltroReporteVentasDTO filtro);

    /**
     * CUN 8 / RF-107: Exporta a PDF el ranking de productos y servicios más vendidos.
     */
    byte[] exportarReporteRankingComercialPdf(FiltroReporteVentasDTO filtro);
}
