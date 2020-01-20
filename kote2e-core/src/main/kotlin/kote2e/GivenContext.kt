package kote2e

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.Cookie
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class GivenContext(
    private val okHttpClient: OkHttpClient,
    private val baseUrl: String,
    private val requestBuilder: Request.Builder = Request.Builder()
) {
    private lateinit var requestBody: RequestBody
    private lateinit var contentType: String
    private var contentTypeAutoSet = false
    private val params = ArrayList<Pair<String, String>>()
    private val cookies = ArrayList<Pair<String, String>>()

    var path: String = "/"

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

    fun request(body: Any) {
        requestBody = jacksonObjectMapper().writeValueAsBytes(body).toRequestBody()
        if (!::contentType.isInitialized) {
            contentType = "application/json"
            contentTypeAutoSet = true
        }
    }

    fun param(name: String, value: String) {
        params.add(name to value)
    }

    fun cookie(name: String, value: String) {
        cookies.add(name to value)
    }

    fun read(path: String): ByteArray {
        return javaClass.classLoader.getResourceAsStream(path)
            .use { it!!.readBytes() }
    }

    @Suppress("FunctionName")
    fun When(cb: WhenContext.() -> Unit): WhenContext {
        val urlBuilder = baseUrl.toHttpUrlOrNull()!!
            .newBuilder()
            .encodedPath(path)
        params.forEach {
            urlBuilder.addQueryParameter(it.first, it.second)
        }
        requestBuilder.url(urlBuilder.build())

        // cookie
        requestBuilder.addHeader("Cookie",
            cookies.joinToString("; ") { """${it.first}=${it.second}""" })

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
