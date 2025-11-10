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
            // Solo ejecutar si no hay sensores
            if (sensorRepository.count() > 0) {
                System.out.println("Ya existen sensores, omitiendo inicialización de datos de prueba");
                return;
            }

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

            // Crear mediciones para los últimos 3 meses
            List<Medicion> mediciones = new ArrayList<>();
            LocalDateTime fechaInicio = LocalDateTime.now().minusMonths(3);
            LocalDateTime fechaFin = LocalDateTime.now();

            for (Sensor sensor : sensores) {
                // Generar mediciones cada 2 horas
                LocalDateTime fechaActual = fechaInicio;
                
                while (fechaActual.isBefore(fechaFin)) {
                    Medicion medicion = new Medicion();
                    medicion.setSensorId(sensor.getId());
                    medicion.setFechaHora(fechaActual);
                    
                    // Temperatura entre 5°C y 35°C con variación por ciudad
                    double tempBase = sensor.getCiudad().equals("Mendoza") ? 20 : 18;
                    double tempVariacion = random.nextGaussian() * 5; // Desviación estándar de 5°C
                    medicion.setTemperatura(Math.max(5, Math.min(35, tempBase + tempVariacion)));
                    
                    // Humedad entre 30% y 90%
                    double humBase = 60;
                    double humVariacion = random.nextGaussian() * 15; // Desviación estándar de 15%
                    medicion.setHumedad(Math.max(30, Math.min(90, humBase + humVariacion)));
                    
                    mediciones.add(medicion);
                    fechaActual = fechaActual.plusHours(2);
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
            System.out.println("Frecuencia: cada 2 horas");
            System.out.println("================================\n");
        };
    }
}
