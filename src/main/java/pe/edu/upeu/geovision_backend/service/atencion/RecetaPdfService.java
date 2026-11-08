package pe.edu.upeu.geovision_backend.service.atencion;

public interface RecetaPdfService {

    byte[] generarRecetaPdf(Long idReceta);
}
