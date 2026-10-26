package pe.edu.upeu.geovisionBackEnd.dto.controlAccesos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuloResponseDTO {

    private Long idModulo;
    private String nombre;
}
