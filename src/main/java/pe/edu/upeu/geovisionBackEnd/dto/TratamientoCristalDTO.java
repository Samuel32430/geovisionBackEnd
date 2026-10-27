package pe.edu.upeu.geovisionBackEnd.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TratamientoCristalDTO {

    private Long idTratamientoCristal;

    @NotBlank(message = "El nombre del tratamiento de cristal es obligatorio")
    @Size(max = 100, message = "El nombre del tratamiento de cristal no debe superar los 100 caracteres")
    private String nombre;
}
