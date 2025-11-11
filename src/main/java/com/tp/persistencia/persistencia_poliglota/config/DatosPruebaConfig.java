package com.tp.persistencia.persistencia_poliglota.config;

import com.tp.persistencia.persistencia_poliglota.model.nosql.Medicion;
import com.tp.persistencia.persistencia_poliglota.model.nosql.Sensor;
import com.tp.persistencia.persistencia_poliglota.repository.MedicionRepository;
import com.tp.persistencia.persistencia_poliglota.repository.SensorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
public class DatosPruebaConfig {

    @Bean
    CommandLineRunner initDatosPrueba(SensorRepository sensorRepository, 
                                      MedicionRepository medicionRepository) {
        return args -> {
            // Limpiar datos existentes para regenerar
            System.out.println("Limpiando datos existentes de MongoDB...");
            medicionRepository.deleteAll();
            sensorRepository.deleteAll();
            System.out.println("Datos eliminados. Regenerando con período 2020-2025...");

            System.out.println("Inicializando datos de prueba...");

            Random random = new Random();
            List<Sensor> sensores = new ArrayList<>();

            // Crear sensores en diferentes ciudades de Argentina
            String[] ciudades = {"Buenos Aires", "Córdoba", "Rosario", "Mendoza", "La Plata", "San Miguel de Tucumán"};

            int sensorCount = 1;
            for (String ciudad : ciudades) {
                for (int i = 0; i < 2; i++) { // 2 sensores por ciudad
                    Sensor sensor = new Sensor();
                    sensor.setId("sensor-" + String.format("%03d", sensorCount));
                    sensor.setNombre("Sensor " + ciudad + " " + (i + 1));
                    sensor.setTipo("temperatura-humedad");
                    sensor.setCiudad(ciudad);
                    sensor.setPais("Argentina");
                    sensor.setLatitud(-34.0 + random.nextDouble() * 10);
                    sensor.setLongitud(-64.0 + random.nextDouble() * 10);
                    sensor.setEstado("activo");
                    sensor.setFechaInicioEmision(LocalDateTime.now().minusMonths(6));
                    
                    sensores.add(sensor);
                    sensorCount++;
                }
            }

            sensorRepository.saveAll(sensores);
            System.out.println("Creados " + sensores.size() + " sensores");

            // Crear mediciones desde 2020 hasta hoy (aprox 20k mediciones)
            List<Medicion> mediciones = new ArrayList<>();
            LocalDateTime fechaInicio = LocalDateTime.of(2020, 1, 1, 0, 0);
            LocalDateTime fechaFin = LocalDateTime.now();
            
            // Calcular intervalo para generar ~20k mediciones con 12 sensores
            // 20000 / 12 sensores = ~1666 mediciones por sensor
            long diasTotales = java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, fechaFin);
            int medicionesPorSensor = 1666;
            long horasIntervalo = (diasTotales * 24) / medicionesPorSensor;

            for (Sensor sensor : sensores) {
                // Generar mediciones con intervalo calculado
                LocalDateTime fechaActual = fechaInicio;
                int contador = 0;
                
                while (fechaActual.isBefore(fechaFin) && contador < medicionesPorSensor) {
                    Medicion medicion = new Medicion();
                    medicion.setSensorId(sensor.getId());
                    medicion.setFechaHora(fechaActual);
                    
                    // Temperatura entre 5°C y 35°C con variación por ciudad y estacionalidad
                    double tempBase = sensor.getCiudad().equals("Mendoza") ? 20 : 18;
                    
                    // Variación estacional (más calor en verano, más frío en invierno)
                    int mes = fechaActual.getMonthValue();
                    double variacionEstacional = 0;
                    if (mes == 12 || mes == 1 || mes == 2) { // Verano
                        variacionEstacional = 8;
                    } else if (mes >= 6 && mes <= 8) { // Invierno
                        variacionEstacional = -8;
                    }
                    
                    double tempVariacion = random.nextGaussian() * 5;
                    medicion.setTemperatura(Math.max(5, Math.min(35, tempBase + variacionEstacional + tempVariacion)));
                    
                    // Humedad entre 30% y 90%
                    double humBase = 60;
                    double humVariacion = random.nextGaussian() * 15;
                    medicion.setHumedad(Math.max(30, Math.min(90, humBase + humVariacion)));
                    
                    mediciones.add(medicion);
                    fechaActual = fechaActual.plusHours(horasIntervalo);
                    contador++;
                }
            }

            medicionRepository.saveAll(mediciones);
            System.out.println("Creadas " + mediciones.size() + " mediciones de prueba");

            // Estadísticas
            System.out.println("\n=== DATOS DE PRUEBA CREADOS ===");
            System.out.println("Sensores totales: " + sensores.size());
            System.out.println("Ciudades: " + String.join(", ", ciudades));
            System.out.println("Mediciones totales: " + mediciones.size());
            System.out.println("Período: " + fechaInicio.toLocalDate() + " a " + fechaFin.toLocalDate());
            System.out.println("Intervalo aproximado: cada " + horasIntervalo + " horas");
            System.out.println("================================\n");
        };
    }
}
