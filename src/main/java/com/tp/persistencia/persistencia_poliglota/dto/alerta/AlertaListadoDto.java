// Deprecated: DTO no longer used after switching alertas endpoints to return entity directly.
package com.tp.persistencia.persistencia_poliglota.dto.alerta;

@Deprecated
public record AlertaListadoDto(Long id, String tipo, String sensorId, java.time.Instant fechaHora, String descripcion, String estado, String severidad) {}