package pe.edu.upeu.geovisionBackEnd.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import pe.edu.upeu.geovisionBackEnd.exception.dto.ErrorResponseDTO;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Utilidad para escribir respuestas de error de seguridad en formato JSON estándar (ErrorResponseDTO).
 */
public final class RespuestaErrorSeguridad {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private RespuestaErrorSeguridad() {
    }

    public static void escribir(
            HttpServletRequest request,
            HttpServletResponse response,
            int status,
            String error,
            String mensaje) throws IOException {

        ErrorResponseDTO dto = new ErrorResponseDTO(
                LocalDateTime.now(),
                status,
                error,
                mensaje,
                request.getRequestURI(),
                null
        );

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), dto);
    }
}
