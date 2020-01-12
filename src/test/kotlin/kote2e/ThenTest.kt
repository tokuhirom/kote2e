package kote2e

import okhttp3.mockwebserver.MockResponse
import org.junit.Test
import kotlin.test.assertEquals

class ThenTest {

    @Test
    fun getValue() {
        server {
            MockResponse()
                .setBody("""{"id":5963}""")
                .setResponseCode(200)
        }.use { server ->
            val bg = Background {
                url = server.url("/").toString()
            }
            val id = bg
                .Given {
                    path = "/"
                }
                .When { method = "GET" }
                .Then {
                    status shouldBe 200
                    response shouldBe  """{"id":5963}"""
                    response.read<Int>("$.id")
                }
            assertEquals(5963, id)
        }
    }

    @Test
    fun readHeader() {
        server {
            MockResponse()
                .addHeader("x-foo-bar", "boo")
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
                    header("x-foo-bar") shouldBe "boo"
                }
        }
    }
}