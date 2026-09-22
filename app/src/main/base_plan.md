# Plan de Implementación - Interfaz de Prueba para IA

Este plan detalla los cambios necesarios para crear una interfaz de usuario funcional en Jetpack Compose que permita probar el modelo de red neuronal.

## Cambios Propuestos

### Modelo e IA

#### [MODIFY] [Neuron.kt](file:///D:/Programacion/Kotlin/app/src/main/java/com/example/finalproject/model/neurons/neuron.kt)
- Reemplazar `TODO()` por la inicialización de la clase `Activation` para evitar cierres inesperados.

#### [MODIFY] [network.kt](file:///D:/Programacion/Kotlin/app/src/main/java/com/example/finalproject/model/neurons/network.kt)
- Modificar la función `network` para que devuelva la lista de resultados (`List<Double>`), permitiendo que la UI muestre los datos procesados.

### Interfaz de Usuario (UI)

#### [MODIFY] [MainActivity.kt](file:///D:/Programacion/Kotlin/app/src/main/java/com/example/finalproject/MainActivity.kt)
- Implementar una pantalla con:
    - Campo de texto para ingresar valores numéricos separados por comas.
    - Botón para ejecutar la red neuronal.
    - Lista o texto para visualizar los resultados de la predicción.
    - Manejo de estados de Compose (`remember`, `mutableStateOf`).

## Verificación
1. Ejecutar la aplicación en el dispositivo/emulador.
2. Ingresar valores como "0.5, 1.2, 0.8".
3. Presionar el botón y verificar que se muestran resultados numéricos en pantalla.
