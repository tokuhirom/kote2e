package kote2e

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.slf4j.bridge.SLF4JBridgeHandler


class BackgroundContext {
    lateinit var url: String
    val builder = OkHttpClient.Builder()

    @Suppress("FunctionName")
    fun Given(cb: GivenContext.() -> Unit): GivenContext {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        // TODO: switch to original logger
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.addInterceptor(logging)

        val ctx = GivenContext(builder.build(), url)
        ctx.apply(cb)
        return ctx
    }
}