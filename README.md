📱 Mi Formación CTMA
Aplicación Android para el seguimiento de actividades de formación del programa CTMA - SENA.

La aplicación permite:

Registrar actividades de formación.

Editar actividades existentes.

Eliminar actividades.

Consultar actividades almacenadas localmente.

Sincronizar información con un backend.

Gestionar errores de conexión y respuestas de la API.

Mantener una copia local de los datos mediante Room.

🛠️ Tecnologías utilizadas
Aplicación Android
Kotlin

Jetpack Compose

Material 3

Room

Retrofit

OkHttp

Gson

ViewModel

StateFlow

Backend
Python

FastAPI

SQLAlchemy

PostgreSQL

📋 Requisitos
Antes de ejecutar el proyecto, asegúrate de tener instalado:

Android Studio Ladybug o superior

JDK 21

Android SDK 36

Min SDK 24 (Android 7.0)

Para ejecutar el backend:

Python 3

PostgreSQL 18 o compatible

🚀 Configuración
1. Clonar el repositorio
git clone <URL_DEL_REPOSITORIO>
cd MiFormacionCTMA

Abre posteriormente el proyecto en Android Studio.

2. Sincronizar Gradle
En Android Studio:

File → Sync Project with Gradle Files

Espera a que finalice la sincronización y verifica que no existan errores de compilación.

3. Configurar la URL de la API
La URL del backend se configura en:

app/src/main/java/com/esteban/miformacionctma/data/remote/RetrofitConfig.kt

Emulador de Android
Para utilizar el backend ejecutándose en el computador desde el emulador:

const val BASE_URL = "http://10.0.2.2:8000/"

10.0.2.2 representa la máquina local del computador desde el emulador de Android.

Dispositivo físico
Si ejecutas la aplicación desde un dispositivo Android conectado a la misma red que el computador:

const val BASE_URL = "http://192.168.1.100:8000/"

Reemplaza 192.168.1.100 por la dirección IP local de tu computador.

🗄️ Base de datos
El proyecto utiliza dos bases de datos:

1. Base de datos local — Room
La aplicación utiliza Room para almacenar los datos localmente en el dispositivo.

Ubicación
app/src/main/java/com/esteban/miformacionctma/data/ActividadDatabase.kt

Tabla
actividades

Entidad
Actividad.kt

La base de datos se crea automáticamente la primera vez que se ejecuta la aplicación.

Nombre del archivo
miformacion_ctma.db

Modelo
@Entity(tableName = "actividades")
data class Actividad(
    val id: Int,           // Identificador
    val titulo: String,
    val descripcion: String,
    val fecha: String,     // Ejemplo: "2026-09-10"
    val prioridad: String, // "Baja" | "Media" | "Alta"
    val progreso: Int      // 0..100
)

2. Base de datos remota — PostgreSQL
El backend utiliza PostgreSQL como base de datos remota.

Script
database/actividades_postgres.sql

Backend
backend/

Tecnologías utilizadas:

FastAPI

SQLAlchemy

PostgreSQL

🔧 Configuración de la base de datos
Paso 1 — Preparar PostgreSQL
Inicia el servicio de PostgreSQL.

En Windows puedes encontrarlo en:

Services → postgresql-x64-18

Después ejecuta el script de creación de la base de datos:

psql -U postgres -f database/actividades_postgres.sql

El script crea:

Base de datos: miformacion_ctma

Tabla: actividades

5 registros de prueba

🐍 Configuración del Backend
Ingresa a la carpeta del backend:

cd backend

Crear entorno virtual
python -m venv .venv

Windows
.venv\Scripts\activate

Instalar dependencias
pip install fastapi uvicorn sqlalchemy psycopg2-binary python-dotenv pydantic

Configurar variables de entorno
Copia el archivo de ejemplo:

copy .env.example .env

Después edita .env con las credenciales correspondientes de PostgreSQL.

Ejemplo:

DATABASE_URL=postgresql://postgres:TU_PASSWORD@localhost:5432/miformacion_ctma

Utiliza las variables y credenciales definidas por el proyecto en .env.example.

▶️ Iniciar el Backend
Desde la carpeta backend/:

uvicorn app.main:app --reload --port 8000

La API estará disponible en:

http://localhost:8000

La documentación interactiva de FastAPI estará disponible en:

http://localhost:8000/docs

📱 Conectar la aplicación Android
Verifica que RetrofitConfig.kt tenga configurada la URL correcta.

Para el emulador:

const val BASE_URL = "http://10.0.2.2:8000/"

El AndroidManifest.xml ya incluye:

Permiso INTERNET

usesCleartextTraffic="true" para permitir HTTP durante el desarrollo local

▶️ Ejecutar el proyecto
El orden recomendado para ejecutar todo el sistema es:

Iniciar PostgreSQL.

Ejecutar el backend con Uvicorn.

Verificar la API en /docs.

Abrir el proyecto en Android Studio.

Sincronizar Gradle.

Ejecutar la aplicación en un emulador o dispositivo físico.

Al abrirse la aplicación, se realiza la sincronización con la API.

🌐 Endpoints de la API
Método	Ruta	Descripción
GET	/actividades/	Lista todas las actividades
GET	/actividades/{id}	Obtiene una actividad
POST	/actividades/	Crea una actividad
PUT	/actividades/{id}	Actualiza una actividad
DELETE	/actividades/{id}	Elimina una actividad

Códigos de respuesta principales
200 — Operación exitosa

201 — Recurso creado

204 — Recurso eliminado

4xx — Error relacionado con la solicitud

5xx — Error del servidor

📁 Estructura del proyecto
app/src/main/java/com/esteban/miformacionctma/
│
├── Actividad.kt
├── MainActivity.kt
│
├── data/
│   ├── DatError.kt
│   ├── ActividadDao.kt
│   ├── ActividadDatabase.kt
│   ├── ActividadRepository.kt
│   │
│   ├── dto/
│   │   └── ActividadDTO.kt
│   │
│   ├── mapper/
│   │   └── ActividadMapper.kt
│   │
│   └── remote/
│       ├── ActividadesApi.kt
│       ├── BearerTokenInterceptor.kt
│       ├── TokenProvider.kt
│       ├── OkHttpConfig.kt
│       ├── RetrofitConfig.kt
│       └── RemoteDatasource.kt
│
└── ui/
    ├── screens/
    │   ├── ActividadesScreen.kt
    │   ├── FormularioActividad.kt
    │   ├── FormularioActividadUiState.kt
    │   ├── RefreshUiState.kt
    │   ├── ActividadViewModel.kt
    │   └── HomeScreen.kt
    │
    └── theme/
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt

🏗️ Arquitectura
La aplicación utiliza una arquitectura en capas, separando la interfaz de usuario, la lógica de presentación, el repositorio y las fuentes de datos.

┌─────────────────────┐
│     UI (Compose)    │
│                     │
│  ActividadesScreen  │
└──────────┬──────────┘
           │
           │ StateFlow
           ▼
┌─────────────────────┐
│      ViewModel      │
│                     │
│ ActividadViewModel  │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│     Repository      │
│                     │
│  syncFromRemote()   │
└───────┬───────┬─────┘
        │       │
        ▼       ▼
┌───────────┐ ┌──────────────┐
│  Remote   │ │ Local (Room) │
│Datasource │ │     DAO      │
└─────┬─────┘ └──────┬───────┘
      │               │
      ▼               ▼
┌───────────┐ ┌────────────────┐
│ Retrofit  │ │ Actividad DB   │
│    API    │ │   SQLite/Room  │
└───────────┘ └────────────────┘

🔄 Flujo de sincronización
El proceso de sincronización funciona de la siguiente manera:

La interfaz observa los datos de Room mediante StateFlow<List<Actividad>>.

syncFromRemote() solicita los datos al backend mediante RemoteDatasource.

RemoteDatasource procesa la respuesta de Retrofit.

Los errores son clasificados mediante Throwable.classify().

Los datos recibidos se almacenan localmente.

La actualización de la base de datos se realiza de forma transaccional mediante:

db.withTransaction {
    dao.replaceRemoteSnapshot()
}

La interfaz recibe automáticamente los cambios mediante Room.

El estado de sincronización se maneja independientemente mediante RefreshUiState.

⚠️ Manejo de errores
El proyecto utiliza DatError para representar diferentes tipos de errores:

sealed interface DatError {
    NoNetwork
    Timeout
    Unauthorized
    Server
    Empty
    Unknown(message)
}

Tipos de errores
Error	Descripción
NoNetwork	No existe conexión de red
Timeout	La solicitud superó el tiempo de espera
Unauthorized	Token inválido o expirado
Server	Error del servidor, normalmente HTTP 5xx
Empty	Respuesta vacía
Unknown	Error no clasificado

📦 Dependencias principales
Librería	Versión	Uso
Jetpack Compose BOM	2024.09.00	Interfaz de usuario
Room	2.8.4	Base de datos local
Retrofit	2.11.0	Cliente HTTP
OkHttp	4.12.0	Capa de red e interceptores
Gson	2.11.0	Serialización JSON
Lifecycle	2.6.1	ViewModel y StateFlow

🌿 Organización del trabajo por ramas
El desarrollo del proyecto se realizó mediante ramas independientes de Git.

Cada integrante del equipo trabajó en una rama diferente y tuvo responsabilidades específicas dentro del proyecto.

Esto permitió separar el trabajo de cada integrante y posteriormente integrar los cambios en la rama principal.

Integrantes y ramas
Integrante	Rama	Trabajo realizado
Esteban Bedoya Rojo	rama-esteban	Desarrollo de las funcionalidades asignadas
Frank Junior Benitez Mosquera	rama-frank	Desarrollo de las funcionalidades asignadas
Hector Steven Cuesta	rama-hector	Desarrollo de las funcionalidades asignadas

Las ramas y responsabilidades pueden consultarse directamente en el historial de Git del repositorio.

Flujo de trabajo
                    ┌── Rama Esteban ────────┐
                    │                        │
                    ├── Rama Frank ──────────┤
                    │                        ▼
Rama principal ─────┼──────────────────► Integración
                    │                        │
                    └── Rama Hector ────────┘
                                             │
                                             ▼
                                      Rama principal

Cada integrante realizó sus cambios en su propia rama para evitar trabajar directamente sobre la rama principal.

📚 Documentación
La documentación del proyecto se encuentra en:

documentos/

Incluye:

Historias de Usuario.docx

Matriz de Trazabilidad.docx

Las evidencias de las actividades se encuentran en:

evidencias/

📂 Directorios del repositorio
Carpeta	Contenido
app/	Código fuente Android con Compose y Room
backend/	API desarrollada con FastAPI
database/	Scripts SQL para PostgreSQL
documentos/	Documentación del proyecto
evidencias/	Evidencias de las actividades

👥 Equipo de desarrollo
Programa: CTMA - SENA

Integrantes
Esteban Bedoya Rojo

Frank Junior Benitez Mosquera

Hector Steven Cuesta

Cada integrante desarrolló diferentes funcionalidades y componentes del proyecto utilizando ramas independientes de Git.

📌 Resumen
Mi Formación CTMA es una aplicación Android desarrollada para facilitar el seguimiento de actividades de formación.

El proyecto integra:

Android
   │
   ├── Jetpack Compose
   ├── ViewModel
   ├── StateFlow
   └── Room
          │
          ▼
       Retrofit
          │
          ▼
       FastAPI
          │
          ▼
      PostgreSQL

La combinación de almacenamiento local y sincronización remota permite mantener los datos de las actividades disponibles en la aplicación y sincronizados con el backend.
