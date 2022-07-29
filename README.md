
#### resumen

# licensing-service

```
crear imágenes de Docker usando desde la version Spring Boot v2.3...
 > Tener Docker y Docker Compose instalados.
 > Spring Boot con una versión igual o superior a 2.3.
 > pom.xml con la versión spring-boot-starter-parent 2.3 o superior.
 
 <parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.4.0</version>
    <relativePath/> <!-- lookup parent from repository -->
 </parent>
 
 
 BUILDPACKS
    Los Buildpacks son herramientas que proporcionan dependencias de aplicaciones y frameworks, trans-
    formando nuestro código fuente en una imagen de aplicación ejecutable. En otras palabras, construir-
    packs detectan y obtienen todo lo que la aplicación necesita para ejecutarse.
    Spring Boot 2.3.0 presenta soporte para crear imágenes de Docker usando Cloud
    Paquetes de compilación nativos. Este soporte se agregó a los complementos de Maven y Gradle usando el
    spring-boot:objetivo build-image para Maven y la tarea bootBuildImage para Gradle.

    > Spring Boot Maven Plugin at https://docs.spring.io/spring-boot/docs/2.3.0.M1/maven-plugin/html
    > Spring Boot Gradle Plugin at https://docs.spring.io/spring-boot/docs/2.3.0.M1/gradle-plugin/reference/html
    
    utiliza escenario Maven. Para construir la imagen, pasos:
    
    Una vez que tengamos la configuración establecida en nuestro archivo pom.xml, podemos ejecutar lo siguiente:
    siguiente comando para reconstruir nuestro Spring Boot JAR:
    
        > mvn clean package
        
    Una vez creado el archivo JAR, podemos ejecutar el siguiente comando en la dirección raíz:
    para mostrar las capas y el orden en que estas deben ser
    añadido a nuestro Dockerfile:
    
        > java -Djarmode=layertools -jar target/licensing-service-0.0.1-SNAPSHOT.jar list
        
    resultado:
                dependencies
                spring-boot-loader
                snapshot-dependencies
                application
        
    dockerfile:
                ...
                ...
                WORKDIR application
                COPY --from=build application/dependencies/ ./    
                COPY --from=build application/spring-boot-loader/ ./
                COPY --from=build application/snapshot-dependencies/ ./
                COPY --from=build application/application/ ./    
                ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]   
    
    luego:
                docker build . --tag licensing-service
                docker run -it -p8080:8080 licensing-service:latest

```
# configuration management
### Spring Cloud Configuration Server

> la carga de la gestión de configuration management  para un microservicio se 
produce durante la fase de arranque del microservicio.
la figura  muestra el ciclo de vida del microservicio.
> 
> ![](../licensing-service/public/images/architecturecinfigurationmanagement.png)
>
> la gestión de configuración de aplicaciones estableciendo
  cuatro principios que queremos seguir:
> *  Segregate—Necesitamos separar completamente la información de configuración del servicio
     del despliegue físico real de un servicio. De hecho, la aplicación
     la figuración no debe implementarse con la instancia de servicio. En cambio,
     la información de configuración debe pasarse como variables de entorno al servicio de inicio o leer desde un repositorio centralizado cuando se inicia el servicio.
> *  Abstract—También necesitamos abstraer el acceso a los datos de configuración detrás de un servicio
     interfaz. En lugar de escribir código que lea directamente el repositorio de servicios,
     ya sea una base de datos basada en archivos o JDBC, deberíamos usar un servidor JSON basado en REST.
     vice para recuperar los datos de configuración de la aplicación.
> *  Centralize—porque una aplicación basada en la nube puede tener literalmente cientos de
     servicios, es fundamental minimizar la cantidad de repositorios diferentes utilizados para
     mantener los datos de configuración. Centralice la configuración de su aplicación en tan pocos
     repositorios como sea posible.
> *   Harden—Porque la información de configuración de su aplicación va a ser
      completamente segregado de su servicio implementado y centralizado, es fundamental
      que la solución que utilice e implemente sea altamente disponible y redundante.
> 
> Una de las cosas clave que debe recordar es que cuando separa su configuración
  información fuera de su código real, está creando una dependencia externa que
  será necesario administrar y controlar la versión. No podemos enfatizar lo suficiente que el
  es necesario realizar un seguimiento de los datos de configuración de la aplicación y controlar la versión porque
  configuración de aplicaciones mal administrada es un caldo de cultivo fértil para
  detectar errores e interrupciones no planificadas.
> 
> 
> Tomemos los cuatro principios y vea cómo se aplican cuando se inicia el servicio. 
  la figura presenta el proceso de arranque con más detalle y muestra cómo un servicio de configuración
  juega un papel fundamental en este paso.
> 
> ![](/home/tomate/Escritorio/fuentes/java/licensing-service/public/images/imagen2.png)
> 
> 1 Cuando aparece una instancia de microservicio, llama a un punto final de servicio para leer su
información de configuración, que es específica para el entorno en el que está operando
in. La información de conexión para la gestión de configuración (conexión
credenciales de ción, punto final de servicio, etc.) luego pasa al microservicio
como empieza.
> 
> 2 La configuración real reside en un repositorio. Basado en la implementación
de su repositorio de configuración, puede elegir diferentes formas de mantener su
datos de configuración Esto puede incluir archivos bajo control de fuente, datos relacionales.
bases o almacenes de datos clave-valor.
> 
> 3 La gestión real de los datos de configuración de la aplicación se produce de forma independiente.
dependiendo de cómo se implemente la aplicación. Cambios en el manual de configuración
La administración generalmente se maneja a través de la canalización de compilación e implementación,
donde las modificaciones se pueden etiquetar con información de versión e implementar
a través de los diferentes entornos (desarrollo, puesta en escena, producción y
etcétera).
> 
> 4 Cuando cambia la gestión de la configuración, los servicios que utilizan esa aplicación
Los datos de configuración de cationes deben ser notificados de la alteración y actualizar sus
copia de los datos de la solicitud.
> 
> # Opciones de implementación
> 
> ![](/home/tomate/Escritorio/fuentes/java/licensing-service/public/images/imagen3.png)
> ![](/home/tomate/Escritorio/fuentes/java/licensing-service/public/images/imagen4.png)
> 
> 
> Todas las soluciones de la tabla se pueden utilizar fácilmente para crear una gestión de configuración
solución. Para los ejemplos de este capítulo y del resto del libro,
utilice Spring Cloud Configuration Server (a menudo llamado Spring Cloud Config
Server o, simplemente, el Config Server), que se adapta perfectamente a nuestro microservicios arquitectura. 
Elegimos esta solución porque:
> 
> * El servidor de configuración de Spring Cloud es fácil de configurar y usar.
> * Spring Cloud Config se integra estrechamente con Spring Boot. Literalmente puedes leer
> todos los datos de configuración de su aplicación con algunas anotaciones fáciles de usar.
> * El servidor de configuración ofrece múltiples backends para almacenar datos de configuración.
> * De todas las soluciones de la tabla, Config Server puede integrarse directamente con
> la plataforma de control de fuente Git y con HashiCorp Vault. 
> ### pasos
> 1 Configure un servidor de configuración de Spring Cloud. Demostraremos tres diferentes
mecanismos para servir datos de configuración de aplicaciones, uno que usa el archivo-
system, otro usando un repositorio Git y otro usando HashiCorp Vault.
> 
> 2 Continúe creando el servicio de licencias para recuperar datos de una base de datos.
> 
> 3 Conecte el servicio Spring Cloud Config a su servicio de licencias para servir
los datos de configuración de la aplicación.
> ## Construyendo nuestro servidor de configuración Spring Cloud