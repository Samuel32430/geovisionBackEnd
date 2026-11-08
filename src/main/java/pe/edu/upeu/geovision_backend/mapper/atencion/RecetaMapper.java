package pe.edu.upeu.geovision_backend.mapper.atencion;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovision_backend.dto.atencion.LenteResponseDTO;
import pe.edu.upeu.geovision_backend.dto.atencion.MedidaOjoDTO;
import pe.edu.upeu.geovision_backend.dto.atencion.MedidaRequestDTO;
import pe.edu.upeu.geovision_backend.dto.atencion.MedidaResponseDTO;
import pe.edu.upeu.geovision_backend.dto.atencion.RecetaResponseDTO;
import pe.edu.upeu.geovision_backend.entity.atencion.Medida;
import pe.edu.upeu.geovision_backend.entity.atencion.Lente;
import pe.edu.upeu.geovision_backend.entity.atencion.MedidaOjo;
import pe.edu.upeu.geovision_backend.entity.atencion.Persona;
import pe.edu.upeu.geovision_backend.entity.atencion.Receta;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.TratamientoCristal;

import java.util.List;

@Component
public class RecetaMapper {

    private final CatalogoOpticoMapper catalogoOpticoMapper;

    public RecetaMapper(CatalogoOpticoMapper catalogoOpticoMapper) {
        this.catalogoOpticoMapper = catalogoOpticoMapper;
    }

    public LenteResponseDTO toLenteResponse(Lente lente, List<Medida> medidas,
                                           List<TratamientoCristal> tratamientos) {
        return new LenteResponseDTO(lente.getId(), lente.getReceta().getId(), lente.getSubTotal(), lente.getMaterialCristal().getId(), lente.getMaterialCristal().getNombre(), lente.getMontura() == null ? null : lente.getMontura().getId(), lente.getMontura() == null ? null : lente.getMontura().getProducto().getCodigo(), lente.getMontura() == null ? null : lente.getMontura().getProducto().getNombre(), tratamientos.stream().map(catalogoOpticoMapper::toTratamientoCristalDTO).toList(), medidas.stream().map(this::toMedidaResponse).toList());
    }

    public MedidaOjo toMedidaOjo(MedidaOjoDTO request) {
        return new MedidaOjo(null, request.agudezaVisual(), request.esfera(), request.cilindro(), request.eje(), request.adicion());
    }

    public Medida toMedida(MedidaRequestDTO request, Receta receta, MedidaOjo derecha, MedidaOjo izquierda) {
        return new Medida(null, request.tipoMedida(), request.distanciaPupilar(), request.prisma(), request.observaciones(), receta, derecha, izquierda, null);
    }

    public RecetaResponseDTO toRecetaResponse(Receta receta, List<Medida> medidas) {
        return toRecetaResponse(receta, medidas, List.of());
    }

    public RecetaResponseDTO toRecetaResponse(Receta receta, List<Medida> medidas, List<LenteResponseDTO> lentes) {
        Persona paciente = receta.getPaciente().getPersona();
        Persona empleado = receta.getEmpleado().getPersona();
        return new RecetaResponseDTO(receta.getId(), receta.getFechaRegistro(), receta.getEsExterno(), receta.getEsExterno() == 1, receta.getPaciente().getId(), nombreCompleto(paciente), paciente.getNumeroDocumento(), receta.getEmpleado().getId(), nombreCompleto(empleado), medidas.stream().map(this::toMedidaResponse).toList(), lentes);
    }

    public MedidaResponseDTO toMedidaResponse(Medida medida) {
        return new MedidaResponseDTO(medida.getId(), medida.getTipoMedida(), medida.getDistanciaPupilar(), medida.getPrisma(), medida.getObservaciones(), medida.getReceta() == null ? null : medida.getReceta().getId(), toMedidaOjoDTO(medida.getMedidaOjoDerecha()), toMedidaOjoDTO(medida.getMedidaOjoIzquierda()), medida.getEnvioLaboratorio() == null
                        ? null : medida.getEnvioLaboratorio().getId());
    }

    private MedidaOjoDTO toMedidaOjoDTO(MedidaOjo ojo) {
        if (ojo == null) {
            return null;
        }
        return new MedidaOjoDTO(ojo.getId(), ojo.getAgudezaVisual(), ojo.getEsfera(), ojo.getCilindro(), ojo.getEje(), ojo.getAdicion());
    }

    private String nombreCompleto(Persona persona) {
        return (persona.getNombres() + " " + persona.getApellidos()).trim();
    }
}
