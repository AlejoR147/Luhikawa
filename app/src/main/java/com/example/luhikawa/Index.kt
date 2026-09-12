package com.example.luhikawa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.luhikawa.ui.theme.LuhikawaTheme
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


val BackgroundColor = Color(0xFF1A1717)
val AccentColor3 = Color(0xFFC7AF93)

val InriaSerif = FontFamily(
    Font(R.font.inriaserif_regular)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LuhikawaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "greeting"
                    ) {
                        composable(route = "greeting") {
                            Greeting(navController = navController)
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

                        composable(route = "perfil") {
                            PerfilScreen(navController = navController)
                        }

                        // CalendarScreen()
                        // AiScreen()
                    }
                }
            }
        }
    }

    @OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
    @Composable
    fun Greeting(navController: NavController, modifier: Modifier = Modifier) {
        var categoriaSeleccionada by remember { mutableStateOf("Trabajo") }

        val categorias = listOf(
            "Trabajo",
            "Estudio",
            "Hábitos",
            "Personal",
            "Lista de deseos",
            "Cumpleaños"
        )

        val db = FirebaseFirestore.getInstance()
        var listaDeTareas by remember {
            mutableStateOf<List<Pair<String, Map<String, Any>>>>(
                emptyList()
            )
        }

        var showDatePicker by remember { mutableStateOf(false) }
        var selectedTaskIdForDate by remember { mutableStateOf<String?>(null) }
        var showDeleteDialog by remember { mutableStateOf(false) }
        var taskIdToDelete by remember { mutableStateOf<String?>(null) }

        var showRestoreDialog by remember { mutableStateOf(false) }
        var selectedTaskIdForRestore by remember { mutableStateOf<String?>(null) }

        val cargarTareas = {
            val query = if (categoriaSeleccionada == "Trabajo") {
                db.collection("tasks")
            } else {
                db.collection("tasks").whereEqualTo("category", categoriaSeleccionada)
            }

            query.get()
                .addOnSuccessListener { result ->
                    listaDeTareas = result.documents.mapNotNull { doc ->
                        doc.data?.let { doc.id to it }
                    }
                }
        }

        LaunchedEffect(categoriaSeleccionada) {
            cargarTareas()
        }

        val tareasImportantes = listaDeTareas.filter { (_, tarea) ->
            tarea["important"] as? Boolean ?: false
        }

        val tareasActivas = listaDeTareas.filter { (_, tarea) ->
            val completada = tarea["completed"] as? Boolean ?: false
            val importante = tarea["important"] as? Boolean ?: false
            !completada && !importante
        }

        val tareasCompletadas = listaDeTareas.filter { (_, tarea) ->
            val completada = tarea["completed"] as? Boolean ?: false
            completada
        }

        val tareasPendientesTotales = listaDeTareas.filter { (_, tarea) ->
            val completada = tarea["completed"] as? Boolean ?: false
            !completada
        }

        Box(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundColor)
                    .padding(bottom = 8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    RectanguloConImagen()

                    Row(
                        modifier = Modifier
                            .padding(top = 15.dp)
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categorias.forEach { categoria ->
                            EtiquetaTexto(
                                texto = categoria,
                                isSelected = (categoria == categoriaSeleccionada),
                                onClick = { categoriaSeleccionada = categoria }
                            )
                        }
                    }

                    Text(
                        text = "Tienes ${tareasPendientesTotales.size} tareas pendientes",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontFamily = InriaSerif,
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .padding(top = 16.dp)
                    )

                    Text(
                        text = "Today´s Focus",
                        color = Color(0xFFC7AF93),
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .padding(horizontal = 12.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (tareasImportantes.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Importantes (${tareasImportantes.size})",
                                    color = Color(0xFFC7AF93),
                                    fontSize = 18.sp,
                                    fontFamily = InriaSerif,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }

                            items(tareasImportantes.size) { index ->
                                val (id, tarea) = tareasImportantes[index]
                                val tituloBase = tarea["title"] as? String ?: "Sin título"
                                val dueDateMillis = tarea["dueDate"] as? Long
                                val fechaFormateada = formatearFecha(dueDateMillis)
                                val horaFormateada = tarea["time"] as? String ?: ""
                                val esImportante = true
                                val iconIndex = (tarea["icon"] as? Long)?.toInt() ?: 0

                                val infoTiempo = buildString {
                                    if (fechaFormateada.isNotEmpty()) append(fechaFormateada)
                                    if (fechaFormateada.isNotEmpty() && horaFormateada.isNotEmpty()) append(" • ")
                                    if (horaFormateada.isNotEmpty()) append(horaFormateada)
                                }

                                val textoConFecha =
                                    if (infoTiempo.isNotEmpty()) "$tituloBase - $infoTiempo" else tituloBase

                                val onMarcarCompletada: () -> Unit = {
                                    db.collection("tasks").document(id)
                                        .update("completed", true)
                                        .addOnSuccessListener { cargarTareas() }
                                }

                                val onMarcarImportante: () -> Unit = {
                                    db.collection("tasks").document(id)
                                        .update("important", !esImportante)
                                        .addOnSuccessListener { cargarTareas() }
                                }

                                val onActualizarFecha: () -> Unit = {
                                    selectedTaskIdForDate = id
                                    showDatePicker = true
                                }

                                val onEnviarABasura: () -> Unit = {
                                    taskIdToDelete = id
                                    showDeleteDialog = true
                                }

                                val onEliminar: () -> Unit = {
                                    db.collection("tasks").document(id)
                                        .delete()
                                        .addOnSuccessListener { cargarTareas() }
                                }

                                val onEditarClick: () -> Unit = {
                                    navController.navigate("recordatorio?taskId=$id")
                                }

                                SwipeableTaskItem(
                                    textoTarea = textoConFecha,
                                    isCafe = (index % 2 == 0),
                                    iconIndex = iconIndex,
                                    onCircleClick = onMarcarCompletada,
                                    onImportanteClick = onMarcarImportante,
                                    onFechaClick = onActualizarFecha,
                                    onBasuraClick = onEnviarABasura,
                                    onEliminar = onEliminar,
                                    onClick = onEditarClick
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }

                        items(tareasActivas.size) { index ->
                            val (id, tarea) = tareasActivas[index]
                            val tituloBase = tarea["title"] as? String ?: "Sin título"
                            val dueDateMillis = tarea["dueDate"] as? Long
                            val fechaFormateada = formatearFecha(dueDateMillis)
                            val horaFormateada = tarea["time"] as? String ?: ""
                            val esImportante = tarea["important"] as? Boolean ?: false
                            val iconIndex = (tarea["icon"] as? Long)?.toInt() ?: 0   // <-- CORREGIDO

                            val infoTiempo = buildString {
                                if (fechaFormateada.isNotEmpty()) append(fechaFormateada)
                                if (fechaFormateada.isNotEmpty() && horaFormateada.isNotEmpty()) append(" • ")
                                if (horaFormateada.isNotEmpty()) append(horaFormateada)
                            }

                            val textoConFecha =
                                if (infoTiempo.isNotEmpty()) "$tituloBase - $infoTiempo" else tituloBase

                            val onMarcarCompletada: () -> Unit = {
                                db.collection("tasks").document(id)
                                    .update("completed", true)
                                    .addOnSuccessListener { cargarTareas() }
                            }

                            val onMarcarImportante: () -> Unit = {
                                db.collection("tasks").document(id)
                                    .update("important", !esImportante)
                                    .addOnSuccessListener { cargarTareas() }
                            }

                            val onActualizarFecha: () -> Unit = {
                                selectedTaskIdForDate = id
                                showDatePicker = true
                            }

                            val onEnviarABasura: () -> Unit = {
                                taskIdToDelete = id
                                showDeleteDialog = true
                            }

                            val onEliminar: () -> Unit = {
                                db.collection("tasks").document(id)
                                    .delete()
                                    .addOnSuccessListener { cargarTareas() }
                            }

                            val onEditarClick: () -> Unit = {
                                navController.navigate("recordatorio?taskId=$id")
                            }

                            SwipeableTaskItem(
                                textoTarea = textoConFecha,
                                isCafe = (index % 2 == 0),
                                iconIndex = iconIndex,   // <-- CORREGIDO
                                onCircleClick = onMarcarCompletada,
                                onImportanteClick = onMarcarImportante,
                                onFechaClick = onActualizarFecha,
                                onBasuraClick = onEnviarABasura,
                                onEliminar = onEliminar,
                                onClick = onEditarClick
                            )
                        }

                        if (tareasCompletadas.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Completadas (${tareasCompletadas.size})",
                                    color = Color(0xFFC7AF93),
                                    fontSize = 18.sp,
                                    fontFamily = InriaSerif,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            items(tareasCompletadas.size) { index ->
                                val (id, tarea) = tareasCompletadas[index]
                                val titulo = tarea["title"] as? String ?: "Sin título"

                                val onRestaurarClick: () -> Unit = {
                                    selectedTaskIdForRestore = id
                                    showRestoreDialog = true
                                }

                                val onEliminar: () -> Unit = {
                                    db.collection("tasks").document(id)
                                        .delete()
                                        .addOnSuccessListener { cargarTareas() }
                                }

                                RectanguloCompletadoPapelera(
                                    textoTarea = titulo,
                                    onDeleteClick = onEliminar,
                                    onClick = onRestaurarClick
                                )
                            }
                        }
                    }
                }

                ImagenDerechaTextoIzquierda()
                BottomNavBarPerfil(navController = navController)
            }

            if (showDatePicker) {
                val datePickerState = rememberDatePickerState()
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    selectedTaskIdForDate?.let { taskId ->
                                        db.collection("tasks").document(taskId)
                                            .update("dueDate", millis)
                                            .addOnSuccessListener { cargarTareas() }
                                    }
                                }
                                showDatePicker = false
                            }
                        ) {
                            Text("Aceptar", fontFamily = InriaSerif)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Cancelar", fontFamily = InriaSerif)
                        }
                    },
                    colors = DatePickerDefaults.colors(containerColor = BackgroundColor)
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = {
                        Text(text = "Eliminar tarea", fontFamily = InriaSerif, color = Color.White)
                    },
                    text = {
                        Text(
                            text = "¿Estás segura de que la quieres eliminar?",
                            fontFamily = InriaSerif,
                            color = Color.White
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                taskIdToDelete?.let { id ->
                                    db.collection("tasks").document(id)
                                        .delete()
                                        .addOnSuccessListener { cargarTareas() }
                                }
                                showDeleteDialog = false
                                taskIdToDelete = null
                            }
                        ) {
                            Text("Sí, eliminar", color = Color(0xFFE57373), fontFamily = InriaSerif)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showDeleteDialog = false
                                taskIdToDelete = null
                            }
                        ) {
                            Text("Cancelar", fontFamily = InriaSerif, color = Color.White)
                        }
                    },
                    modifier = Modifier.background(
                        BackgroundColor,
                        shape = RoundedCornerShape(28.dp)
                    )
                )
            }

            if (showRestoreDialog) {
                AlertDialog(
                    onDismissRequest = { showRestoreDialog = false },
                    title = {
                        Text(text = "Restaurar tarea", fontFamily = InriaSerif, color = Color.White)
                    },
                    text = {
                        Text(
                            text = "¿Quieres restablecer esta tarea a su estado anterior?",
                            fontFamily = InriaSerif,
                            color = Color.White
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                selectedTaskIdForRestore?.let { id ->
                                    db.collection("tasks").document(id)
                                        .update("completed", false)
                                        .addOnSuccessListener { cargarTareas() }
                                }
                                showRestoreDialog = false
                                selectedTaskIdForRestore = null
                            }
                        ) {
                            Text(
                                "Sí, restablecer",
                                color = Color(0xFFC7AF93),
                                fontFamily = InriaSerif
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showRestoreDialog = false
                                selectedTaskIdForRestore = null
                            }
                        ) {
                            Text("Cancelar", fontFamily = InriaSerif, color = Color.White)
                        }
                    },
                    modifier = Modifier.background(
                        BackgroundColor,
                        shape = RoundedCornerShape(28.dp)
                    )
                )
            }
        }
    }


    @Composable
    fun SwipeableTaskItem(
        textoTarea: String,
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

                    Text(
                        text = textoTarea,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp),
                        color = if (isCafe) Color.Black else Color.White,
                        fontFamily = InriaSerif,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }

    @Composable
    fun RectanguloConImagen() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp)
                .background(AccentColor32),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logolk),
                contentDescription = "Logo LK",
                modifier = Modifier
                    .size(85.dp)
                    .align(Alignment.CenterEnd),
                contentScale = ContentScale.Fit
            )
        }
    }

    @Composable
    fun ListaDeEtiquetas() {
        val categorias = listOf(
            "Trabajo",
            "Estudio",
            "Hábitos",
            "Personal",
            "Lista de deseos",
            "Cumpleaños"
        )
        var seleccionada by remember { mutableStateOf("Trabajo") }

        Row(
            modifier = Modifier
                .padding(top = 15.dp)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categorias.forEach { categoria ->
                EtiquetaTexto(
                    texto = categoria,
                    isSelected = (categoria == seleccionada),
                    onClick = { seleccionada = categoria }
                )
            }
        }
    }

    @Composable
    fun EtiquetaTexto(
        texto: String,
        isSelected: Boolean = false,
        onClick: () -> Unit = {}
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()

        val backgroundColor by animateColorAsState(
            targetValue = if (isSelected || isPressed) AccentColor32 else BackgroundColor,
            label = "fondoAnimado"
        )
        val textColor by animateColorAsState(
            targetValue = if (isSelected || isPressed) BackgroundColor else AccentColor32,
            label = "textoAnimado"
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(backgroundColor)
                .border(
                    width = 2.dp,
                    color = AccentColor32,
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = texto,
                color = textColor,
                fontSize = 14.sp,
                fontFamily = InriaSerif
            )
        }
    }

    @Composable
    fun RectanguloCafe(textoTarea: String, onCircleClick: () -> Unit) {
        Box(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .fillMaxWidth()
                .height(60.dp)
                .background(
                    color = AccentColor32,
                    shape = RoundedCornerShape(22.dp)
                ),
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
                        .border(width = 1.dp, color = Color.Black, shape = CircleShape)
                        .clickable { onCircleClick() }
                )

                Text(
                    text = textoTarea,
                    modifier = Modifier.padding(start = 16.dp),
                    color = Color.Black,
                    fontFamily = InriaSerif,
                    fontSize = 18.sp
                )
            }
        }
    }


    @Composable
    fun RectanguloNoCafe(textoTarea: String, onCircleClick: () -> Unit) {
        Box(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .fillMaxWidth()
                .height(60.dp)
                .background(
                    color = BackgroundColor,
                    shape = RoundedCornerShape(22.dp)
                )
                .border(
                    width = 2.dp,
                    color = AccentColor32,
                    shape = RoundedCornerShape(22.dp)
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onCircleClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(
                        modifier = Modifier.size(36.dp)
                    ) {
                        drawCircle(
                            color = AccentColor32,
                            radius = size.minDimension / 2f,
                            style = Stroke(width = 2f)
                        )
                    }
                }

                Text(
                    text = textoTarea,
                    modifier = Modifier.padding(start = 16.dp),
                    color = Color.White,
                    fontFamily = InriaSerif,
                    fontSize = 18.sp
                )
            }
        }
    }

    @Composable
    fun ImagenDerechaTextoIzquierda() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 35.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.width(180.dp)
            ) {
                Text(
                    text = "Tómate tu tiempo.\nEl descanso también es productivo",
                    style = TextStyle(
                        fontFamily = InriaSerif,
                        fontSize = 22.sp,
                        color = TextBeigea,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 22.sp
                    )
                )
            }

            Image(
                painter = painterResource(id = R.drawable.gato),
                contentDescription = "Gato descansando",
                modifier = Modifier.size(150.dp),
                contentScale = ContentScale.Fit
            )
        }
    }

    fun formatearFecha(millis: Long?): String {
        if (millis == null) return ""
        val formatter = SimpleDateFormat("d 'de' MMMM", Locale("es", "ES")).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        return formatter.format(Date(millis))
    }

    @Composable
    fun RectanguloCompletadoPapelera(
        textoTarea: String,
        onDeleteClick: () -> Unit,
        onClick: () -> Unit
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .height(60.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(BackgroundColor)
                .border(1.dp, Color(0xFFC7AF93), RoundedCornerShape(22.dp))
                .clickable { onClick() },
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = textoTarea,
                    color = Color.Gray,
                    fontFamily = InriaSerif,
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color(0xFFE57373)
                    )
                }
            }
        }
    }

    @Composable
    fun ParteAbajo(navController: NavController) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Calendario",
                tint = TextBeigea,
                modifier = Modifier
                    .size(26.dp)
                    .clickable { /* Ruta al calendario si aplica */ }
            )
            Icon(
                imageVector = Icons.Default.SmartToy,
                contentDescription = "IA",
                tint = TextBeigea,
                modifier = Modifier
                    .size(26.dp)
                    .clickable { /* Ruta a la IA */ }
            )

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .border(1.dp, AccentBordera, CircleShape)
                    .clickable {
                        navController.navigate("recordatorio")
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Añadir",
                    tint = TextBeigea,
                    modifier = Modifier.size(24.dp)
                )
            }

            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Completado",
                tint = TextBeigea,
                modifier = Modifier.size(26.dp)
            )

            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Perfil",
                tint = TextBeigea,
                modifier = Modifier
                    .size(26.dp)
                    .clickable {
                        navController.navigate("perfil")
                    }
            )
        }
    }
}

@Composable
fun ImportantTaskCard(
    taskId: String,
    title: String,
    time: String,
    iconIndex: Int,
    isCompleted: Boolean,
    onTaskCheckedChange: (Boolean) -> Unit
) {
    val db = FirebaseFirestore.getInstance()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = BgDarka),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(BgBeigea.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getIconFromIndex(iconIndex),
                        contentDescription = null,
                        tint = BgBeigea,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = InriaSerif
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = time,
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
            }

            IconButton(
                onClick = {
                    val newState = !isCompleted
                    db.collection("tasks").document(taskId)
                        .update("completed", newState)
                        .addOnSuccessListener {
                            onTaskCheckedChange(newState)
                        }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Completar tarea",
                    tint = if (isCompleted) Color(0xFF4CAF50) else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
