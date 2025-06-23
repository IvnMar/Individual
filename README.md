# Tarea2

## Cómo instalar y ejecutar la aplicación

1. Clona el repositorio:
    ```sh
    git clone https://github.com/tu_usuario/SistemaRecomendacion2025-2.git
    cd SistemaRecomendacion2025-2
    ```
    Esto descarga el código fuente de la aplicación a tu máquina local y te mueve al directorio del proyecto.

2. Configura la base de datos:
    - Asegúrate de tener MySQL instalado y ejecutándose.
    - Crea una base de datos llamada `tarea2`.
    ```sql
    CREATE DATABASE tarea2;
    ```
    Esto prepara el entorno de la base de datos que la aplicación necesita para almacenar y recuperar datos.

3. Configura las propiedades de la aplicación:
    - Abre el archivo [application.properties](http://_vscodecontentref_/2) y ajusta las propiedades de la base de datos según tu configuración local.
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/tarea2?useSSL=false&serverTimezone=UTC
    spring.datasource.username=tu_usuario
    spring.datasource.password=tu_contraseña
    ```
    Esto asegura que la aplicación pueda conectarse a la base de datos MySQL que configuraste en el paso anterior.

4. Compila y ejecuta la aplicación:
    ```sh
    ./mvnw clean package
    ./mvnw spring-boot:run
    ```
    El primer comando compila el código fuente y empaqueta la aplicación. El segundo comando inicia la aplicación usando Spring Boot.

5. Accede a la aplicación en tu navegador:
    ```
    http://localhost:8081
    ```
    Esto te permite interactuar con la aplicación a través de tu navegador web.

## Cómo desplegar usando Docker

1. Asegúrate de tener Docker y Docker Compose instalados.
    Esto es necesario para construir y ejecutar contenedores Docker.

2. Construye y levanta los contenedores:
    ```sh
    docker-compose up --build
    ```
    Este comando construye las imágenes Docker necesarias y levanta los contenedores definidos en el archivo [docker-compose.yml](http://_vscodecontentref_/3).

3. Accede a la aplicación en tu navegador:
    ```
    http://localhost:8081
    ```
    Esto te permite interactuar con la aplicación a través de tu navegador web, similar a la ejecución local.

4. Para detener los contenedores, ejecuta:
    ```sh
    docker-compose down
    ```
    Este comando detiene y elimina los contenedores, redes y volúmenes creados por `docker-compose up`.


Investigacion:
Investigación sobre Dockerización de la Aplicación

 1.- Dockerfile
El Dockerfile es un archivo de configuración que define el proceso para construir una imagen de Docker de una aplicación. Se divide en dos etapas: **construcción (build)** y ejecución (runtime).

Fase de Construcción (Build Stage)

FROM maven:3.8.4-openjdk-21 AS build

Usa la imagen oficial de Maven con OpenJDK 21 para compilar la aplicación.
AS build nombra esta etapa como build, permitiendo copiar archivos desde esta fase a la siguiente.

WORKDIR /app

Define /app como el directorio de trabajo en el contenedor.

COPY pom.xml .
COPY src ./src

COPY pom.xml . copia el archivo de configuración de Maven al contenedor.
COPY src ./src copia el código fuente de la aplicación.

RUN mvn clean package -DskipTests

Ejecuta Maven para compilar el código y generar un archivo .jar, omitiendo las pruebas.

Fase de Ejecución (Runtime Stage)

FROM openjdk:17-jdk-slim

Usa una imagen ligera de OpenJDK 17 para ejecutar la aplicación.

WORKDIR /app

Define el directorio de trabajo en el contenedor final.

COPY --from=build /app/target/*.jar app.jar

Copia el archivo .jar generado en la fase de construcción a esta imagen final.

EXPOSE 8080

Indica que la aplicación utilizará el puerto 8080.

ENTRYPOINT ["java","-jar","app.jar"]

Define el comando que ejecutará el contenedor al iniciarse.

---

2. docker-compose.yml
Este archivo permite definir y gestionar varios contenedores como servicios interconectados.

version: '3.8'

Especifica la versión de Docker Compose.

Servicio de la Aplicación (app)

services:
  app:
    build: .

Define un servicio app basado en la imagen generada con el Dockerfile.

ports:
  - "8080:8080"

Mapea el puerto 8080 del contenedor al 8080 del host.

depends_on:
  - db

Asegura que el servicio db (base de datos) se inicie antes que la aplicación.

environment:
  SPRING_DATASOURCE_URL: jdbc:mysql://db:3306/spring_hello
  SPRING_DATASOURCE_USERNAME: root
  SPRING_DATASOURCE_PASSWORD: example

Configura las variables de entorno para la conexión a la base de datos.

Servicio de Base de Datos (db)

  db:
    image: mysql:8.0

Usa la imagen oficial de MySQL 8.0.

environment:
  MYSQL_ROOT_PASSWORD: admin
  MYSQL_DATABASE: tarea2

MYSQL_ROOT_PASSWORD: admin define la contraseña para el usuario root.
MYSQL_DATABASE: tarea2 crea la base de datos tarea2 al iniciar MySQL.

ports:
  - "3306:3306"

Expone el puerto 3306 de MySQL para permitir conexiones externas.

volumes:
  - mysql_data:/var/lib/mysql
  - ./src/main/resources/schema.sql:/docker-entrypoint-initdb.d/schema.sql

mysql_data:/var/lib/mysql usa un volumen persistente para los datos de MySQL.
./src/main/resources/schema.sql:/docker-entrypoint-initdb.d/schema.sql carga un script SQL inicial para configurar la base de datos.

Volúmenes

volumes:
  mysql_data:

Define un volumen persistente mysql_data para evitar la pérdida de datos al reiniciar el contenedor.



