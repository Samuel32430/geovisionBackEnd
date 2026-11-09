package pe.edu.upeu.geovision_backend.util.reports;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.edu.upeu.geovision_backend.dto.reports.KpiCardDTO;
import pe.edu.upeu.geovision_backend.entity.gestion_configuracion.ConfiguracionEmpresa;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReportePdfUtilTest {

    @Test
    @DisplayName("Generar PDF con membrete, tarjetas KPI y tabla retorna bytes de PDF válidos (%PDF-1.x)")
    void generarPdf_conDatosCompletos_retornaBytesValidos() throws IOException {
        ReportePdfUtil util = new ReportePdfUtil();

        ConfiguracionEmpresa empresa = new ConfiguracionEmpresa();
        empresa.setNombreLegal("Óptica Geo Visión S.A.C.");
        empresa.setNombreComercial("Geo Visión Ópticas");
        empresa.setRuc("20601234567");
        empresa.setDireccion("Av. Los Pinos 123, Lima");
        empresa.setTelefono("987654321");

        util.dibujarEncabezado(empresa, "REPORTE DE PRUEBA UNITARIA", "Periodo: Noviembre 2026");

        List<KpiCardDTO> kpis = List.of(
                new KpiCardDTO("VENTAS", "S/ 1,500.00", "10 operaciones"),
                new KpiCardDTO("COBRADO", "S/ 1,200.00", "80%"),
                new KpiCardDTO("SALDO", "S/ 300.00", "Pendiente")
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

        String cabeceraPdf = new String(pdfBytes, 0, 4);
        assertThat(cabeceraPdf).isEqualTo("%PDF");
    }
}
