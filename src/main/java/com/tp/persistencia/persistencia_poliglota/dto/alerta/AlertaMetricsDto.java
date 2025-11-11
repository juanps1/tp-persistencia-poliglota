// Deprecated: DTO no longer used; metrics now returned as Map<String,Long>.
package com.tp.persistencia.persistencia_poliglota.dto.alerta;

@Deprecated
public record AlertaMetricsDto(long activas, long nuevasHoy, long resueltasSemana, long sensorActivas, long climaticaActivas) {}