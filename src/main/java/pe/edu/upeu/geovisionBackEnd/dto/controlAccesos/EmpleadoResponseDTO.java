package pe.edu.upeu.geovisionBackEnd.dto.controlAccesos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpleadoResponseDTO {

    private Long idPersona;
    private String nombres;
    private String apellidos;
    private Long idCargo;
    private String nombreCargo;
    private LocalDate fechaContrato;
    private Integer estado;
    private Long idUsuario;
    private String nombreUsuario;
}
