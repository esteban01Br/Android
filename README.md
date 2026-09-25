# 🔬 PréstamoLab CTMA

### Aplicación móvil Android para la gestión de préstamos de equipos de laboratorio

---

## 🏷️ Badges

![API](https://img.shields.io/badge/API-Android%2024%2B-green?logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0-purple?logo=kotlin)
![Version](https://img.shields.io/badge/Version-0.6.0-blue)
![Licencia](https://img.shields.io/badge/Licencia-Académica-blue)
![CI](https://img.shields.io/badge/CI-GitHub%20Actions-success)

---

## 📱 Descripción

**PréstamoLab CTMA** es una aplicación móvil Android desarrollada con Kotlin y Jetpack Compose para gestionar el catálogo de equipos de laboratorio y las solicitudes de préstamo, incluyendo el registro de devoluciones con evidencia fotográfica.

El proyecto sigue una arquitectura local-first: **Room** es la fuente canónica local, **DataStore** guarda las preferencias del usuario y **Retrofit/OkHttp** sincronizan con el servicio remoto. Incluye capacidades del dispositivo (Photo Picker, GPS y notificaciones), seguridad (HTTPS y token cifrado en Android Keystore) e integración continua con GitHub Actions.

La aplicación es parte de un proyecto académico para practicar desarrollo móvil Android, Scrum, prueba de software y arquitectura.

---

## ✨ Funcionalidades principales

* 📦 Visualización del catálogo de equipos con **filtro por categoría** persistido en DataStore.
* 🔍 Detalle de equipo e información de disponibilidad.
* 📝 Creación de solicitudes de préstamo (con validaciones y captura opcional de ubicación GPS).
* 📋 Consulta, cancelación y **registro de devolución con evidencia fotográfica** (Photo Picker).
* ☁️ Sincronización local-first con el servicio remoto (Retrofit/OkHttp + MockWebServer en pruebas).
* 🧭 Navegación con barra inferior (Equipos / Solicitudes).
* 💾 Persistencia local con Room (catálogo y solicitudes sobreviven al reinicio).
* 📱 Capacidades del dispositivo: **GPS**, **Photo Picker** y **notificaciones** de recordatorio.
* 🔒 Seguridad: HTTPS obligatorio, token cifrado en Android Keystore y permisos de mínimo privilegio.
* 🧠 Estados reactivos Loading / Content / Empty / Error con ViewModel y StateFlow.
* 🤖 CI con GitHub Actions (build, unit tests, lint).

---

## 🛠️ Tecnologías utilizadas

| Tecnología | Uso en el proyecto |
| ---------- | ------------------ |
| **Kotlin 2.0 / Corrutinas / Flow** | Lenguaje y programación asíncrona reactiva. |
| **Jetpack Compose + Material 3** | Interfaz de usuario declarativa. |
| **Navigation Compose** | Navegación entre pantallas. |
| **ViewModel + StateFlow** | Lógica de presentación y estado observable. |
| **Room 2.6.1 (KSP)** | Persistencia local (catálogo y solicitudes). |
| **DataStore Preferences** | Preferencias del usuario (filtro de categoría). |
| **Retrofit 2.11 + OkHttp 4.12** | Consumo de la API REST. |
| **Android Keystore (AES/GCM)** | Cifrado del token de sesión. |
| **JUnit 4 + coroutines-test + MockWebServer** | Pruebas unitarias e integración HTTP. |
| **GitHub Actions** | Integración continua. |

---

## 🧩 Modelo de datos

### Equipo
| Campo | Tipo | Descripción |
| ----- | ---- | ----------- |
| `id` | `Long` | Identificador del equipo. |
| `nombre` | `String` | Nombre del equipo. |
| `categoria` | `CategoriaEquipo` | COMPUTO, AUDIOVISUAL, REDES, ELECTRONICA u otra. |
| `estado` | `EstadoEquipo` | DISPONIBLE, RESERVADO o PRESTADO. |

### Solicitud de préstamo
| Campo | Tipo | Descripción |
| ----- | ---- | ----------- |
| `id` | `Long` | Identificador de la solicitud. |
| `equipoId` | `Long` | Equipo solicitado. |
| `ambienteDestino` / `proposito` | `String` | Destino y propósito del préstamo. |
| `duracionHoras` | `Int` | Duración solicitada (1–8 h). |
| `estado` | `EstadoSolicitud` | SOLICITADA, ENTREGADA, DEVUELTA o CANCELADA. |
| `fechaSolicitud` / `fechaLimiteDevolucion` | `Long` | Fechas (epoch millis). |
| `evidenciaUri` | `String?` | URI de la evidencia fotográfica. |
| `evidenciaEstado` | `EstadoEvidencia` | LOCAL, SUBIENDO, SINCRONIZADA o FALLIDA. |
| `latitud` / `longitud` | `Double?` | Ubicación GPS registrada al solicitar. |
| `sincronizado` | `Boolean` | Indicador de sincronización remota. |

---

## 📋 Reglas de negocio

1. El campo **destino** es obligatorio y no puede estar vacío.
2. El campo **propósito** debe contener entre **10 y 180 caracteres**.
3. La duración del préstamo debe estar entre **1 y 8 horas**, inclusive.
4. Solo un equipo **DISPONIBLE** puede solicitarse; la solicitud lo marca **RESERVADO**.
5. Un equipo ya solicitado/repetido no puede tener dos préstamos activos.
6. Solo una solicitud **ENTREGADA** puede registrar devolución (con o sin evidencia).
7. La devolución marca la solicitud **DEVUELTA** y el equipo **DISPONIBLE**.
8. La evidencia pasa por estados **LOCAL → SUBIENDO → SINCRONIZADA/FALLIDA**.
9. Los errores de red/HTTP no rompen el estado de la UI y se muestran como mensajes recuperables.

---

## 📁 Estructura del proyecto

```text
PrestamoLab-CTMA/
│
├── .github/workflows/
│   └── android-ci.yml              # CI: assembleDebug, testDebugUnitTest, lintDebug
├── app/src/
│   ├── main/java/com/example/prestamolab/
│   │   ├── MainActivity.kt
│   │   ├── PrestamoLabApplication.kt
│   │   ├── RecordatorioReceiver.kt
│   │   ├── di/AppContainer.kt
│   │   ├── model/                  # Equipo, SolicitudPrestamo, Estados
│   │   ├── data/
│   │   │   ├── local/              # Entities, DAOs, PrestamoDatabase, UserPreferencesStore
│   │   │   ├── remote/             # Dtos, Mappers, PrestamoApiService, NetworkModule
│   │   │   ├── repository/         # PrestamoRepository (+Room / InMemory)
│   │   │   └── security/TokenStore.kt
│   │   ├── ui/                     # PrestamoLabApp, screens/, theme/
│   │   ├── util/                   # UbicacionProvider, NotificadorRecordatorio
│   │   └── viewmodel/              # PrestamoViewModel (+Factory)
│   └── test/java/com/example/prestamolab/
│       └── ValidacionesTest.kt     # Pruebas de validaciones
└── docs/                           # Matrices, informes, backlogs, suites de pruebas
```

---

## ⚙️ Requisitos previos

* Android Studio (con SDK compatible, `compileSdk 35`).
* JDK 21 compatible con Gradle 8.9 y AGP 8.7.
* Dispositivo/emulador Android (API 24+), con GPS activo para probar la ubicación.
* Internet para descargar dependencias en la primera compilación.

---

## 🚀 Cómo compilar y ejecutar

```powershell
cd PrestamoLab-CTMA
.\gradlew.bat assembleDebug
```

Ejecutar desde Android Studio con el botón **Run ▶**. El APK de depuración queda en `app/build/outputs/apk/debug/`.

---

## 🔌 API REST y ambientes

La URL base se configura por ambiente mediante `BuildConfig.API_BASE_URL` (sin exponer secretos):

| BuildType | URL |
|-----------|-----|
| `debug` | `http://10.0.2.2:8080/api/v1/` (emulador → host local) |
| `release` | `https://api.prestamolab.example.com/api/v1/` (placeholder) |

La sincronización manual (botón en el catálogo) actualiza Room desde el servicio. Sin servicio disponible, la aplicación funciona con la semilla local (local-first). Más detalle en `docs/informe-tecnico.md`.

---

## 🧪 Pruebas

La estrategia prevista (los tests los ejecuta el equipo del proyecto):

* **Unitarias**: `.\gradlew.bat testDebugUnitTest`
* **Prueba concreta**: `.\gradlew.bat test --tests "*ValidacionesTest"`
* **Instrumentadas (dispositivo/emulador)**: `.\gradlew.bat connectedAndroidTest` (Room in-memory + recorrido UI Compose).

Reportes HTML: `app/build/reports/tests/testDebugUnitTest/index.html`.

---

## 🤖 Integración continua

`.github/workflows/android-ci.yml` ejecuta en cada push/PR:

1. `assembleDebug`
2. `testDebugUnitTest`
3. `lintDebug`

y publica el artefacto `app-debug.apk`.

---

## 📚 Documentación

La carpeta `docs/` contiene la documentación del proyecto:

* `matriz-trazabilidad.md` — trazabilidad requisitos → diseño → código → verificación.
* `informe-tecnico.md` — informe técnico de los incrementos v0.2.0 a v0.6.0.
* `product-backlog.md`, `sprint-backlog.md`, `sprint-review.md` — gestión de sprints.
* `matriz-riesgos.md`, `registro-defectos.md`, `suite-pruebas.md`, `bitacora-pruebas.md` — riesgos y pruebas.

---

## 📝 Notas

* La información se **persiste localmente** con Room/DataStore.
* La aplicación **sincroniza con una API REST** (Retrofit/OkHttp) de forma **local-first**.
* Solo se usa **HTTPS** en producción; token cifrado en Android Keystore.
* Permisos de **mínimo privilegio** solicitados en tiempo de ejecución (ubicación y notificaciones).
* Los datos son **sintéticos**, con fines académicos y de prueba.
* **Uso de IA**: la herramienta de desarrollo (opencode) asistió en la implementación de la capa de datos, la integración REST, las capacidades del dispositivo y la documentación; todo el código fue compilado, revisado y es modificable por el equipo.

---

<p align="center">
  <strong>PréstamoLab CTMA</strong><br>
  Proyecto académico de desarrollo de aplicaciones móviles Android.
</p> y Scrum
