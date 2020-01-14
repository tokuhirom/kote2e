package kote2e

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.QueueDispatcher
import okhttp3.mockwebserver.RecordedRequest

fun server(handler: (RecordedRequest) -> MockResponse): MockWebServer {
    val server = MockWebServer()
    server.dispatcher = object : QueueDispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            return handler(request)
        }
    }
    server.start()
    return server
}

