package com.example.finalproject.UI

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.luhikawa.R

val BgDark3 = Color(0xFF1A1717)
val BgBeige3 = Color(0xFFC7AF93)
val TextBeige3 = Color(0xFFC7AF93)
val TextDark3 = Color(0xFF1A1717)
val AccentColor32 = Color(0xFFC7AF93)

class MainActivityIA : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BgDark3
                ) {
                    AiScreen()
                }
            }
        }
    }
}

@Composable
fun AiScreen(
    viewModel: ChatViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsState()
    var textoEntrada by remember { mutableStateOf("") }
    val estadoLista = rememberLazyListState()

    // Desplazamiento automático al mensaje más reciente
    LaunchedEffect(estado.mensajes.size) {
        if (estado.mensajes.isNotEmpty()) {
            estadoLista.animateScrollToItem(estado.mensajes.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark3)
            ,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            RectanguloConImagen2()
            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                state = estadoLista,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(estado.mensajes) { msg ->
                    if (msg.deUsuario) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Spacer(modifier = Modifier.width(40.dp))
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = BgBeige3)
                            ) {
                                Text(
                                    text = msg.mensaje,
                                    modifier = Modifier.padding(14.dp),
                                    style = TextStyle(
                                        fontFamily = FontFamily.Serif,
                                        fontSize = 14.sp,
                                        color = TextDark3
                                    )
                                )
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {

                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = BgBeige3)
                            ) {
                                Text(
                                    text = msg.mensaje,
                                    modifier = Modifier.padding(14.dp),
                                    style = TextStyle(
                                        fontFamily = FontFamily.Serif,
                                        fontSize = 14.sp,
                                        color = TextDark3
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(40.dp))
                        }
                    }
                }


                if (estado.cargando) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                color = TextBeige3,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BgBeige3)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = textoEntrada,
                        onValueChange = { textoEntrada = it },
                        placeholder = {
                            Text(
                                text = "Escribe un mensaje...",
                                style = TextStyle(
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 14.sp,
                                    color = TextDark3.copy(alpha = 0.7f)
                                )
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = TextDark3,
                            unfocusedTextColor = TextDark3
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.send_24),
                        contentDescription = "Enviar",
                        tint = TextDark3,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                if (textoEntrada.isNotBlank()) {
                                    viewModel.enviarMensaje(textoEntrada)
                                    textoEntrada = ""
                                }
                            }
                    )
                }
            }
        }
    }
}

@Composable
fun RectanguloConImagen2() {
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