package com.example.finalproject.UI

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luhikawa.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class MensajeChat(
    val mensaje: String,
    val deUsuario: Boolean
)
data class EstadoChat(
    val mensajes: List<MensajeChat> = listOf(
        MensajeChat("¡Hola! Soy Luhi, tu asistente personal. ¿En qué puedo ayudarte hoy?", deUsuario = false)
    ),
    val cargando: Boolean = false
)

class ChatViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val claveApi = BuildConfig.GEMINI_API_KEY

    private val modeloGemini = GenerativeModel(
        modelName = "gemini-3.6-flash",
        apiKey = claveApi,
        systemInstruction = content {
            text(
                "Te llamas Luhi. Eres la asistente inteligente oficial de la aplicación Luhikawa.\n" +
                "Tu objetivo es ayudar al usuario a organizar sus tareas, hábitos y actividades diarias.\n\n" +
                "INSTRUCCIONES PARA EJECUTAR ACCIONES CRUD EN LAS TAREAS DE FIRESTORE:\n" +
                "- Si el usuario te pide CREAR o AGREGAR una nueva tarea, responde confirmando amablemente e incluye al final de tu mensaje este comando:\n" +
                "  ACCION_CREAR: {\"title\": \"Nombre de la tarea\", \"category\": \"Personal\"}\n" +
                "  (Categorías válidas: Trabajo, Estudio, Hábitos, Personal, Lista de deseos, Cumpleaños)\n\n" +
                "- Si el usuario te pide COMPLETAR o MARCAR COMO HECHA una tarea, responde confirmando e incluye al final:\n" +
                "  ACCION_COMPLETAR: {\"title\": \"Nombre de la tarea\"}\n\n" +
                "- Si el usuario te pide ELIMINAR o BORRAR una tarea, responde confirmando e incluye al final:\n" +
                "  ACCION_ELIMINAR: {\"title\": \"Nombre de la tarea\"}\n\n" +
                "- Si el usuario solo pregunta o conversa, responde normalmente sin incluir comandos de acción."
            )
        }
    )

    private val sesionChat = modeloGemini.startChat()

    private val _estado = MutableStateFlow(EstadoChat())
    val estado: StateFlow<EstadoChat> = _estado.asStateFlow()

    fun enviarMensaje(textoUsuario: String) {
        val textoLimpio = textoUsuario.trim()
        if (textoLimpio.isEmpty() || _estado.value.cargando) return

        if (claveApi.isBlank()) {
            _estado.value = _estado.value.copy(
                mensajes = _estado.value.mensajes + MensajeChat(
                    "Error: luhi esta perdido, intentalo mas tarde.",
                    deUsuario = false
                )
            )
            return
        }

        val mensajeUsuario = MensajeChat(textoLimpio, deUsuario = true)

        _estado.value = _estado.value.copy(
            mensajes = _estado.value.mensajes + mensajeUsuario,
            cargando = true
        )

        viewModelScope.launch {
            try {
                val resumenTareas = obtenerResumenTareasFirestore()

                val promptConContexto = if (resumenTareas.isNotBlank()) {
                    "[Estado actual de las tareas:\n$resumenTareas]\n\nMensaje del usuario: $textoLimpio"
                } else {
                    textoLimpio
                }

                val respuesta = sesionChat.sendMessage(promptConContexto)
                val textoRespuestaOriginal = respuesta.text ?: "No pude obtener una respuesta."

                val textoParaMostrar = procesarComandosCrud(textoRespuestaOriginal)

                _estado.value = _estado.value.copy(
                    mensajes = _estado.value.mensajes + MensajeChat(textoParaMostrar, deUsuario = false),
                    cargando = false
                )
            } catch (e: Exception) {
                _estado.value = _estado.value.copy(
                    mensajes = _estado.value.mensajes + MensajeChat(
                        "Error al conectar con Luhi}",
                        deUsuario = false
                    ),
                    cargando = false
                )
            }
        }
    }

    private suspend fun procesarComandosCrud(textoOriginal: String): String {
        var textoResultado = textoOriginal

        // 1. Crear Tarea
        if (textoResultado.contains("ACCION_CREAR:")) {
            val titulo = extraerValor(textoResultado, "title")
            val categoria = extraerValor(textoResultado, "category").ifBlank { "Personal" }
            if (titulo.isNotBlank()) {
                crearTareaFirestore(titulo, categoria)
            }
            textoResultado = textoResultado.substringBefore("ACCION_CREAR:").trim()
        }

        // 2. Completar Tarea
        if (textoResultado.contains("ACCION_COMPLETAR:")) {
            val titulo = extraerValor(textoResultado, "title")
            if (titulo.isNotBlank()) {
                completarTareaFirestore(titulo)
            }
            textoResultado = textoResultado.substringBefore("ACCION_COMPLETAR:").trim()
        }

        // 3. Eliminar Tarea
        if (textoResultado.contains("ACCION_ELIMINAR:")) {
            val titulo = extraerValor(textoResultado, "title")
            if (titulo.isNotBlank()) {
                eliminarTareaFirestore(titulo)
            }
            textoResultado = textoResultado.substringBefore("ACCION_ELIMINAR:").trim()
        }

        return textoResultado
    }

    private fun extraerValor(texto: String, clave: String): String {
        return try {
            val patron = "\"$clave\"\\s*:\\s*\"([^\"]+)\"".toRegex(RegexOption.IGNORE_CASE)
            patron.find(texto)?.groupValues?.get(1) ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    private suspend fun crearTareaFirestore(titulo: String, categoria: String) {
        try {
            val nuevaTarea = mapOf(
                "title" to titulo,
                "category" to categoria,
                "completed" to false,
                "important" to false,
                "description" to "Creada por Luhi IA",
                "dueDate" to ""
            )
            db.collection("tasks").add(nuevaTarea).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun completarTareaFirestore(titulo: String) {
        try {
            val snapshot = db.collection("tasks").get().await()
            for (doc in snapshot.documents) {
                val t = doc.getString("title") ?: doc.getString("name") ?: ""
                if (t.equals(titulo, ignoreCase = true) || t.contains(titulo, ignoreCase = true)) {
                    doc.reference.update("completed", true).await()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun eliminarTareaFirestore(titulo: String) {
        try {
            val snapshot = db.collection("tasks").get().await()
            for (doc in snapshot.documents) {
                val t = doc.getString("title") ?: doc.getString("name") ?: ""
                if (t.equals(titulo, ignoreCase = true) || t.contains(titulo, ignoreCase = true)) {
                    doc.reference.delete().await()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun obtenerResumenTareasFirestore(): String {
        return try {
            val snapshot = db.collection("tasks").get().await()
            if (snapshot.isEmpty) return "No hay tareas registradas actualmente en tu lista."

            snapshot.documents.mapIndexed { index, doc ->
                val titulo = doc.getString("title") ?: doc.getString("name") ?: "Sin título"
                val categoria = doc.getString("category") ?: "General"
                val completada = doc.getBoolean("completed") ?: false
                val importante = doc.getBoolean("important") ?: false

                "${index + 1}. $titulo [Categoría: $categoria, Completada: $completada, Importante: $importante]"
            }.joinToString("\n")
        } catch (e: Exception) {
            "No pude consultar las tareas."
        }
    }
}
