package kote2e

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.Cookie
import okhttp3.mockwebserver.MockResponse
import org.junit.Test
import java.nio.charset.StandardCharsets
import kotlin.test.assertEquals
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull


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

    @Test
    fun param() {
        server { request ->
            val url = request.requestUrl!!
            assertEquals("/foo/bar", url.encodedPath)
            assertEquals(setOf("name", "x[]"), url.queryParameterNames)
            assertEquals(listOf("太郎"), url.queryParameterValues("name"))
            assertEquals(listOf("次郎"), url.queryParameterValues("x[]"))

            MockResponse()
                .setResponseCode(200)
        }.use { server ->
            val bg = Background {
                url = server.url("/").toString()
            }
            bg
                .Given {
                    path = "/foo/bar"
                    param("name", "太郎")
                    param("x[]", "次郎")
                }
                .When { method = "GET" }
                .Then {
                    status shouldBe 200
                }
        }
    }

    @Test
    fun cookie() {
        server { request ->
            val rawCookie = request.getHeader("cookie")!!
            assertEquals("foo=bar; aaa=bbb", rawCookie)

            MockResponse()
                .setResponseCode(200)
        }.use { server ->
            val bg = Background {
                url = server.url("/").toString()
            }
            bg
                .Given {
                    cookie("foo", "bar")
                    cookie("aaa", "bbb")
                }
                .When { method = "GET" }
                .Then {
                    status shouldBe 200
                }
        }
    }
}

