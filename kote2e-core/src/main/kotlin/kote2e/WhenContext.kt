package kote2e

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class WhenContext(
    private val okHttpClient: OkHttpClient,
    private val requestBuilder: Request.Builder,
    private val requestBody: RequestBody?
) {
    var method: String? = null

    @Suppress("FunctionName")
    fun <T> Then(cb: ThenContext.() -> T): T {
        requestBuilder.method(method!!, requestBody)
        val response = okHttpClient.newCall(requestBuilder.build())
            .execute()
        val tc = ThenContext(response)
        return tc.let(cb)
    }
}