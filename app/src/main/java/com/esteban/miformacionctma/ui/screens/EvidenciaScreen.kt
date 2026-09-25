package com.esteban.miformacionctma.ui.screens

import android.content.ActivityNotFoundException
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.esteban.miformacionctma.data.evidencia.EvidenciaEstados
import com.esteban.miformacionctma.data.evidencia.validateImage
import com.esteban.miformacionctma.data.local.entity.ActividadEntity
import com.esteban.miformacionctma.data.local.entity.EvidenciaEntity
import com.esteban.miformacionctma.repository.EvidenciaRepository
import com.esteban.miformacionctma.util.RecordatorioPreferencias
import com.esteban.miformacionctma.util.eliminarArchivoTemporalSiVacio
import com.esteban.miformacionctma.util.nuevoUriEvidencia
import com.esteban.miformacionctma.util.persistirAccesoLectura

@Composable
fun ElegirEvidenciaButton(onSelected: (Uri?) -> Unit) {
    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> onSelected(uri) }
    )
    Button(
        onClick = {
            picker.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Elegir imagen")
    }
}

@Composable
fun CapturarEvidenciaButton(
    crearUri: () -> Uri,
    onCaptured: (uri: Uri?, ok: Boolean) -> Unit,
    onError: (String) -> Unit = {}
) {
    val context = LocalContext.current
    var uriActual by remember { mutableStateOf<Uri?>(null) }
    val camera = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { ok -> onCaptured(uriActual, ok) }
    Button(
        onClick = {
            val uri = crearUri()
            uriActual = uri
            try {
                camera.launch(uri)
            } catch (error: ActivityNotFoundException) {
                eliminarArchivoTemporalSiVacio(context, uri)
                onError("No se encontró una aplicación de cámara en el dispositivo.")
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Tomar foto")
    }
}

@Composable
fun VistaPreviaEvidencia(uri: Uri?, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val bitmap by produceState<ImageBitmap?>(initialValue = null, uri) {
        value = uri?.let {
            runCatching {
                context.contentResolver.openInputStream(it)?.use { entrada ->
                    BitmapFactory.decodeStream(entrada)?.asImageBitmap()
                }
            }.getOrNull()
        }
    }
    bitmap?.let {
        Image(
            bitmap = it,
            contentDescription = "Vista previa de la evidencia",
            modifier = modifier.fillMaxWidth(),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun ReminderPermission(
    onGranted: () -> Unit,
    onDenied: () -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> if (granted) onGranted() else onDenied() }
    Button(
        onClick = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            } else {
                onGranted()
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Activar recordatorios")
    }
}

@Composable
fun EvidenciaScreen(
    actividades: List<ActividadEntity>,
    repository: EvidenciaRepository,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel = remember(repository) {
        EvidenciaViewModel(repository) { uri ->
            validateImage(context.contentResolver, uri)
        }
    }

    val uiState = viewModel.uiState
    val evidencias by viewModel.observarEvidencias()
        .collectAsState(initial = emptyList())

    var errorCamara by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "📷 Evidencia fotográfica",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Selecciona la actividad que deseas evidenciar:",
            style = MaterialTheme.typography.bodyMedium
        )

        actividades.forEach { actividad ->
            OutlinedButton(
                onClick = { viewModel.seleccionarActividad(actividad.id) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("${actividad.titulo}${if (viewModel.actividadSeleccionada == actividad.id) "  ✓" else ""}")
            }
        }

        if (viewModel.actividadSeleccionada == null) {
            Text("Registra primero una actividad para poder adjuntar evidencia.")
            return@Column
        }

        Spacer(modifier = Modifier.height(4.dp))

        ElegirEvidenciaButton(onSelected = { uri ->
            uri?.let {
                persistirAccesoLectura(context, it)
                viewModel.alSeleccionar(it)
            }
        })

        CapturarEvidenciaButton(
            crearUri = { nuevoUriEvidencia(context) },
            onCaptured = { uri, ok ->
                if (ok && uri != null) {
                    errorCamara = null
                    viewModel.alSeleccionar(uri)
                } else {
                    uri?.let { eliminarArchivoTemporalSiVacio(context, it) }
                    viewModel.alCancelarCaptura()
                }
            },
            onError = { errorCamara = it }
        )

        errorCamara?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        VistaPreviaEvidencia(uri = uiState.uri)

        uiState.metadatos?.let { meta ->
            Text(
                text = "Validada: ${meta.mimeType} · ${meta.sizeBytes} bytes",
                style = MaterialTheme.typography.bodySmall
            )
        }

        val actividadActual = viewModel.actividadSeleccionada

        Button(
            onClick = {
                actividadActual?.let(viewModel::registrar)
            },
            enabled = actividadActual != null && uiState.uri != null && uiState.metadatos != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar evidencia (LOCAL)")
        }

        uiState.mensaje?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        EvidenciaLista(
            evidencias = evidencias,
            onSubir = viewModel::subir,
            onEliminar = viewModel::eliminar
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "🔔 Recordar vencimientos",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        var recordatoriosActivados by remember {
            mutableStateOf(RecordatorioPreferencias.activados(context))
        }

        Text(
            text = if (recordatoriosActivados) {
                "Recordatorios activados. Permiso disponible."
            } else {
                "El permiso se solicita al activar; negarlo no bloquea la app."
            },
            style = MaterialTheme.typography.bodySmall
        )

        ReminderPermission(
            onGranted = {
                RecordatorioPreferencias.guardar(context, true)
                recordatoriosActivados = true
            },
            onDenied = {
                RecordatorioPreferencias.guardar(context, false)
                recordatoriosActivados = false
            }
        )
    }
}

@Composable
private fun EvidenciaLista(
    evidencias: List<EvidenciaEntity>,
    onSubir: (String) -> Unit,
    onEliminar: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Evidencias de esta actividad:",
            style = MaterialTheme.typography.titleMedium
        )

        if (evidencias.isEmpty()) {
            Text("No hay evidencias para esta actividad.")
        }

        evidencias.forEach { evidencia ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "• ${
                            when (evidencia.estado) {
                                EvidenciaEstados.LOCAL -> "LOCAL (pendiente)"
                                EvidenciaEstados.SUBIENDO -> "SUBIENDO…"
                                EvidenciaEstados.SINCRONIZADA -> "SINCRONIZADA"
                                EvidenciaEstados.FALLIDA -> "FALLIDA"
                                else -> evidencia.estado
                            }
                        }",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "${evidencia.mimeType} · ${evidencia.sizeBytes} bytes",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Column {
                    Button(
                        onClick = { onSubir(evidencia.id) },
                        enabled = evidencia.estado != EvidenciaEstados.SUBIENDO
                    ) {
                        Text("Subir")
                    }
                    OutlinedButton(
                        onClick = { onEliminar(evidencia.id) },
                        enabled = evidencia.estado != EvidenciaEstados.SUBIENDO
                    ) {
                        Text("Eliminar")
                    }
                }
            }
        }
    }
}