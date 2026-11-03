package pe.edu.upeu.geovisionBackEnd.service.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pe.edu.upeu.geovisionBackEnd.dto.*;

public interface MantenimientoService {
    Page<CategoriaDTO> listarCategoria(@Size(max = 200) String termino, @Min(0) @Max(1) Integer estado, @NotNull Pageable pageable);
    CategoriaDTO obtenerCategoria(@NotNull @Positive Long id);
    CategoriaDTO registrarCategoria(@NotNull @Valid CategoriaDTO request);
    CategoriaDTO actualizarCategoria(@NotNull @Positive Long id, @NotNull @Valid CategoriaDTO request);

    Page<MaterialCristalDTO> listarMaterialCristal(@Size(max = 200) String termino, @NotNull Pageable pageable);
    MaterialCristalDTO obtenerMaterialCristal(@NotNull @Positive Long id);
    MaterialCristalDTO registrarMaterialCristal(@NotNull @Valid MaterialCristalDTO request);
    MaterialCristalDTO actualizarMaterialCristal(@NotNull @Positive Long id, @NotNull @Valid MaterialCristalDTO request);

    Page<TratamientoCristalDTO> listarTratamientoCristal(@Size(max = 200) String termino, @NotNull Pageable pageable);
    TratamientoCristalDTO obtenerTratamientoCristal(@NotNull @Positive Long id);
    TratamientoCristalDTO registrarTratamientoCristal(@NotNull @Valid TratamientoCristalDTO request);
    TratamientoCristalDTO actualizarTratamientoCristal(@NotNull @Positive Long id, @NotNull @Valid TratamientoCristalDTO request);

    Page<ServicioDTO> listarServicio(@Size(max = 200) String termino, @Min(0) @Max(1) Integer estado, @NotNull Pageable pageable);
    ServicioDTO obtenerServicio(@NotNull @Positive Long id);
    ServicioDTO registrarServicio(@NotNull @Valid ServicioDTO request);
    ServicioDTO actualizarServicio(@NotNull @Positive Long id, @NotNull @Valid ServicioDTO request);

    Page<LaboratorioDTO> listarLaboratorio(@Size(max = 200) String termino, @Min(0) @Max(1) Integer estado, @NotNull Pageable pageable);
    LaboratorioDTO obtenerLaboratorio(@NotNull @Positive Long id);
    LaboratorioDTO registrarLaboratorio(@NotNull @Valid LaboratorioDTO request);
    LaboratorioDTO actualizarLaboratorio(@NotNull @Positive Long id, @NotNull @Valid LaboratorioDTO request);

}
