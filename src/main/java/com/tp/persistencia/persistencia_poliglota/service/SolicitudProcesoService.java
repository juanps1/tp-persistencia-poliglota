package com.tp.persistencia.persistencia_poliglota.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tp.persistencia.persistencia_poliglota.model.nosql.Alerta;
import com.tp.persistencia.persistencia_poliglota.model.nosql.Medicion;
import com.tp.persistencia.persistencia_poliglota.model.nosql.Sensor;
import com.tp.persistencia.persistencia_poliglota.model.sql.SolicitudProceso;
import com.tp.persistencia.persistencia_poliglota.model.sql.Usuario;
import com.tp.persistencia.persistencia_poliglota.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SolicitudProcesoService {

    private final SolicitudProcesoRepository solicitudProcesoRepository;
    private final MedicionRepository medicionRepository;
    private final SensorRepository sensorRepository;
    private final AlertaRepository alertaRepository;
    private final UsuarioRepository usuarioRepository;
    private final GeneradorPdfService generadorPdfService;
    private final ObjectMapper objectMapper;

    public SolicitudProcesoService(SolicitudProcesoRepository solicitudProcesoRepository,
                                   MedicionRepository medicionRepository,
                                   SensorRepository sensorRepository,
                                   AlertaRepository alertaRepository,
                                   UsuarioRepository usuarioRepository,
                                   GeneradorPdfService generadorPdfService) {
        this.solicitudProcesoRepository = solicitudProcesoRepository;
        this.medicionRepository = medicionRepository;
        this.sensorRepository = sensorRepository;
        this.alertaRepository = alertaRepository;
        this.usuarioRepository = usuarioRepository;
        this.generadorPdfService = generadorPdfService;
        this.objectMapper = new ObjectMapper();
    }

    public List<SolicitudProceso> listar() {
        return solicitudProcesoRepository.findAll();
    }

    public List<SolicitudProceso> listarPorUsuario(Long usuarioId) {
        return solicitudProcesoRepository.findByUsuarioId(usuarioId);
    }

    public List<SolicitudProceso> buscarPendientes() {
        return solicitudProcesoRepository.findByEstado("pendiente");
    }

    public Optional<SolicitudProceso> buscarPorId(Long id) {
        return solicitudProcesoRepository.findById(id);
    }

    public SolicitudProceso guardar(SolicitudProceso solicitud) {
        return solicitudProcesoRepository.save(solicitud);
    }

    public boolean eliminar(Long id) {
        if (solicitudProcesoRepository.existsById(id)) {
            solicitudProcesoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public void ejecutarProceso(SolicitudProceso solicitud) {
        try {
            solicitud.setEstado("en_proceso");
            solicitudProcesoRepository.save(solicitud);

            Map<String, Object> parametros = parseParametros(solicitud.getParametrosJson());
            Map<String, Object> resultado;
            String rutaPdf;

            switch (solicitud.getTipoProceso()) {
                case "informe_max_min":
                    resultado = ejecutarInformeMaxMin(parametros);
                    rutaPdf = generadorPdfService.generarInformeMaxMin(solicitud.getId(), resultado, parametros);
                    break;
                case "informe_promedio":
                    resultado = ejecutarInformePromedio(parametros);
                    rutaPdf = generadorPdfService.generarInformePromedio(solicitud.getId(), resultado, parametros);
                    break;
                case "alerta":
                    resultado = ejecutarAlerta(parametros, solicitud.getUsuario());
                    rutaPdf = generadorPdfService.generarInformeAlertas(solicitud.getId(), resultado, parametros);
                    break;
                case "consulta":
                    resultado = ejecutarConsulta(parametros);
                    rutaPdf = generadorPdfService.generarInformeConsulta(solicitud.getId(), resultado, parametros);
                    break;
                default:
                    throw new IllegalArgumentException("Tipo de proceso no soportado: " + solicitud.getTipoProceso());
            }

            solicitud.setResultadoJson(objectMapper.writeValueAsString(resultado));
            solicitud.setRutaArchivoPdf(rutaPdf);
            solicitud.setEstado("completado");
            solicitud.setFechaFinalizacion(LocalDateTime.now());
            solicitud.setMensajeError(null);

        } catch (Exception e) {
            solicitud.setEstado("error");
            solicitud.setMensajeError(e.getMessage());
            solicitud.setFechaFinalizacion(LocalDateTime.now());
        }

        solicitudProcesoRepository.save(solicitud);
    }

    private Map<String, Object> ejecutarInformeMaxMin(Map<String, Object> parametros) {
        List<Medicion> mediciones = obtenerMediciones(parametros);

        if (mediciones.isEmpty()) {
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("temperaturaMax", 0.0);
            resultado.put("temperaturaMin", 0.0);
            resultado.put("humedadMax", 0.0);
            resultado.put("humedadMin", 0.0);
            resultado.put("totalMediciones", 0);
            return resultado;
        }

        double tempMax = mediciones.stream().mapToDouble(Medicion::getTemperatura).max().orElse(0.0);
        double tempMin = mediciones.stream().mapToDouble(Medicion::getTemperatura).min().orElse(0.0);
        double humMax = mediciones.stream().mapToDouble(Medicion::getHumedad).max().orElse(0.0);
        double humMin = mediciones.stream().mapToDouble(Medicion::getHumedad).min().orElse(0.0);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("temperaturaMax", tempMax);
        resultado.put("temperaturaMin", tempMin);
        resultado.put("humedadMax", humMax);
        resultado.put("humedadMin", humMin);
        resultado.put("totalMediciones", mediciones.size());

        return resultado;
    }

    private Map<String, Object> ejecutarInformePromedio(Map<String, Object> parametros) {
        List<Medicion> mediciones = obtenerMediciones(parametros);

        if (mediciones.isEmpty()) {
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("temperaturaPromedio", 0.0);
            resultado.put("humedadPromedio", 0.0);
            resultado.put("totalMediciones", 0);
            return resultado;
        }

        double tempPromedio = mediciones.stream().mapToDouble(Medicion::getTemperatura).average().orElse(0.0);
        double humPromedio = mediciones.stream().mapToDouble(Medicion::getHumedad).average().orElse(0.0);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("temperaturaPromedio", tempPromedio);
        resultado.put("humedadPromedio", humPromedio);
        resultado.put("totalMediciones", mediciones.size());

        return resultado;
    }

    private Map<String, Object> ejecutarAlerta(Map<String, Object> parametros, Usuario usuario) {
        List<Medicion> mediciones = obtenerMediciones(parametros);

        Double tempMin = getDoubleParam(parametros, "temperaturaMin");
        Double tempMax = getDoubleParam(parametros, "temperaturaMax");
        Double humMin = getDoubleParam(parametros, "humedadMin");
        Double humMax = getDoubleParam(parametros, "humedadMax");

        int alertasGeneradas = 0;
        int medicionesFueraRango = 0;

        for (Medicion medicion : mediciones) {
            boolean fueraRango = false;

            if (tempMin != null && medicion.getTemperatura() < tempMin) {
                fueraRango = true;
            }
            if (tempMax != null && medicion.getTemperatura() > tempMax) {
                fueraRango = true;
            }
            if (humMin != null && medicion.getHumedad() < humMin) {
                fueraRango = true;
            }
            if (humMax != null && medicion.getHumedad() > humMax) {
                fueraRango = true;
            }

            if (fueraRango) {
                medicionesFueraRango++;
                
                // Crear alerta en la base de datos
                Alerta alerta = new Alerta();
                alerta.setUsuarioId(usuario.getId());
                alerta.setMensaje(String.format("Medición fuera de rango - Sensor: %s, Temp: %.2f°C, Hum: %.2f%%", 
                    medicion.getSensorId(), medicion.getTemperatura(), medicion.getHumedad()));
                alerta.setNivel("ALTA");
                alerta.setFechaCreacion(LocalDateTime.now());
                alertaRepository.save(alerta);
                
                alertasGeneradas++;
            }
        }

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("alertasGeneradas", alertasGeneradas);
        resultado.put("medicionesFueraRango", medicionesFueraRango);
        resultado.put("totalMedicionesAnalizadas", mediciones.size());

        return resultado;
    }

    private Map<String, Object> ejecutarConsulta(Map<String, Object> parametros) {
        List<Medicion> mediciones = obtenerMediciones(parametros);

        List<Map<String, Object>> datos = mediciones.stream().map(m -> {
            Map<String, Object> item = new HashMap<>();
            item.put("sensorId", m.getSensorId());
            item.put("fechaHora", m.getFechaHora().toString());
            item.put("temperatura", m.getTemperatura());
            item.put("humedad", m.getHumedad());
            return item;
        }).collect(Collectors.toList());

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("totalRegistros", mediciones.size());
        resultado.put("mediciones", datos);

        return resultado;
    }

    private List<Medicion> obtenerMediciones(Map<String, Object> parametros) {
        LocalDateTime fechaDesde = parseDateTime(parametros.get("fechaDesde"));
        LocalDateTime fechaHasta = parseDateTime(parametros.get("fechaHasta"));

        List<String> sensorIds = new ArrayList<>();

        // Filtrar por sensor específico
        if (parametros.containsKey("sensorId")) {
            sensorIds.add(parametros.get("sensorId").toString());
        } 
        // Filtrar por ciudad
        else if (parametros.containsKey("ciudad")) {
            String ciudad = parametros.get("ciudad").toString();
            List<Sensor> sensores = sensorRepository.findByCiudad(ciudad);
            sensorIds = sensores.stream().map(Sensor::getId).collect(Collectors.toList());
        } 
        // Filtrar por país
        else if (parametros.containsKey("pais")) {
            String pais = parametros.get("pais").toString();
            List<Sensor> sensores = sensorRepository.findByPais(pais);
            sensorIds = sensores.stream().map(Sensor::getId).collect(Collectors.toList());
        }
        // Si no hay filtro, tomar todos los sensores
        else {
            List<Sensor> sensores = sensorRepository.findAll();
            sensorIds = sensores.stream().map(Sensor::getId).collect(Collectors.toList());
        }

        if (sensorIds.isEmpty()) {
            return new ArrayList<>();
        }

        return medicionRepository.findBySensorIdInAndFechaHoraBetween(sensorIds, fechaDesde, fechaHasta);
    }

    private Map<String, Object> parseParametros(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private LocalDateTime parseDateTime(Object value) {
        if (value == null) {
            return LocalDateTime.now().minusYears(10); // Default muy antiguo
        }
        
        try {
            String strValue = value.toString();
            // Intentar varios formatos
            try {
                return LocalDateTime.parse(strValue);
            } catch (Exception e) {
                // Intentar con formato de fecha simple
                return LocalDateTime.parse(strValue + "T00:00:00");
            }
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }

    private Double getDoubleParam(Map<String, Object> parametros, String key) {
        Object value = parametros.get(key);
        if (value == null) return null;
        
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        
        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return null;
        }
    }
}

