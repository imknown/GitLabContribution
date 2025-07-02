package net.imknown.gitlabcontribution

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.ChallengeHandler
import io.ktor.client.engine.darwin.DarwinClientEngineConfig
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSURLAuthenticationChallenge
import platform.Foundation.NSURLAuthenticationMethodServerTrust
import platform.Foundation.NSURLCredential
import platform.Foundation.NSURLSession
import platform.Foundation.NSURLSessionAuthChallengeDisposition
import platform.Foundation.NSURLSessionAuthChallengePerformDefaultHandling
import platform.Foundation.NSURLSessionAuthChallengeUseCredential
import platform.Foundation.NSURLSessionTask
import platform.Foundation.create
import platform.Foundation.serverTrust
import platform.Security.SecTrustCopyExceptions
import platform.Security.SecTrustSetExceptions
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

actual fun createHttpClient() = HttpClient {
    engine {
        this as DarwinClientEngineConfig
//        handleChallenge(TrustAllChallengeHandler())
    }
}

/**
 * Challenge handler which trusts whatever certificate the server presents
 * This needs to be used in combination with plist additions:
 * ```
 * <key>NSAppTransportSecurity</key>
 * <dict>
 *     <key>NSExceptionDomains</key>
 *     <dict>
 *         <key>example.com</key>
 *         <dict>
 *             <key>NSExceptionAllowsInsecureHTTPLoads</key>
 *         <true/>
 *         </dict>
 *     </dict>
 * </dict>
 * ```
 * Supporting links:
 * - https://developer.apple.com/documentation/bundleresources/information_property_list/nsexceptionallowsinsecurehttploads
 * - https://developer.apple.com/documentation/foundation/url_loading_system/handling_an_authentication_challenge/performing_manual_server_trust_authentication
 */
private class TrustAllChallengeHandler : ChallengeHandler {
    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    override fun invoke(
        session: NSURLSession,
        task: NSURLSessionTask,
        challenge: NSURLAuthenticationChallenge,
        completionHandler: (NSURLSessionAuthChallengeDisposition, NSURLCredential?) -> Unit,
    ) {
        // Check that we want to handle this kind of challenge
        val protectionSpace = challenge.protectionSpace
        if (protectionSpace.authenticationMethod != NSURLAuthenticationMethodServerTrust) {
            // Not a 'NSURLAuthenticationMethodServerTrust', default handling...
            completionHandler(NSURLSessionAuthChallengePerformDefaultHandling, null)
            return
        }

        val serverTrust = challenge.protectionSpace.serverTrust
        if (serverTrust == null) {
            // Server trust is null, default handling...
            completionHandler(NSURLSessionAuthChallengePerformDefaultHandling, null)
            return
        }

        // Ignore all server trust exceptions
        val exceptions = SecTrustCopyExceptions(serverTrust)
        SecTrustSetExceptions(serverTrust, exceptions)
        completionHandler(NSURLSessionAuthChallengeUseCredential, NSURLCredential.create(serverTrust))
    }
}