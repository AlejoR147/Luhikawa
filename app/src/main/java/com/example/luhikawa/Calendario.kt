package com.example.luhikawa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.getValue
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.material3.Scaffold
import com.example.finalproject.UI.RectanguloConImagen2


val BgDarke = Color(0xFF1A1717)
val BgBeigee = Color(0xFFC7AF93)
val TextBeigee = Color(0xFFC7AF93)
val TextDarkee = Color(0xFF1A1717)
val AccentBordere = Color(0xFFC7AF93)
val AccentColor32 = Color(0xFFC7AF93)

val CustomFontFamilye = FontFamily.Serif

class MainActivityCalendar : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "calendario"
                    ) {
                        composable(route = "calendario") {
                            CalendarScreen(navController = navController)
                        }


                        composable(route = "perfil") {
                            PerfilScreen(navController = navController)
                        }

                        composable(
                            route = "recordatorio?taskId={taskId}",
                            arguments = listOf(
                                navArgument("taskId") {
                                    type = NavType.StringType
                                    nullable = true
                                    defaultValue = null
                                }
                            )
                        ) { backStackEntry ->
                            val taskId = backStackEntry.arguments?.getString("taskId")
                            RecordatorioScreen(navController = navController, taskId = taskId)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CalendarScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    var tareas by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var fechaSeleccionada by remember { mutableStateOf<LocalDate?>(null) }

    val hoy = LocalDate.now()
    var anio by remember { mutableStateOf(hoy.year) }
    var mes by remember { mutableStateOf(hoy.monthValue) }

    val cargarTareas = {
        db.collection("tasks").get()
            .addOnSuccessListener { result ->
                tareas = result.documents.mapNotNull { doc ->
                    doc.data?.let { it + ("taskId" to doc.id) }
                }
            }
    }

    LaunchedEffect(Unit) { cargarTareas() }

    // Filtrar solo tareas PENDIENTES (no completadas)
    val tareasPendientes = remember(tareas) {
        tareas.filter { tarea ->
            val completada = tarea["completed"] as? Boolean ?: false
            !completada
        }
    }

    // Agrupar tareas pendientes por fecha
    val tareasPorFecha = remember(tareasPendientes) {
        tareasPendientes.groupBy { tarea ->
            val fechaStr = tarea["dueDate"] as? String ?: return@groupBy null
            try {
                LocalDate.parse(fechaStr)
            } catch (e: Exception) {
                null
            }
        }.filterKeys { it != null }
    }

    fun colorParaFecha(fecha: LocalDate): Color? {
        val tareasDelDia = tareasPorFecha[fecha] ?: return null
        val maxImportante = tareasDelDia.any { it["important"] as? Boolean == true }
        return if (maxImportante) Color(0xFF5D4037) else Color(0xFFC7AF93)
    }

    val mesNombre = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )[mes - 1]

    val primerDia = LocalDate.of(anio, mes, 1)
    val diasEnMes = primerDia.lengthOfMonth()
    val offset = primerDia.dayOfWeek.value % 7
    val celdas = List(offset) { null } + (1..diasEnMes).map { it }
    val celdasCompletas = celdas + List((7 - (celdas.size % 7)) % 7) { null }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDarka)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Cabecera que ocupa todo el ancho de la pantalla
            RectanguloConImagen2()

            // Contenido con padding horizontal
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Selector de mes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Mes anterior",
                        tint = TextBeigea,
                        modifier = Modifier.clickable {
                            if (mes == 1) {
                                mes = 12
                                anio -= 1
                            } else {
                                mes -= 1
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        "$mesNombre $anio",
                        style = TextStyle(fontFamily = InriaSerif, fontSize = 22.sp, color = TextBeigea)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = "Mes siguiente",
                        tint = TextBeigea,
                        modifier = Modifier.clickable {
                            if (mes == 12) {
                                mes = 1
                                anio += 1
                            } else {
                                mes += 1
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Días de la semana
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("D", "L", "M", "M", "J", "V", "S").forEach { d ->
                        Text(
                            text = d,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            color = TextBeigea.copy(alpha = 0.5f),
                            fontFamily = InriaSerif
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Cuadrícula del mes
                celdasCompletas.chunked(7).forEach { semana ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        semana.forEach { dia ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(CircleShape)
                                    .background(
                                        if (dia != null) {
                                            val fecha = LocalDate.of(anio, mes, dia)
                                            colorParaFecha(fecha) ?: Color.Transparent
                                        } else Color.Transparent
                                    )
                                    .clickable(enabled = dia != null) {
                                        if (dia != null) {
                                            fechaSeleccionada = LocalDate.of(anio, mes, dia)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (dia != null) {
                                    Text(
                                        text = dia.toString(),
                                        color = if (colorParaFecha(LocalDate.of(anio, mes, dia)) != null)
                                            Color.White else TextBeigea,
                                        fontFamily = InriaSerif,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Tareas asignadas",
                    style = TextStyle(fontFamily = InriaSerif, fontSize = 20.sp, color = TextBeigea, fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            val tareasDelDia = fechaSeleccionada?.let { tareasPorFecha[it] } ?: emptyList()

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (tareasDelDia.isEmpty()) {
                    item {
                        Text(
                            text = if (fechaSeleccionada == null)
                                "Toca una fecha para ver sus tareas"
                            else "No hay tareas para este día",
                            color = TextBeigea.copy(alpha = 0.5f),
                            fontFamily = InriaSerif,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                    }
                } else {
                    items(tareasDelDia.size) { index ->
                        val tarea = tareasDelDia[index]
                        val titulo = tarea["title"] as? String ?: "Sin título"
                        val fechaLegible = tarea["date"] as? String ?: ""
                        val horaFormateada = tarea["time"] as? String ?: ""
                        val importante = tarea["important"] as? Boolean == true
                        val iconIndex = (tarea["icon"] as? Long)?.toInt() ?: 0
                        val id = tarea["taskId"] as? String

                        val infoTiempo = buildString {
                            if (fechaLegible.isNotEmpty()) append(fechaLegible)
                            if (fechaLegible.isNotEmpty() && horaFormateada.isNotEmpty()) append(" • ")
                            if (horaFormateada.isNotEmpty()) append(horaFormateada)
                        }
                        val textoConFecha =
                            if (infoTiempo.isNotEmpty()) "$titulo - $infoTiempo" else titulo

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .padding(vertical = 4.dp)
                        ) {
                            SwipeableTaskItem(
                                textoTarea = textoConFecha,
                                fechaTarea = tarea["date"] as? String,
                                isCafe = (index % 2 == 0),
                                iconIndex = iconIndex,
                                onCircleClick = {
                                    if (id != null) {
                                        db.collection("tasks").document(id)
                                            .update("completed", true)
                                            .addOnSuccessListener { cargarTareas() }
                                    }
                                },
                                onImportanteClick = {
                                    if (id != null) {
                                        db.collection("tasks").document(id)
                                            .update("important", !importante)
                                            .addOnSuccessListener { cargarTareas() }
                                    }
                                },
                                onFechaClick = {},
                                onBasuraClick = {},
                                onEliminar = {
                                    if (id != null) {
                                        db.collection("tasks").document(id)
                                            .delete()
                                            .addOnSuccessListener { cargarTareas() }
                                    }
                                },
                                onClick = {}
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun SwipeableTaskItem(
    textoTarea: String,
    fechaTarea: String? = null,
    isCafe: Boolean,
    iconIndex: Int,
    onCircleClick: () -> Unit,
    onImportanteClick: () -> Unit,
    onFechaClick: () -> Unit,
    onBasuraClick: () -> Unit,
    onEliminar: () -> Unit,
    onClick: () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    val maxRevealWidth = 270.dp
    val density = LocalDensity.current
    val maxRevealWidthPx = with(density) { maxRevealWidth.toPx() }

    val animatedOffset by animateFloatAsState(
        targetValue = offsetX,
        label = "swipeOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .height(60.dp)
            .clip(RoundedCornerShape(22.dp))
    ) {

        Row(
            modifier = Modifier
                .matchParentSize()
                .background(
                    color = if (isCafe) BackgroundColor else AccentColor32,
                    shape = RoundedCornerShape(22.dp)
                ),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val contentColor = if (isCafe) Color.White else Color.Black

            Box(
                modifier = Modifier
                    .width(90.dp)
                    .fillMaxHeight()
                    .clickable {
                        onImportanteClick()
                        offsetX = 0f
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Importante",
                        tint = contentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Importante",
                        fontSize = 9.sp,
                        color = contentColor,
                        fontFamily = InriaSerif
                    )
                }
            }

            Box(
                modifier = Modifier
                    .width(90.dp)
                    .fillMaxHeight()
                    .clickable {
                        onFechaClick()
                        offsetX = 0f
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Fecha",
                        tint = contentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Fecha",
                        fontSize = 9.sp,
                        color = contentColor,
                        fontFamily = InriaSerif
                    )
                }
            }

            Box(
                modifier = Modifier
                    .width(90.dp)
                    .fillMaxHeight()
                    .clickable {
                        onBasuraClick()
                        offsetX = 0f
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Basura",
                        tint = contentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Basura",
                        fontSize = 9.sp,
                        color = contentColor,
                        fontFamily = InriaSerif
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .matchParentSize()
                .offset { IntOffset(animatedOffset.toInt(), 0) }
                .background(
                    color = if (isCafe) AccentColor32 else BackgroundColor,
                    shape = RoundedCornerShape(22.dp)
                )
                .then(
                    if (!isCafe) Modifier.border(2.dp, AccentColor32, RoundedCornerShape(22.dp))
                    else Modifier
                )
                .clickable { onClick() }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            val newOffset = offsetX + dragAmount
                            offsetX = newOffset.coerceIn(-maxRevealWidthPx, 0f)
                        },
                        onDragEnd = {
                            offsetX = if (offsetX < -maxRevealWidthPx / 2f) -maxRevealWidthPx else 0f
                        }
                    )
                },
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(Color.Transparent, shape = CircleShape)
                        .border(
                            width = 1.dp,
                            color = if (isCafe) Color.Black else Color.White,
                            shape = CircleShape
                        )
                        .clickable { onCircleClick() }
                )

                Spacer(modifier = Modifier.width(12.dp))

                Icon(
                    imageVector = getIconFromIndex(iconIndex),
                    contentDescription = null,
                    tint = if (isCafe) Color.Black else Color.White,
                    modifier = Modifier.size(22.dp)
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    Text(
                        text = textoTarea,
                        color = if (isCafe) Color.Black else Color.White,
                        fontFamily = InriaSerif,
                        fontSize = 18.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (!fechaTarea.isNullOrEmpty()) {
                        Text(
                            text = fechaTarea,
                            color = if (isCafe) Color.Black.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.6f),
                            fontFamily = InriaSerif,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}