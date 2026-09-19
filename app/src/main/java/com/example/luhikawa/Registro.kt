package com.example.luhikawa

import android.R.attr.clickable
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import androidx.compose.runtime.rememberCoroutineScope
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import java.security.SecureRandom
import android.util.Base64
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.credentials.CustomCredential
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions


class MainActivityRegistro : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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

@Composable
fun RegistroScreen(navController: NavController) {

    var usuario by remember { mutableStateOf("") }
    var nombreCompleto by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = CredentialManager.create(context)


    val auth = Firebase.auth
    val db = Firebase.firestore

    val spacingXS = 20.dp
    val spacingS = 50.dp
    val spacingM = 16.dp
    val spacingL = 24.dp
    val spacingXL = 30.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDarka)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(spacingS))

        Image(
            painter = painterResource(id = R.drawable.logolkb),
            contentDescription = "Logo LK",
            modifier = Modifier.size(60.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(spacingXS))

        Image(
            painter = painterResource(id = R.drawable.titlebeige),
            contentDescription = "LUHIKAWA",
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .wrapContentHeight(),
            contentScale = ContentScale.FillWidth,
            colorFilter = ColorFilter.tint(Color(0xFFC7AF93))
        )

        Spacer(modifier = Modifier.height(spacingXL))

        OutlinedTextField(
            value = usuario,
            onValueChange = { usuario = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Usuario", style = TextStyle(fontFamily = InriaSerif, color = TextBeigea.copy(alpha = 0.6f), fontSize = 18.sp))
            },
            textStyle = TextStyle(fontFamily = InriaSerif, color = TextBeigea, fontSize = 18.sp),
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentBordera,
                unfocusedBorderColor = AccentBordera,
                cursorColor = BgBeigea
            ),
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = "Icono Usuario", tint = TextBeigea.copy(alpha = 0.7f))
            }
        )

        Spacer(modifier = Modifier.height(spacingM))

        OutlinedTextField(
            value = nombreCompleto,
            onValueChange = { nombreCompleto = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Nombre Completo", style = TextStyle(fontFamily = InriaSerif, color = TextBeigea.copy(alpha = 0.6f), fontSize = 18.sp))
            },
            textStyle = TextStyle(fontFamily = InriaSerif, color = TextBeigea, fontSize = 18.sp),
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentBordera,
                unfocusedBorderColor = AccentBordera,
                cursorColor = BgBeigea
            ),
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = "Icono Nombre", tint = TextBeigea.copy(alpha = 0.7f))
            }
        )

        Spacer(modifier = Modifier.height(spacingM))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Correo electrónico", style = TextStyle(fontFamily = InriaSerif, color = TextBeigea.copy(alpha = 0.6f), fontSize = 18.sp))
            },
            textStyle = TextStyle(fontFamily = InriaSerif, color = TextBeigea, fontSize = 18.sp),
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentBordera,
                unfocusedBorderColor = AccentBordera,
                cursorColor = BgBeigea
            ),
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = "Icono Email", tint = TextBeigea.copy(alpha = 0.7f))
            }
        )

        Spacer(modifier = Modifier.height(spacingM))

        var passwordVisible by remember { mutableStateOf(false) }

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Contraseña", style = TextStyle(fontFamily = InriaSerif, color = TextBeigea.copy(alpha = 0.6f), fontSize = 18.sp))
            },
            textStyle = TextStyle(fontFamily = InriaSerif, color = TextBeigea, fontSize = 18.sp),
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentBordera,
                unfocusedBorderColor = AccentBordera,
                cursorColor = BgBeigea
            ),
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = "Icono Contraseña", tint = TextBeigea.copy(alpha = 0.7f))
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Mostrar/Ocultar contraseña", tint = TextBeigea.copy(alpha = 0.7f))
                }
            }
        )

        Spacer(modifier = Modifier.height(spacingM))

        OutlinedTextField(
            value = confirmarContrasena,
            onValueChange = { confirmarContrasena = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Confirmar Contraseña", style = TextStyle(fontFamily = InriaSerif, color = TextBeigea.copy(alpha = 0.6f), fontSize = 18.sp))
            },
            textStyle = TextStyle(fontFamily = InriaSerif, color = TextBeigea, fontSize = 18.sp),
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentBordera,
                unfocusedBorderColor = AccentBordera,
                cursorColor = BgBeigea
            ),
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = "Icono Confirmar Contraseña", tint = TextBeigea.copy(alpha = 0.7f))
            },

            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),

            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Mostrar/Ocultar confirmar contraseña", tint = TextBeigea.copy(alpha = 0.7f))
                }
            }
        )

        Spacer(modifier = Modifier.height(spacingL))

        Button(
            onClick = {
                if (email.isBlank() || contrasena.isBlank() || usuario.isBlank() || nombreCompleto.isBlank()) {
                    Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (contrasena != confirmarContrasena) {
                    Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                auth.createUserWithEmailAndPassword(email, contrasena)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid ?: ""

                            val userMap = hashMapOf(
                                "uid" to userId,
                                "usuario" to usuario,
                                "nombreCompleto" to nombreCompleto,
                                "email" to email
                            )

                            db.collection("users").document(userId)
                                .set(userMap)
                                .addOnSuccessListener {

                                    Toast.makeText(context, "¡Bienvenida a Luhikawa, $usuario!", Toast.LENGTH_SHORT).show()

                                    navController.navigate("greeting") {
                                        popUpTo("registro") { inclusive = true }
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(context, "Error al guardar datos: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                }

                        } else {
                            android.util.Log.e(
                                "RegistroAuth",
                                "Fallo en Auth: ${task.exception?.localizedMessage}",
                                task.exception
                            )
                            Toast.makeText(context, "Error de registro: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                    }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BgBeigea, contentColor = TextDarka),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text("Registrarse", style = TextStyle(fontFamily = InriaSerif, fontSize = 18.sp, fontWeight = FontWeight.Bold))
        }

        Spacer(modifier = Modifier.height(spacingM))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(BgDarka, shape = CircleShape)
                    .clickable {
                        coroutineScope.launch {
                            try {
                                val secureRandom = SecureRandom()
                                val bytes = ByteArray(64)
                                secureRandom.nextBytes(bytes)
                                val nonce = Base64.encodeToString(bytes, Base64.NO_WRAP)

                                val googleIdOption = GetGoogleIdOption.Builder()
                                    .setServerClientId("550323438631-7ju4fallk6fvg6vce4s1vbdtfguvb4tp.apps.googleusercontent.com")
                                    .setFilterByAuthorizedAccounts(false)
                                    .setNonce(nonce)
                                    .build()

                                val request = GetCredentialRequest.Builder()
                                    .addCredentialOption(googleIdOption)
                                    .build()

                                val result = credentialManager.getCredential(
                                    context = context,
                                    request = request
                                )

                                handleGoogleCredentialResponse(result, auth, db, context, navController)

                            } catch (e: Exception) {
                                Toast.makeText(context, "Error de Google: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Registrarse con Google",
                    modifier = Modifier.size(32.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Spacer(modifier = Modifier.height(spacingM))

        Text(
            text = "¿Ya tienes una cuenta?",
            style = TextStyle(fontFamily = InriaSerif, fontSize = 18.sp, color = TextBeigea.copy(alpha = 0.8f), textAlign = TextAlign.Center)
        )

        Spacer(modifier = Modifier.height(spacingXS))

        Text(
            text = "Inicia sesión",
            style = TextStyle(
                fontFamily = InriaSerif, fontSize = 18.sp, color = TextBeigea,
                textDecoration = TextDecoration.Underline, textAlign = TextAlign.Center
            ),
            modifier = Modifier.clickable {
                navController.navigate("login") {
                    popUpTo("registro") { inclusive = true }
                }
            }
        )
    }
}

private fun handleGoogleCredentialResponse(
    result: GetCredentialResponse,
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    context: Context,
    navController: NavController
) {
    val credential = result.credential
    if (credential is CustomCredential &&
        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
    ) {
        try {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val googleIdToken = googleIdTokenCredential.idToken

            val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)

            auth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = auth.currentUser
                        val userId = firebaseUser?.uid ?: ""
                        val email = firebaseUser?.email ?: ""
                        val nombre = firebaseUser?.displayName ?: "Usuario Google"

                        val userMap = hashMapOf(
                            "uid" to userId,
                            "usuario" to nombre,
                            "nombreCompleto" to nombre,
                            "email" to email
                        )

                        db.collection("users").document(userId)
                            .set(userMap, SetOptions.merge())
                            .addOnSuccessListener {
                                // MENSAJE DE BIENVENIDA Y NAVEGACIÓN AL INDEX
                                Toast.makeText(context, "¡Bienvenida de vuelta, $nombre!", Toast.LENGTH_SHORT).show()
                                navController.navigate("greeting") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Error al guardar en Firestore: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                            }
                    } else {
                        Toast.makeText(context, "Fallo en Firebase: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }
        } catch (e: Exception) {
            Toast.makeText(context, "Error al parsear credenciales: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}