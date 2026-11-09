package pe.edu.upeu.geovision_backend.dto;

import java.util.List;

public record PaginaResponseDTO<T>(List<T> content, int number, int size,
        long totalElements, int totalPages, boolean first, boolean last, boolean empty
) {
}
