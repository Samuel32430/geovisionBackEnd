package pe.edu.upeu.geovision_backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.upeu.geovision_backend.exception.dto.ErrorResponseDTO;

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

        return buildResponse(request, HttpStatus.BAD_REQUEST, null, message, null);
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

        return buildResponse(request, HttpStatus.BAD_REQUEST, null, message,
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
            )
        );

        return buildResponse(request, HttpStatus.BAD_REQUEST, null, message, validationErrors);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<@NonNull ErrorResponseDTO> handleMethodValidation(
        HttpServletRequest request,
        HandlerMethodValidationException ex
    ) {
        if (ex.isForReturnValue()) {
            String message = "La respuesta generada no cumple las validaciones";
            return buildResponse(request, HttpStatus.INTERNAL_SERVER_ERROR, null, message, null);
        }

        String message = "Existen errores de validación";

        Map<String, String> validationErrors = new LinkedHashMap<>();
        ex.getParameterValidationResults().forEach(result -> {
            var parameter = result.getMethodParameter();
            String parameterName = parameter.getParameterName();
            String key = parameterName != null ? parameterName : "arg" + parameter.getParameterIndex();

            result.getResolvableErrors().forEach(error -> validationErrors.putIfAbsent(
                    error instanceof FieldError fieldError ? fieldError.getField() : key,
                    error.getDefaultMessage() != null ? error.getDefaultMessage() : "Valor inválido"
                )
            );
        }
        );

        return buildResponse(request, HttpStatus.BAD_REQUEST, null, message, validationErrors);
    }

    // MethodNotAllowed: 405
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<@NonNull ErrorResponseDTO> handleMethodNotSupported(
        HttpServletRequest request,
        HttpRequestMethodNotSupportedException ex
    ) {
        String message = "El método HTTP '" + ex.getMethod() + "' no está permitido para este recurso";

        return buildResponse(
                request, HttpStatus.METHOD_NOT_ALLOWED, ex.getHeaders(),
                message, null
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<@NonNull ErrorResponseDTO> handleResponseStatus(
        HttpServletRequest request,
        ResponseStatusException ex
    ) {
        String message = ex.getReason() != null ? ex.getReason() : "No se pudo completar la solicitud";

        return buildResponse(request, ex.getStatusCode(), null, message, null);
    }

    // NotFound: 404
    @ExceptionHandler(RecursosNoEncontradosException.class)
    public ResponseEntity<@NonNull ErrorResponseDTO> handleNotFound(
        HttpServletRequest request,
        RecursosNoEncontradosException ex
    ) {
        return buildResponse(request, HttpStatus.NOT_FOUND, null, ex.getMessage(), null);
    }

    // Conflict: 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<@NonNull ErrorResponseDTO> handleIntegrityConflict(
        HttpServletRequest request
    ) {
        log.warn("Conflicto de integridad en {}", request.getRequestURI());

        String message = "No se pudieron guardar los cambios por un conflicto de datos. "
                + "Actualiza la información e inténtalo nuevamente.";

        return buildResponse(request, HttpStatus.CONFLICT, null, message, null);
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<@NonNull ErrorResponseDTO> handleBusinessRule(
        HttpServletRequest request,
        ReglaNegocioException ex
    ) {
        return buildResponse(request, HttpStatus.CONFLICT, null, ex.getMessage(), null);
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

        return buildResponse(request, HttpStatus.UNAUTHORIZED, null, message, null);
    }

    // Forbidden: 403
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<@NonNull ErrorResponseDTO> handleAccessDenied(
        HttpServletRequest request
    ) {
        log.warn("Acceso denegado en {}", request.getRequestURI());

        String message = "No tiene permisos para realizar esta operación";

        return buildResponse(request, HttpStatus.FORBIDDEN, null, message, null);
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

        return buildResponse(request, HttpStatus.INTERNAL_SERVER_ERROR, null, ex.getMessage(), null);
    }

    // >>>> RESPONSE BUILDING
    public static ResponseEntity<@NonNull ErrorResponseDTO> buildResponse(
        HttpServletRequest request,
        HttpStatusCode status,
        HttpHeaders headers,
        String message,
        Map<String, String> validationErrors
    ) {
        return ResponseEntity
                .status(status)
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponseDTO(
                        LocalDateTime.now(),
                        status.value(),
                        HttpStatus.resolve(status.value()) != null
                                ? HttpStatus.valueOf(status.value()).getReasonPhrase()
                                : "HTTP " + status.value(),
                        message,
                        request.getRequestURI(),
                        validationErrors
                    )
                );
    }
}
