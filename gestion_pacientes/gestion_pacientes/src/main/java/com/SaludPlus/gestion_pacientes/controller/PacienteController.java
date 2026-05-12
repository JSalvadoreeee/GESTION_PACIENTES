package com.SaludPlus.gestion_pacientes.controller;

import com.SaludPlus.gestion_pacientes.model.Paciente;
import com.SaludPlus.gestion_pacientes.service.PacienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pacientes")
@Tag(name = "1. Gestión de Pacientes", description = "Endpoints para el mantenimiento de fichas clínicas y carga masiva de datos")
// Esto vincula el controlador con el candado que configuramos en la clase principal
@SecurityRequirement(name = "SaludPlusSecurity") 
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @Operation(summary = "Obtener catálogo completo", description = "Recupera la lista total de pacientes registrados en la base de datos principal.")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    @GetMapping
    public List<Paciente> listar() {
        return this.pacienteService.findAll();
    }

    @Operation(summary = "Buscar por ID", description = "Localiza la ficha de un paciente específico mediante su identificador único.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Paciente localizado"),
        @ApiResponse(responseCode = "404", description = "El ID proporcionado no existe en los registros")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Paciente> buscarPorId(
            @Parameter(description = "ID del paciente a buscar", example = "1") 
            @PathVariable Long id) {
        return this.pacienteService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Registrar Paciente", description = "Crea un nuevo registro de paciente. El sistema genera automáticamente un respaldo en la base de datos espejo.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Registro creado y respaldado con éxito", 
                     content = @Content(schema = @Schema(implementation = Paciente.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "401", description = "No autorizado - Token faltante o inválido")
    })
    @PostMapping
    public ResponseEntity<Paciente> guardar(@RequestBody Paciente paciente) {
        return ResponseEntity.status(201).body(this.pacienteService.save(paciente));
    }

    @Operation(summary = "Modificar Ficha Clínica", description = "Actualiza la información de un paciente existente. Si el ID no existe, retorna error 404.")
    @PutMapping("/{id}")
    public ResponseEntity<Paciente> actualizar(
            @Parameter(description = "ID del paciente a actualizar", example = "1")
            @PathVariable Long id, 
            @RequestBody Paciente paciente) {
        try {
            Paciente actualizado = this.pacienteService.update(id, paciente);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Importación Masiva (Batch)", description = "Procesa múltiples registros en una sola transacción para optimizar el rendimiento de la base de datos.")
    @PostMapping("/batch")
    public ResponseEntity<List<Paciente>> cargaMasiva(@RequestBody List<Paciente> pacientes) {
        List<Paciente> guardados = this.pacienteService.saveAll(pacientes);
        return ResponseEntity.ok(guardados);
    }

    @Operation(summary = "Eliminar Registro", description = "Remueve físicamente el registro del paciente del sistema.")
    @ApiResponse(responseCode = "204", description = "Registro eliminado satisfactoriamente")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del paciente a eliminar", example = "1")
            @PathVariable Long id) {
        this.pacienteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}