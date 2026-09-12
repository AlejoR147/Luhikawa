package com.example.luhikawa

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore


val BgDark = Color(0xFF1A1717)
val BgBeige = Color(0xFFC7AF93)
val TextBeige = Color(0xFFC7AF93)
val TextDark = Color(0xFF1A1717)
val AccentBorder = Color(0xFFC7AF93)

val CustomFontFamily = FontFamily.Serif

class MainActivityH : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BgDarka
                ) {
                    val navController = rememberNavController()
                    RegistroScreen(navController = navController)
                }
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun RecordatorioScreen(navController: NavController, taskId: String? = null) {
    var reminderName by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf(0) }
    var selectedImportance by remember { mutableStateOf("Alta") }

    val context = LocalContext.current
    var selectedDate by remember { mutableStateOf("15 Oct 2026") }
    var selectedTime by remember { mutableStateOf("16:00") }

    var selectedCategory by remember { mutableStateOf("Trabajo") }
    var selectedFrequency by remember { mutableStateOf("Todos los días") }

    val db = FirebaseFirestore.getInstance()

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    LaunchedEffect(taskId) {
        if (!taskId.isNullOrEmpty()) {
            db.collection("tasks").document(taskId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        reminderName = document.getString("title") ?: ""
                        selectedCategory = document.getString("category") ?: "Trabajo"
                        selectedDate = document.getString("date") ?: "15 Oct 2026"
                        selectedTime = document.getString("time") ?: "16:00"
                        selectedImportance = document.getString("importance") ?: "Alta"
                        selectedIcon = (document.getLong("icon") ?: 0L).toInt()

                        if (document.contains("frequency")) {
                            selectedFrequency = document.getString("frequency") ?: "Todos los días"
                        }
                    }
                }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(BgDarka)) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            HeaderSection()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    NuevoRecordatorioHeader(isEditing = !taskId.isNullOrEmpty())

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Detalles",
                        style = TextStyle(
                            fontFamily = InriaSerif,
                            fontSize = 26.sp,
                            color = TextBeigea
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    InputLabel(text = "Nombre del recordatorio")
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextFieldCustom(
                        value = reminderName,
                        onValueChange = { reminderName = it },
                        placeholder = "Ej: Cita con el dentista"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    InputLabel(text = "Ícono")
                    Spacer(modifier = Modifier.height(6.dp))
                    IconSelector(
                        selectedIndex = selectedIcon,
                        onIconSelected = { selectedIcon = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    InputLabel(text = "Importancia")
                    Spacer(modifier = Modifier.height(6.dp))
                    ImportanceSelector(
                        selectedOption = selectedImportance,
                        onOptionSelected = { selectedImportance = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    InputLabel(text = "Fecha y Hora")
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showDatePicker = true }
                        ) {
                            DateTimeSelector(
                                icon = Icons.Outlined.CalendarMonth,
                                text = selectedDate,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showTimePicker = true }
                        ) {
                            DateTimeSelector(
                                icon = Icons.Default.Schedule,
                                text = selectedTime,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    InputLabel(text = "Clasificación")
                    Spacer(modifier = Modifier.height(6.dp))
                    IndexStyleCategorySelector(
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it }
                    )

                    if (selectedCategory == "Hábitos") {
                        Spacer(modifier = Modifier.height(16.dp))
                        InputLabel(text = "Frecuencia de repetición")
                        Spacer(modifier = Modifier.height(6.dp))
                        FrequencySelector(
                            selectedOption = selectedFrequency,
                            onOptionSelected = { selectedFrequency = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }

                Button(
                    onClick = {
                        if (reminderName.isNotBlank()) {
                            val dateParts = selectedDate.split(" ")
                            val monthMap = mapOf(
                                "Ene" to 1, "Feb" to 2, "Mar" to 3, "Abr" to 4, "May" to 5, "Jun" to 6,
                                "Jul" to 7, "Ago" to 8, "Sep" to 9, "Oct" to 10, "Nov" to 11, "Dic" to 12
                            )
                            val day = dateParts[0].toInt()
                            val month = monthMap[dateParts[1]] ?: 1
                            val year = dateParts[2].toInt()
                            val fechaIso = "%04d-%02d-%02d".format(year, month, day)

                            val taskMap = mutableMapOf<String, Any>(
                                "title" to reminderName,
                                "category" to if (selectedCategory == "Todo") "Tareas" else selectedCategory,
                                "date" to selectedDate,
                                "dueDate" to fechaIso,
                                "time" to selectedTime,
                                "importance" to selectedImportance,
                                "icon" to selectedIcon,
                                "completed" to false
                            )

                            if (selectedCategory == "Hábitos") {
                                taskMap["frequency"] = selectedFrequency
                            }

                            if (!taskId.isNullOrEmpty()) {
                                db.collection("tasks").document(taskId)
                                    .update(taskMap)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "¡Actualizado con éxito!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.popBackStack()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            context,
                                            "Error al actualizar: ${e.localizedMessage}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                            } else {
                                db.collection("tasks")
                                    .add(taskMap)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "¡Guardado con éxito!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.popBackStack()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            context,
                                            "Error al guardar: ${e.localizedMessage}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Por favor escribe un nombre",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BgBeigea,
                        contentColor = TextDarka
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (!taskId.isNullOrEmpty()) "ACTUALIZAR" else "GUARDAR",
                        style = TextStyle(
                            fontFamily = InriaSerif,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val fecha = java.time.Instant.ofEpochMilli(millis)
                                    .atZone(java.time.ZoneOffset.UTC)   // <-- cambio clave
                                    .toLocalDate()
                                val year = fecha.year
                                val month = fecha.monthValue
                                val day = fecha.dayOfMonth
                                val months = arrayOf(
                                    "Ene", "Feb", "Mar", "Abr", "May", "Jun",
                                    "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"
                                )
                                selectedDate = "$day ${months[month - 1]} $year"
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

        if (showTimePicker) {
            var selectedHour by remember { mutableStateOf(16) }
            var selectedMinute by remember { mutableStateOf(0) }

            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                containerColor = BgDarka,
                title = {
                    Text(
                        text = "Seleccionar Hora",
                        fontFamily = InriaSerif,
                        color = TextBeigea,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = String.format("%02d", if (selectedHour == 0) 23 else selectedHour - 1),
                                color = Color.Gray.copy(alpha = 0.4f),
                                fontSize = 22.sp,
                                modifier = Modifier
                                    .clickable { selectedHour = if (selectedHour == 0) 23 else selectedHour - 1 }
                                    .padding(8.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = String.format("%02d", selectedHour),
                                    color = Color.White,
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(text = "h", color = Color.Gray, fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = String.format("%02d", if (selectedHour == 23) 0 else selectedHour + 1),
                                color = Color.Gray.copy(alpha = 0.4f),
                                fontSize = 22.sp,
                                modifier = Modifier
                                    .clickable { selectedHour = if (selectedHour == 23) 0 else selectedHour + 1 }
                                    .padding(8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = ":", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(16.dp))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = String.format("%02d", if (selectedMinute == 0) 59 else selectedMinute - 1),
                                color = Color.Gray.copy(alpha = 0.4f),
                                fontSize = 22.sp,
                                modifier = Modifier
                                    .clickable { selectedMinute = if (selectedMinute == 0) 59 else selectedMinute - 1 }
                                    .padding(8.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = String.format("%02d", selectedMinute),
                                    color = Color.White,
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(text = "m", color = Color.Gray, fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = String.format("%02d", if (selectedMinute == 59) 0 else selectedMinute + 1),
                                color = Color.Gray.copy(alpha = 0.4f),
                                fontSize = 22.sp,
                                modifier = Modifier
                                    .clickable { selectedMinute = if (selectedMinute == 59) 0 else selectedMinute + 1 }
                                    .padding(8.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                            showTimePicker = false
                        }
                    ) {
                        Text("Aceptar", fontFamily = InriaSerif, color = BgBeigea, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showTimePicker = false }) {
                        Text("Cancelar", fontFamily = InriaSerif, color = Color.Gray)
                    }
                }
            )
        }
    }
}


fun getIconFromIndex(index: Int): ImageVector {
    return when (index) {
        0 -> Icons.Outlined.Notifications
        1 -> Icons.Default.FitnessCenter
        2 -> Icons.Default.Medication
        3 -> Icons.Default.WaterDrop
        4 -> Icons.Default.DirectionsCar
        else -> Icons.Outlined.Notifications
    }
}


@Composable
fun NuevoRecordatorioHeader(isEditing: Boolean = false) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BgBeigea)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isEditing) "EDITAR RECORDATORIO" else "NUEVO RECORDATORIO",
            style = TextStyle(
                fontFamily = InriaSerif,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextDarka
            )
        )
    }
}

@Composable
fun FrequencySelector(
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    val options = listOf("Todos los días", "Día de por medio", "Cada dos semanas")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                BorderStroke(1.dp, AccentBordera),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        options.forEach { text ->
            val isSelected = text == selectedOption
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (isSelected) BgBeigea else Color.Transparent,
                        shape = when (text) {
                            "Todos los días" -> RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                            "Cada dos semanas" -> RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp)
                            else -> RoundedCornerShape(0.dp)
                        }
                    )
                    .clickable { onOptionSelected(text) }
                    .padding(vertical = 10.dp, horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    style = TextStyle(
                        fontFamily = InriaSerif,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) BgDarka else TextBeigea
                    ),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .background(BgBeigea)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Menú",
            tint = TextDarka,
            modifier = Modifier.size(24.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.logolk),
            contentDescription = "Logo LK",
            modifier = Modifier
                .size(70.dp)
                .padding(end = 4.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun NuevoRecordatorioHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BgBeigea)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "NUEVO RECORDATORIO",
            style = TextStyle(
                fontFamily = InriaSerif,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextDarka
            )
        )
    }
}

@Composable
fun InputLabel(text: String) {
    Text(
        text = text,
        style = TextStyle(
            fontFamily = InriaSerif,
            fontSize = 16.sp,
            color = TextBeigea.copy(alpha = 0.8f)
        )
    )
}

@Composable
fun OutlinedTextFieldCustom(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                style = TextStyle(
                    fontFamily = InriaSerif,
                    color = Color.Gray
                )
            )
        },
        textStyle = TextStyle(
            fontFamily = InriaSerif,
            color = TextBeigea,
            fontSize = 18.sp
        ),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentBordera,
            unfocusedBorderColor = AccentBordera,
            cursorColor = BgBeigea
        ),
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Editar",
                tint = AccentBordera,
                modifier = Modifier.size(20.dp)
            )
        }
    )
}

@Composable
fun IconSelector(selectedIndex: Int, onIconSelected: (Int) -> Unit) {
    val iconsList = listOf(
        Icons.Outlined.Notifications,
        Icons.Default.FitnessCenter,
        Icons.Default.Medication,
        Icons.Default.WaterDrop,
        Icons.Default.DirectionsCar
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        iconsList.forEachIndexed { index, icon ->
            val isSelected = index == selectedIndex
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) BgBeigea else Color.Transparent)
                    .border(
                        BorderStroke(1.dp, AccentBordera),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onIconSelected(index) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Icono $index",
                    tint = if (isSelected) BgDarka else TextBeigea,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

@Composable
fun ImportanceSelector(
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    val options = listOf("Baja", "Media", "Alta")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                BorderStroke(1.dp, AccentBordera),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        options.forEach { text ->
            val isSelected = text == selectedOption
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (isSelected) BgBeigea else Color.Transparent,
                        shape = when (text) {
                            "Baja" -> RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                            "Alta" -> RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp)
                            else -> RoundedCornerShape(0.dp)
                        }
                    )
                    .clickable { onOptionSelected(text) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    style = TextStyle(
                        fontFamily = InriaSerif,
                        fontSize = 16.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) BgDarka else TextBeigea
                    )
                )
            }
        }
    }
}

@Composable
fun DateTimeSelector(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(50.dp)
            .border(
                BorderStroke(1.dp, AccentBordera),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AccentBordera,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = TextStyle(
                fontFamily = InriaSerif,
                fontSize = 15.sp,
                color = TextBeigea
            )
        )
    }
}

@Composable
fun IndexStyleCategorySelector(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf("Trabajo", "Estudio", "Hábitos", "Personal", "Lista de deseos", "Cumpleaños")

    androidx.compose.foundation.lazy.LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(categories.size) { index ->
            val category = categories[index]
            val isSelected = category == selectedCategory
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) BgBeigea else Color.Transparent)
                    .border(
                        BorderStroke(1.dp, AccentBordera),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable { onCategorySelected(category) }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category,
                    style = TextStyle(
                        fontFamily = InriaSerif,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) BgDarka else TextBeigea
                    )
                )
            }
        }
    }
}

@Composable
fun BottomNavBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgDarka)
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(icon = Icons.Default.DateRange, contentDescription = "Calendario")
        BottomNavItem(icon = Icons.Outlined.Cloud, contentDescription = "Clima")
        Box(
            modifier = Modifier
                .size(60.dp)
                .border(BorderStroke(1.dp, Color.White), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(Color.White.copy(alpha = 0.1f), CircleShape)
            )
        }
        BottomNavItem(icon = Icons.Default.CheckCircle, contentDescription = "Agenda")
        BottomNavItem(icon = Icons.Outlined.Person, contentDescription = "Perfil")
    }
}

@Composable
fun BottomNavItem(icon: ImageVector, contentDescription: String) {
    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = Color.White.copy(alpha = 0.7f),
        modifier = Modifier.size(28.dp)
    )
}