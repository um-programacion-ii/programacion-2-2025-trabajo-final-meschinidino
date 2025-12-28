# Mobile (Kotlin Multiplatform)

Cliente KMP que consume el backend para el flujo de compra de entradas.

## Requisitos
- JDK 17 (Android/AGP)
- Android Studio o IntelliJ IDEA

## Build Android

```bash
./gradlew :composeApp:assembleDebug
```

## Configuración de Backend

El cliente permite configurar la URL base desde la pantalla de login (por defecto `http://localhost:8080`).
