package com.example.finalproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.UI.theme.FinalProjectTheme
import com.example.finalproject.UI.*
import androidx.compose.material3.*

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
            FinalProjectTheme() {
                Greeting()
                RecordatorioScreen()
                //LoginScreen()
                //CalendarScreen()
                AiScreen()
                RegistroScreen()
                TareaScreen()
                //PerfilScreen()

            }
        }
    }
}


@Composable
fun Greeting(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
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
            ListaDeEtiquetas()

            Text(
                text = "Hoy tienes x tareas",
                color = Color.White,
                fontSize = 30.sp,
                fontFamily = InriaSerif,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(top = 16.dp)
            )

            Text(
                text = "Today´s Focus",
                color = Color(0xFFC7AF93),
                modifier = Modifier
                    .padding(top = 13.dp)
                    .padding(horizontal = 12.dp)
            )

            RectanguloCafe(textoTarea = "Preparar presentación")
            RectanguloNoCafe(textoTarea = "Estudiar para el examen")
        }
        ImagenDerechaTextoIzquierda()
        ParteAbajo()
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
fun RectanguloCafe(textoTarea: String) {
    Box(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth()
            .height(70.dp)
            .background(
                color = AccentColor32,
                shape = RoundedCornerShape(22.dp)
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(Color.Transparent, shape = CircleShape)
                    .border(width = 1.dp, color = Color.Black, shape = CircleShape)
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
fun RectanguloNoCafe(textoTarea: String) {
    Box(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth()
            .height(70.dp)
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
    ){
        Row(
            modifier = Modifier.padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically
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

            Text(
                text = textoTarea,
                modifier = Modifier.padding(start = 16.dp),
                color = Color.White,
                fontFamily = InriaSerif,
                fontSize = 20.sp
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
                    color = TextBeige3,
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

@Composable
fun ParteAbajo() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.calendar_month_24),
            contentDescription = "Calendario",
            tint = TextBeige3,
            modifier = Modifier.size(26.dp)
        )
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.cloud_24),
            contentDescription = "Clima",
            tint = TextBeige3,
            modifier = Modifier.size(26.dp)
        )

        Box(
            modifier = Modifier
                .size(50.dp)
                .border(1.dp, AccentBorder3, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.add_24),
                contentDescription = "Añadir",
                tint = TextBeige3,
                modifier = Modifier.size(24.dp)
            )
        }

        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.check_circle_24),
            contentDescription = "Completado",
            tint = TextBeige3,
            modifier = Modifier.size(26.dp)
        )
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.person_24),
            contentDescription = "Perfil",
            tint = TextBeige3,
            modifier = Modifier.size(26.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FinalProjectTheme(){
        Greeting()
    }
}