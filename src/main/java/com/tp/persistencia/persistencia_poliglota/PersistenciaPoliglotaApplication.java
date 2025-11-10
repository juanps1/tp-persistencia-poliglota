package com.tp.persistencia.persistencia_poliglota;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PersistenciaPoliglotaApplication {

	public static void main(String[] args) {
		SpringApplication.run(PersistenciaPoliglotaApplication.class, args);
	}

}
