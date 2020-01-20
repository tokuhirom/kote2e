package kote2e

import okhttp3.mockwebserver.MockResponse
import org.junit.Test
import java.nio.charset.StandardCharsets
import kotlin.test.assertEquals


class GivenTest {
    @Test
    fun header() {
        server { request ->
            MockResponse()
                .setBody(request.getHeader("X-Foo-Bar")!!)
                .setResponseCode(200)
        }.use { server ->
            val bg = Background {
                url = server.url("/").toString()
            }
            bg
                .Given {
                    path = "/"
                    header("X-Foo-Bar", "Baz")
                }
                .When { method = "GET" }
                .Then {
                    status shouldBe 200
                    response shouldMatch "Baz"
                }
        }
    }


    @Test
    fun requestJsonAsString() {
        server { request ->
            assertEquals("application/json", request.getHeader("content-type"))

            val body = request.body.readString(StandardCharsets.UTF_8)
            MockResponse()
                .setBody(body)
                .setResponseCode(200)
        }.use { server ->
            val bg = Background {
                url = server.url("/").toString()
            }
            bg
                .Given {
                    path = "/"
                    request("""{"id":4649}""")
                }
                .When { method = "POST" }
                .Then {
                    status shouldBe 200
                    response shouldBe """{"id":4649}"""
                }
        }
    }

    @Test
    fun requestJsonFromObject() {
        server { request ->
            assertEquals("application/json", request.getHeader("content-type"))

            val body = request.body.readString(StandardCharsets.UTF_8)
            MockResponse()
                .setBody(body)
                .setResponseCode(200)
        }.use { server ->
            val bg = Background {
                url = server.url("/").toString()
            }
            bg
                .Given {
                    path = "/"
                    request(mapOf("hello" to "world"))
                }
                .When { method = "POST" }
                .Then {
                    status shouldBe 200
                    response shouldBe """{"hello":"world"}"""
                }
        }
    }

    @Test
    fun read() {
        server { request ->
            assertEquals("application/octet-stream", request.getHeader("content-type"))
            val body = request.body.readString(StandardCharsets.UTF_8)
            MockResponse()
                .setBody(body)
                .setResponseCode(200)
        }.use { server ->
            val bg = Background {
                url = server.url("/").toString()
            }
            bg
                .Given {
                    path = "/"
                    request(read("hello.dat"))
                }
                .When { method = "POST" }
                .Then {
                    status shouldBe 200
                    response shouldBe """abcdefg"""
                }
        }
    }
}

