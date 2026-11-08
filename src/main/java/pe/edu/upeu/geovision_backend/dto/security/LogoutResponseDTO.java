package pe.edu.upeu.geovision_backend.dto.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogoutResponseDTO {

    private String mensaje;
    private Boolean exitoso;
    private LocalDateTime fecha;
}
