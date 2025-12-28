package org.eventos.project

class JsPlatform: Platform {
    override val name: String = "Web with Kotlin/JS"
}

actual fun getPlatform(): Platform = JsPlatform()
actual fun defaultBaseUrl(): String = "http://localhost:8080"
