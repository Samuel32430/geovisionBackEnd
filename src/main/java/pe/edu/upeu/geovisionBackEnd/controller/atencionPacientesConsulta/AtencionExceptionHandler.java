package pe.edu.upeu.geovisionBackEnd.controller.atencionPacientesConsulta;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pe.edu.upeu.geovisionBackEnd.exception.dto.ErrorResponseDTO;
import java.time.LocalDateTime;
import java.util.Map;

/** Module-local handling; never return SQL or clinical request values. */
@Order(-1)
@RestControllerAdvice(basePackages = "pe.edu.upeu.geovisionBackEnd.controller.atencionPacientesConsulta")
public class AtencionExceptionHandler {
    @ExceptionHandler({HttpMessageNotReadableException.class, MissingServletRequestParameterException.class,
            HandlerMethodValidationException.class})
    public ResponseEntity<ErrorResponseDTO> solicitudInvalida(Exception ex, HttpServletRequest request) {
        return ResponseEntity.badRequest().body(new ErrorResponseDTO(LocalDateTime.now(), 400, "Bad Request",
                "El cuerpo o los parametros de la solicitud son invalidos", request.getRequestURI(), null));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDTO> metodoNoPermitido(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        return ResponseEntity.status(405).header("Allow", String.join(", ", ex.getSupportedMethods()))
                .body(new ErrorResponseDTO(LocalDateTime.now(), 405, "Method Not Allowed",
                        "Metodo no permitido", request.getRequestURI(), null));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponseDTO> tipoNoSoportado(
            HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        return ResponseEntity.status(415).body(new ErrorResponseDTO(LocalDateTime.now(), 415,
                "Unsupported Media Type", "Se requiere un contenido JSON valido", request.getRequestURI(), null));
    }

    @ExceptionHandler(PessimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponseDTO> bloqueo(PessimisticLockingFailureException ex, HttpServletRequest request) {
        return ResponseEntity.status(409).body(new ErrorResponseDTO(LocalDateTime.now(), 409, "Conflict",
                "El registro esta siendo modificado; vuelva a intentar", request.getRequestURI(), null));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> conflicto(DataIntegrityViolationException ex, HttpServletRequest request) {
        return ResponseEntity.status(409).body(new ErrorResponseDTO(LocalDateTime.now(), 409, "Conflict",
                "La operacion entra en conflicto con los datos registrados", request.getRequestURI(), null));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> validacion(ConstraintViolationException ex, HttpServletRequest request) {
        var errores = new java.util.LinkedHashMap<String, String>();
        ex.getConstraintViolations().forEach(v -> errores.put(v.getPropertyPath().toString(), v.getMessage()));
        return ResponseEntity.badRequest().body(new ErrorResponseDTO(LocalDateTime.now(), 400, "Bad Request",
                "Existen errores de validacion", request.getRequestURI(), errores));
    }
}

