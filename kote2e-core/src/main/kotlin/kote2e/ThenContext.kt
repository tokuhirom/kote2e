package kote2e

import okhttp3.Response
import kotlin.test.assertEquals

class ThenContext(private val rawResponse: Response) {
    var response: ResponseWrapper =
        ResponseWrapper(rawResponse)
    val status: Int
        get() = rawResponse.code

    fun header(name: String): String? {
        return rawResponse.header(name)
    }

    infix fun ResponseWrapper.shouldMatch(expected: String) {
        assertEquals(expected, rawResponse.peekBody(Long.MAX_VALUE).string())
    }

    infix fun String?.shouldBe(expected: String) {
        assertEquals(expected, this)
    }

    infix fun ResponseWrapper?.shouldBe(expected: String) {
        assertEquals(expected, rawResponse.peekBody(Long.MAX_VALUE).string())
    }

    infix fun Int?.shouldBe(expected: Int) {
        assertEquals(expected, this)
    }
}
