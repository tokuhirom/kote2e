package kote2e

import com.jayway.jsonpath.JsonPath
import okhttp3.Response

class ResponseWrapper(private val response: Response) {
    fun <T> read(jsonPath: String): T {
        val body = response.peekBody(Long.MAX_VALUE).string()
        return JsonPath.read(body, jsonPath)
    }
}
