package net.imknown.gitlabcontribution

import io.ktor.client.HttpClient

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()

actual fun createHttpClient() = HttpClient()