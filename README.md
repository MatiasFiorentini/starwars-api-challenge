# Star Wars API Challenge

API REST desarrollada en **Java 21 + Spring Boot** que integra con la [SWAPI (Star Wars API)](https://www.swapi.tech/documentation) para exponer información de **People**, **Films**, **Starships** y **Vehicles** del universo Star Wars, con listado paginado, filtrado por ID y/o nombre, autenticación segura mediante JWT, cache en memoria, documentación interactiva con Swagger, y una batería de tests unitarios y de integración.

> 🔗 **Demo desplegada:** _`https://starwars-api-challenge.onrender.com`_

> ⚠️ **Nota:** el servicio usa el plan gratuito de Render, que se "duerme" tras
> 15 minutos de inactividad. El primer request puede tardar 30-60 segundos
> en responder mientras el servicio se reactiva; los siguientes son normales.

---

## Índice

- [Tecnologías utilizadas](#tecnologías-utilizadas)
- [Arquitectura](#arquitectura)
- [Requisitos previos](#requisitos-previos)
- [Cómo levantar el proyecto localmente](#cómo-levantar-el-proyecto-localmente)
- [Configuración (`application.properties`)](#configuración-applicationproperties)
- [Autenticación](#autenticación)
- [Endpoints disponibles](#endpoints-disponibles)
- [Documentación interactiva (Swagger)](#documentación-interactiva-swagger)
- [Consola de la base de datos (H2)](#consola-de-la-base-de-datos-h2)
- [Cache](#cache)
- [Tests](#tests)
- [Docker](#docker)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Decisiones de diseño relevantes](#decisiones-de-diseño-relevantes)
- [Consideraciones para producción](#consideraciones-para-producción)

---

## Tecnologías utilizadas

| Tecnología | Uso |
|---|---|
| Java 21 | Lenguaje |
| Spring Boot 4.0.7 | Framework principal |
| Spring Web (MVC) | Controllers REST |
| Spring WebClient | Cliente HTTP hacia SWAPI |
| Spring Security | Autenticación y autorización |
| JJWT | Generación y validación de JWT |
| Spring Data JPA + H2 | Persistencia de usuarios (base en memoria) |
| Caffeine | Cache en memoria |
| springdoc-openapi (Swagger) | Documentación interactiva de la API |
| Lombok | Reducción de boilerplate |
| JUnit 5 + Mockito + AssertJ | Testing |
| Maven | Build y gestión de dependencias |
| Docker | Empaquetado y despliegue |

---

## Arquitectura

El proyecto sigue una arquitectura en capas, con separación estricta de responsabilidades:

```
Controller → Service → Client (WebClient hacia SWAPI)
                ↓
             Mapper (DTO externo → DTO propio)
```

- **`dto/external`**: mismas responses que devuelve SWAPI (nunca se expone directamente).
- **`dto/request` / `dto/response`**: contrato propio de esta API, desacoplado de SWAPI.
- **`client`**: un único `SwapiClient` genérico, compartido por las 4 entidades, responsable de toda la comunicación HTTP con SWAPI (incluye el manejo de paginación, filtros y el parámetro `expanded=true` para traer datos completos).
- **`mapper`**: traduce DTOs externos a DTOs propios.
- **`service`**: lógica de negocio, orquestación y cache.
- **`controller`**: expone los endpoints REST.
- **`security`**: autenticación JWT completa
- **`exception`**: manejo global de errores (`@RestControllerAdvice`), con un formato de error consistente en toda la API.

---

## Requisitos previos

- **Java 21** (JDK, no solo JRE)
- **Maven 3.9+** (o usar el wrapper `mvnw` si está incluido)
---

## Cómo levantar el proyecto localmente

### 1. Clonar el repositorio

```bash
git clone <URL-de-este-repositorio>
cd challenge
```

### 2. Compilar y correr

```bash
mvn clean install
mvn spring-boot:run
```

La aplicación queda disponible en:

```
http://localhost:8080
```

---

## Configuración (`application.properties`)

| Propiedad | Descripción | Valor por defecto                                       |
|---|---|---------------------------------------------------------|
| `swapi.base-url` | URL base de la API de SWAPI | `https://www.swapi.tech/api`                            |
| `jwt.secret` | Clave secreta (Base64) para firmar los JWT | Ya configurada solo por motivos de prueba del challenge |
| `jwt.expiration-ms` | Duración del token en milisegundos | `86400000` (24 horas)                                   |
| `spring.datasource.url` | URL de la base H2 en memoria | `jdbc:h2:mem:challenge_db`                              |
| `spring.jpa.hibernate.ddl-auto` | Estrategia de creación de tablas | `update`                                                |
| `spring.h2.console.enabled` | Habilita la consola web de H2 | `true`                                                  |

> ⚠️ La base de datos H2 es **en memoria**: todos los usuarios registrados se pierden al reiniciar la aplicación.

---

## Autenticación

La API utiliza autenticación **JWT stateless**. Todos los endpoints de negocio (People, Films, Starships, Vehicles) requieren un token válido en el header `Authorization`.

### 1. Registrarse

```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "matias",
  "password": "miPassword123"
}
```

Devuelve un token inmediatamente (no hace falta loguearse después de registrarse):

```json
{ "token": "eyJhbGciOiJIUzI1NiJ9..." }
```

### 2. Iniciar sesión (si ya tenés cuenta)

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "matias",
  "password": "miPassword123"
}
```

### 3. Usar el token en los endpoints protegidos

```http
GET /api/people
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## Endpoints disponibles

Todos los endpoints, salvo `/api/auth/**`, requieren autenticación JWT.

### Autenticación

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/api/auth/register` | Registra un usuario nuevo y devuelve un JWT |
| `POST` | `/api/auth/login` | Autentica y devuelve un JWT |

### People

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/people` | Listado paginado. Filtros: `name`, `page`, `size` |
| `GET` | `/api/people/{id}` | Detalle de una persona por ID |

### Films

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/films` | Listado paginado. Filtros: `title`, `page`, `size` |
| `GET` | `/api/films/{id}` | Detalle de una película por ID |

### Starships

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/starships` | Listado paginado. Filtros: `name`, `page`, `size` |
| `GET` | `/api/starships/{id}` | Detalle de una nave por ID |

### Vehicles

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/vehicles` | Listado paginado. Filtros: `name`, `page`, `size` |
| `GET` | `/api/vehicles/{id}` | Detalle de un vehículo por ID |

### Formato de error (consistente en toda la API)

```json
{
  "timestamp": "2026-08-17T21:10:33.512Z",
  "status": 404,
  "error": "Not Found",
  "message": "Recurso no encontrado en SWAPI: /people/999",
  "path": "/api/people/999"
}
```

---

## Documentación interactiva (Swagger)

Con la aplicación corriendo, la documentación completa e interactiva está disponible en:

```
http://localhost:8080/swagger-ui.html
```

Tambien puede probar la documentacion interactiva desde render

```
https://starwars-api-challenge.onrender.com/swagger-ui.html
```

Desde ahí se puede:
- Ver todos los endpoints, agrupados por entidad, con ejemplos y descripciones.
- Ejecutar `POST /api/auth/login` directamente y copiar el token obtenido.
- Hacer click en el botón **Authorize** (candado, arriba a la derecha) y pegar el token, para poder probar los endpoints protegidos sin salir de la UI.

---

## Consola de la base de datos (H2)

Para inspeccionar visualmente los usuarios registrados:

```
http://localhost:8080/h2-console
```

Datos de conexión:

| Campo | Valor |
|---|---|
| Driver Class | `org.h2.Driver` |
| JDBC URL | `jdbc:h2:mem:challenge_db` |
| User Name | `sa` |
| Password | *(vacío)* |

---

## Cache

Los métodos `find...(filter)` y `findById(id)` de las 4 entidades (People, Films, Starships, Vehicles) están cacheados en memoria con **Caffeine**, para evitar llamadas repetidas e innecesarias a SWAPI.

- Tamaño máximo por cache: 500 entradas
- Expiración: 10 minutos desde la escritura

---

## Tests

El proyecto incluye tests unitarios y de integración, cubriendo mappers, services, generación/validación de JWT y controllers (incluyendo casos de autenticación, validación y manejo de errores).

### Correr todos los tests

```bash
mvn test
```

### Tipos de test incluidos

| Tipo | Ubicación | Qué prueban |
|---|---|---|
| Unitarios | `mapper/*Test.java` | Transformación de DTOs externos a DTOs propios, sin dependencias |
| Unitarios | `service/impl/*Test.java` | Lógica de negocio, con `SwapiClient` mockeado (Mockito) |
| Unitarios | `security/JwtServiceTest.java` | Generación, extracción y validación de tokens JWT |
| Integración | `controller/*Test.java` | Flujo HTTP completo (`MockMvc`), incluyendo seguridad real (`JwtAuthFilter`), validaciones y manejo de errores |

---

## Docker

El proyecto incluye un `Dockerfile` con build multi-etapa (compila con Maven en una imagen, y corre con un JRE liviano en otra, para una imagen final más chica).

### Construir la imagen

```bash
docker build -t starwars-challenge .
```

### Correr el contenedor

```bash
docker run -d -p 8080:8080 --name starwars-app starwars-challenge
```

La aplicación queda disponible en `http://localhost:8080`, igual que corriéndola local sin Docker.

> ⚠️ Si corrés la app en Docker, recordá pasar `jwt.secret` como variable de entorno en vez de dejarla en `application.properties` (ver [Consideraciones para producción](#consideraciones-para-producción)).

---

## Estructura del proyecto

```
src/main/java/com/starwars/challenge/conexa/
├── client/              → SwapiClient (interface + impl), único cliente HTTP hacia SWAPI
├── config/              → WebClientConfig, CacheConfig, OpenApiConfig
├── controller/          → PersonController, FilmController, StarshipController,
│                          VehicleController, AuthController
├── dto/
│   ├── external/        → DTOs que mapean el JSON de SWAPI (person, film, starship, vehicle)
│   ├── request/         → Filtros y bodies de entrada (*FilterRequest, LoginRequest, RegisterRequest)
│   └── response/        → DTOs de salida propios (*Response, PagedResponse, AuthResponse)
├── entity/               → User (entidad JPA)
├── exception/            → GlobalExceptionHandler, ResourceNotFoundException,
│                          UsernameAlreadyExistsException, ErrorResponse
├── mapper/               → PersonMapper, FilmMapper, StarshipMapper, VehicleMapper
├── repository/           → IUserRepository (Spring Data JPA)
├── security/             → SecurityConfig, JwtService, JwtAuthFilter,
│                          JwtAuthenticationEntryPoint, CustomUserDetailsService
├── service/              → Interfaces (I*Service) + implementaciones (service/impl)
└── ChallengeApplication.java

src/test/java/com/starwars/challenge/conexa/
├── mapper/               → Tests unitarios de los 4 mappers
├── service/impl/          → Tests unitarios de los 4 services (con Mockito)
├── security/              → Test unitario de JwtService
└── controller/             → Tests de integración de los 5 controllers
```


