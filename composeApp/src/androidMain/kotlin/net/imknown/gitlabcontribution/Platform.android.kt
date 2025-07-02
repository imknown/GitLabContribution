package net.imknown.gitlabcontribution

import android.os.Build
import io.ktor.client.HttpClient

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun createHttpClient() = HttpClient()