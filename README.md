
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
                docker run -it -p8080:8080 licensing-service:latest | docker compose up

```
# Setting up the licensing service Spring Cloud Config Service dependencies
> Lo primero que debe hacer es agregar un par de entradas más al archivo Maven en
su servicio de licencias. La siguiente lista proporciona las entradas que necesitamos agregar.
> 
```
    Le dice a Spring Boot que tire bajar las dependencias necesarias para Spring Cloud Configuración

    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-config</artifactId>    
    </dependency>
```

> Configuración del servicio de licencias para usar Spring Cloud Config
Después de que se hayan definido las dependencias de Maven, debemos informar al servicio de licencias
dónde ponerse en contacto con el servidor de configuración de Spring Cloud. En un servicio Spring Boot que
usa Spring Cloud Config, la información de configuración se puede establecer en uno de estos archivos:
bootstrap.yml, bootstrap.properties, application.yml o application.properties.
 Como mencionamos anteriormente, el archivo bootstrap.yml lee las propiedades de la aplicación
antes de cualquier otra información de configuración. En general, el archivo bootstrap.yml contiene
contiene el nombre de la aplicación para el servicio, el perfil de la aplicación y el URI para contener
conectarse a un servidor de configuración. Cualquier otra información de configuración que desee
mantener local para el servicio (y no almacenado en Spring Cloud Config) se puede configurar en el servidor
vicios en el archivo application.yml local.
 Por lo general, la información que almacena en el archivo application.yml son datos de configuración
que es posible que desee tener disponible para un servicio incluso si el servidor Spring Cloud Config
el vicio no está disponible. Los archivos bootstrap.yml y application.yml se almacenan en un
directorio src/main/resources del proyecto.
 Para que el servicio de licencias se comunique con su servicio Spring Cloud Config,
estos parámetros se pueden definir en el archivo bootstrap.yml, en docker-compose.yml
archivo del servicio de licencias, o a través de argumentos JVM cuando inicia el servicio. El siguiente
La siguiente lista muestra cómo debería verse bootstrap.yml en su aplicación si así lo desea.
esta opción.