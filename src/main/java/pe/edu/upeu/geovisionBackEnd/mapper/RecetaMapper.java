package pe.edu.upeu.geovisionBackEnd.mapper;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovisionBackEnd.dto.LenteResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.MedidaOjoDTO;
import pe.edu.upeu.geovisionBackEnd.dto.MedidaRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.MedidaResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.RecetaResponseDTO;
import pe.edu.upeu.geovisionBackEnd.entity.Medida;
import pe.edu.upeu.geovisionBackEnd.entity.Lente;
import pe.edu.upeu.geovisionBackEnd.entity.MedidaOjo;
import pe.edu.upeu.geovisionBackEnd.entity.Persona;
import pe.edu.upeu.geovisionBackEnd.entity.Receta;
import pe.edu.upeu.geovisionBackEnd.entity.TratamientoCristal;

import java.util.List;

@Component
public class RecetaMapper {

    private final CatalogoOpticoMapper catalogoOpticoMapper;

    public RecetaMapper(CatalogoOpticoMapper catalogoOpticoMapper) {
        this.catalogoOpticoMapper = catalogoOpticoMapper;
    }

    public LenteResponseDTO toLenteResponse(Lente lente, List<Medida> medidas,
                                           List<TratamientoCristal> tratamientos) {
        return LenteResponseDTO.builder()
                .idLente(lente.getIdLente())
                .idReceta(lente.getReceta().getIdReceta())
                .subTotal(lente.getSubTotal())
                .idMaterialCristal(lente.getMaterialCristal().getIdMaterialCristal())
                .nombreMaterialCristal(lente.getMaterialCristal().getNombre())
                .idProductoMontura(lente.getMontura() == null ? null : lente.getMontura().getIdProducto())
                .codigoMontura(lente.getMontura() == null ? null : lente.getMontura().getProducto().getCodigo())
                .nombreMontura(lente.getMontura() == null ? null : lente.getMontura().getProducto().getNombre())
                .medidas(medidas.stream().map(this::toMedidaResponse).toList())
                .tratamientos(tratamientos.stream().map(catalogoOpticoMapper::toTratamientoCristalDTO).toList())
                .build();
    }

    public MedidaOjo toMedidaOjo(MedidaOjoDTO request) {
        return MedidaOjo.builder()
                .agudezaVisual(request.getAgudezaVisual())
                .esfera(request.getEsfera())
                .cilindro(request.getCilindro())
                .eje(request.getEje())
                .adicion(request.getAdicion())
                .build();
    }

    public Medida toMedida(MedidaRequestDTO request, Receta receta, MedidaOjo derecha, MedidaOjo izquierda) {
        return Medida.builder()
                .tipoMedida(request.getTipoMedida())
                .distanciaPupilar(request.getDistanciaPupilar())
                .prisma(request.getPrisma())
                .observaciones(request.getObservaciones())
                .receta(receta)
                .medidaOjoDerecha(derecha)
                .medidaOjoIzquierda(izquierda)
                .build();
    }

    public RecetaResponseDTO toRecetaResponse(Receta receta, List<Medida> medidas) {
        Persona paciente = receta.getPaciente().getPersona();
        Persona empleado = receta.getEmpleado().getPersona();
        return RecetaResponseDTO.builder()
                .idReceta(receta.getIdReceta())
                .fechaRegistro(receta.getFechaRegistro())
                .esExterno(receta.getEsExterno())
                .esRecetaExterna(receta.getEsExterno() == 1)
                .idPaciente(receta.getPaciente().getIdPersona())
                .nombreCompletoPaciente(nombreCompleto(paciente))
                .numeroDocumentoPaciente(paciente.getNumeroDocumento())
                .idEmpleado(receta.getEmpleado().getIdPersona())
                .nombreCompletoEmpleado(nombreCompleto(empleado))
                .medidas(medidas.stream().map(this::toMedidaResponse).toList())
                .lentes(List.of())
                .build();
    }

    public MedidaResponseDTO toMedidaResponse(Medida medida) {
        return MedidaResponseDTO.builder()
                .idMedida(medida.getIdMedida())
                .tipoMedida(medida.getTipoMedida())
                .distanciaPupilar(medida.getDistanciaPupilar())
                .prisma(medida.getPrisma())
                .observaciones(medida.getObservaciones())
                .idReceta(medida.getReceta().getIdReceta())
                .ojoDerecho(toMedidaOjoDTO(medida.getMedidaOjoDerecha()))
                .ojoIzquierdo(toMedidaOjoDTO(medida.getMedidaOjoIzquierda()))
                .idEnvioLaboratorio(medida.getEnvioLaboratorio() == null
                        ? null : medida.getEnvioLaboratorio().getIdEnvioLaboratorio())
                .build();
    }

    private MedidaOjoDTO toMedidaOjoDTO(MedidaOjo ojo) {
        return MedidaOjoDTO.builder()
                .idMedidaOjo(ojo.getIdMedidaOjo())
                .agudezaVisual(ojo.getAgudezaVisual())
                .esfera(ojo.getEsfera())
                .cilindro(ojo.getCilindro())
                .eje(ojo.getEje())
                .adicion(ojo.getAdicion())
                .build();
    }

    private String nombreCompleto(Persona persona) {
        return (persona.getNombres() + " " + persona.getApellidos()).trim();
    }
}
