/**
 * extrae todas las propiedades de ejemplo del servidor de configuración de Spring Cloud y
 * (pring Cloud Configuration Server)
 * los inyecta en el atributo de propiedad en la clase ServiceConfig.
 * Si bien es posible inyectar directamente valores de configuración en propiedades en
 * clases individuales, nos ha resultado útil centralizar toda la configuración
 * información en una sola clase de configuración y luego inyectar la configuración
 * clase en donde se necesita.
 *
 * Una de las primeras preguntas que surge de los equipos de desarrollo cuando quieren
 * usar Spring Cloud Configuration Server es cómo pueden actualizar dinámicamente sus
 * aplicaciones cuando cambia una propiedad. Está seguro. El servidor de configuración siempre sirve
 * la última versión de una propiedad. Los cambios realizados en una propiedad a través de su depósito subyacente
 * tory estará al día!
 * Las aplicaciones Spring Boot, sin embargo, solo leen sus propiedades al inicio, por lo que
 * Los cambios reales realizados en el servidor de configuración no serán recogidos automáticamente por el
 * Aplicación Spring Boot. Pero Spring Boot Actuator ofrece una anotación @RefreshScope
 * que permite a un equipo de desarrollo acceder a un punto final /refresh que forzará la
 * Aplicación Spring Boot para volver a leer la configuración de su aplicación. se agrega en el
 * metodo main de arranque
 */


package com.optimagrowth.license.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "example")
@Getter @Setter
public class ServiceConfig {

    private String property;

}