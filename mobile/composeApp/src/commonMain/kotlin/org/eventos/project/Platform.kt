package org.eventos.project

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform