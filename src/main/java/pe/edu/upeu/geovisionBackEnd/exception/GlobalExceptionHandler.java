package pe.edu.upeu.geovisionBackEnd.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import pe.edu.upeu.geovisionBackEnd.exception.dto.ErrorResponseDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // >>>> ERRORS 4XX
    // BadRequest: 400
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<@NonNull ErrorResponseDTO> handleUnreadableBody(
            HttpServletRequest request
    ) {
        String message = "El cuerpo JSON tiene un formato inválido o contiene valores incompatibles";

        return buildResponse(request, HttpStatus.BAD_REQUEST, message, null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<@NonNull ErrorResponseDTO> handleInvalidParameter(
            HttpServletRequest request,
            MethodArgumentTypeMismatchException ex
    ) {
        String message = "El parámetro '" + ex.getName() + "' tiene un formato inválido";
        if (LocalDate.class.equals(ex.getRequiredType())) {
            message += ". Use una fecha ISO yyyy-MM-dd";
        }

        return buildResponse(request, HttpStatus.BAD_REQUEST, message,
                Map.of(ex.getName(), message)
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<@NonNull ErrorResponseDTO> handleValidation(
            HttpServletRequest request,
            MethodArgumentNotValidException ex
    ) {
        String message = "Existen errores de validación";

        Map<String, String> validationErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> validationErrors.put(
                e.getField(),
                e.getDefaultMessage()
        ));

        return buildResponse(request, HttpStatus.BAD_REQUEST, message, validationErrors);
    }

    // NotFound: 404
    @ExceptionHandler(RecursosNoEncontradosException.class)
    public ResponseEntity<@NonNull ErrorResponseDTO> handleNotFound(
            HttpServletRequest request,
            RecursosNoEncontradosException ex
    ) {
        return buildResponse(request, HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    // Conflict: 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<@NonNull ErrorResponseDTO> handleIntegrityConflict(
            HttpServletRequest request
    ) {
        log.warn("Conflicto de integridad en {}", request.getRequestURI());

        String message = "No se pudieron guardar los cambios por un conflicto de datos. "
                + "Actualiza la información e inténtalo nuevamente.";

        return buildResponse(request, HttpStatus.CONFLICT, message, null);
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<@NonNull ErrorResponseDTO> handleBusinessRule(
            HttpServletRequest request,
            ReglaNegocioException ex
    ) {
        return buildResponse(request, HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    // >>>> AUTHORIZATION ERRORS 401-403
    // Unauthorized: 401
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<@NonNull ErrorResponseDTO> handleAuthentication(
            HttpServletRequest request,
            AuthenticationException ex
    ) {
        log.warn(
                "Autenticación fallida en {} | motivo={}",
                request.getRequestURI(),
                ex.getClass().getSimpleName()
        );

        String message = "Usuario o contraseña incorrectos";

        return buildResponse(request, HttpStatus.UNAUTHORIZED, message, null);
    }

    // Forbidden: 403
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<@NonNull ErrorResponseDTO> handleAccessDenied(
            HttpServletRequest request
    ) {
        log.warn("Acceso denegado en {}", request.getRequestURI());

        String message = "No tiene permisos para realizar esta operación";

        return buildResponse(request, HttpStatus.FORBIDDEN, message, null);
    }

    // >>>> SERVER ERRORS 5XX
    // InternalServerError: 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<@NonNull ErrorResponseDTO> handleAnyException(
            HttpServletRequest request,
            Exception ex
    ) {
        log.warn("Error sin manejar en {}. {}: {}",
                request.getRequestURI(),
                ex.getClass().getSimpleName(),
                ex.getMessage()
        );

        return buildResponse(request, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), null);
    }

    // >>>> PRIVATE METHODS
    public static ResponseEntity<@NonNull ErrorResponseDTO> buildResponse(
            HttpServletRequest request,
            HttpStatus status,
            String message,
            Map<String, String> validationErrors
    ) {
        return ResponseEntity
                .status(status)
                .body(new ErrorResponseDTO(
                        LocalDateTime.now(),
                        status.value(),
                        status.getReasonPhrase(),
                        message,
                        request.getRequestURI(),
                        validationErrors
                ));
    }
}
