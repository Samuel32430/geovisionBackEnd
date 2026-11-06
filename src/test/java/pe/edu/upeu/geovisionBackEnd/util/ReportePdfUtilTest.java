package pe.edu.upeu.geovisionBackEnd.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.edu.upeu.geovisionBackEnd.dto.reporte.KpiCardDTO;
import pe.edu.upeu.geovisionBackEnd.entity.ConfiguracionEmpresa;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReportePdfUtilTest {

    @Test
    @DisplayName("Generar PDF con membrete, tarjetas KPI y tabla retorna bytes de PDF válidos (%PDF-1.x)")
    void generarPdf_conDatosCompletos_retornaBytesValidos() throws IOException {
        ReportePdfUtil util = new ReportePdfUtil();

        ConfiguracionEmpresa empresa = ConfiguracionEmpresa.builder()
                .nombreLegal("Óptica Geo Visión S.A.C.")
                .nombreComercial("Geo Visión Ópticas")
                .ruc("20601234567")
                .direccion("Av. Los Pinos 123, Lima")
                .telefono("987654321")
                .build();

        util.dibujarEncabezado(empresa, "REPORTE DE PRUEBA UNITARIA", "Periodo: Noviembre 2026");

        List<KpiCardDTO> kpis = List.of(
                KpiCardDTO.builder().titulo("VENTAS").valor("S/ 1,500.00").subtitulo("10 operaciones").build(),
                KpiCardDTO.builder().titulo("COBRADO").valor("S/ 1,200.00").subtitulo("80%").build(),
                KpiCardDTO.builder().titulo("SALDO").valor("S/ 300.00").subtitulo("Pendiente").build()
        );
        util.dibujarTarjetasKpi(kpis);

        List<String> encabezados = List.of("Código", "Descripción", "Cantidad", "Total");
        float[] anchos = new float[]{80f, 250f, 80f, 105f};
        List<List<String>> filas = List.of(
                List.of("MONT-001", "Montura Ray-Ban Aviador Metal", "2", "S/ 700.00"),
                List.of("LENT-002", "Lunas Antirreflejo UV Blue", "1", "S/ 350.00"),
                List.of("SERV-001", "Examen de Refracción Computarizada", "3", "S/ 150.00")
        );
        util.dibujarTabla(encabezados, anchos, filas);

        byte[] pdfBytes = util.exportarPdf();

        assertThat(pdfBytes).isNotNull();
        assertThat(pdfBytes.length).isGreaterThan(100);

        // Los primeros 4 bytes de un archivo PDF válido siempre son %PDF
        String cabeceraPdf = new String(pdfBytes, 0, 4);
        assertThat(cabeceraPdf).isEqualTo("%PDF");
    }
}
