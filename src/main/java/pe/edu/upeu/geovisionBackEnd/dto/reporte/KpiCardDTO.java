package pe.edu.upeu.geovisionBackEnd.dto.reporte;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa una tarjeta de métrica destacada (KPI) para la cabecera
 * ejecutiva de los reportes en pantalla y documentos PDF.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KpiCardDTO {
    private String titulo;
    private String valor;
    private String subtitulo;
}
