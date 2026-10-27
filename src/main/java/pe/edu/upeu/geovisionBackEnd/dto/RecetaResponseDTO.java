package pe.edu.upeu.geovisionBackEnd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecetaResponseDTO {

    private Long idReceta;
    private LocalDateTime fechaRegistro;
    private Integer esExterno;
    private Boolean esRecetaExterna;
    private Long idPaciente;
    private String nombreCompletoPaciente;
    private String numeroDocumentoPaciente;
    private Long idEmpleado;
    private String nombreCompletoEmpleado;
    private List<MedidaResponseDTO> medidas;
    private List<LenteResponseDTO> lentes;

    public Boolean getEsRecetaExterna() {
        if (esRecetaExterna != null) {
            return esRecetaExterna;
        }
        return esExterno != null && esExterno == 1;
    }
}
