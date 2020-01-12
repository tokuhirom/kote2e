package kote2e

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URI

class GivenContext(
    private val okHttpClient: OkHttpClient,
    private val baseUrl: String,
    private val requestBuilder: Request.Builder = Request.Builder()
) {
    private lateinit var requestBody: RequestBody
    private lateinit var contentType: String
    private var contentTypeAutoSet = false

    var path: String? = null
        set(value) {
            if (value != null) {
                requestBuilder.url(URI(baseUrl).resolve(value).toURL())
            }
        }

    fun header(name: String, value: String) {
        if (name.toLowerCase() == "content-type") {
            contentType = value
            contentTypeAutoSet = false
        }
        requestBuilder.header(name, value)
    }

    fun request(body: String) {
        requestBody = body.toRequestBody()
        if (!::contentType.isInitialized) {
            if (body.matches(Regex(pattern = """\s*\{.*\}\s*"""))) {
                contentType = "application/json"
                contentTypeAutoSet = true
            }
        }
    }

    fun request(body: ByteArray) {
        requestBody = body.toRequestBody()
        if (!::contentType.isInitialized) {
            contentType = "application/octet-stream"
            contentTypeAutoSet = true
        }
    }

    fun read(path: String): ByteArray {
        return javaClass.classLoader.getResourceAsStream(path).readBytes()
    }

    @Suppress("FunctionName")
    fun When(cb: WhenContext.() -> Unit): WhenContext {
        if (contentTypeAutoSet) {
            requestBuilder.header("content-type", contentType)
        }
        val wc = WhenContext(
            okHttpClient, requestBuilder,
            if (::requestBody.isInitialized) {
                requestBody
            } else {
                null
            }
        )
        wc.apply(cb)
        return wc
    }
}
