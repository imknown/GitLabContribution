package net.imknown.gitlabcontribution

import io.ktor.client.HttpClient

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()

actual fun createHttpClient() = HttpClient()