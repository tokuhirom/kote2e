package kote2e.matcher

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource


internal class FuzzyMatcherTest {
    @ParameterizedTest
    @MethodSource("argsss")
    fun number(gotJson: String, expectedJson: String, expectedesult: List<String>) {
        val result = FuzzyMatcher().match(
            gotJson,
            expectedJson
        )
        assertEquals(expectedesult, result)
    }

    companion object {
        @JvmStatic
        fun argsss() = listOf(
            // null
            arguments("null", "null", listOf<String>()),
            arguments("false", "null", listOf("\$: Expected null but got false")),

            // boolean
            arguments("false", "false", listOf<String>()),
            arguments("true", "false", listOf("\$: Expected false but got true")),
            arguments("true", "true", listOf<String>()),
            arguments("false", "true", listOf("\$: Expected true but got false")),
            arguments("3", "false", listOf("\$: Expected false but got 3")),

            // number
            arguments("3", "3", listOf<String>()),
            arguments("3.14", "3", listOf("\$: Expected 3 but got 3.14")),
            arguments("\"foo\"", "3", listOf("\$: Expected 3 but got \"foo\"")),
            arguments("3", "\"#number\"", listOf<String>()),

            // string
            arguments("3", "\"#string\"", listOf<String>()),
            arguments("\"abc\"", "\"abc\"", listOf<String>()),
            arguments("\"oops\"", "\"abc\"", listOf("\$: Expected \"abc\" but got \"oops\"")),

            // object
            arguments( // missing field
                """{
                    "foo":"bar"
                }""".trimIndent(), """{
                    "hoge":"fuga",
                    "foo":"bar"
                }""".trimIndent(), listOf("\$: Missing element hoge")
            ),
            arguments( // extra field
                """{
                    "hoge":"fuga",
                    "foo":"bar"
                }""".trimIndent(), """{
                    "foo":"bar"
                }""".trimIndent(), listOf("\$: Unknown fields [hoge]")
            ),
            arguments( // extra field
                """{
                    "hoge":"fuga",
                    "foo":"bar"
                }""".trimIndent(), """{
                    "foo":"bar"
                }""".trimIndent(), listOf("\$: Unknown fields [hoge]")
            ),
            arguments( // field value miss match
                """{
                    "hoge":"fuga",
                    "foo":"bar"
                }""".trimIndent(), """{
                    "hoge":"INV",
                    "foo":"bar"
                }""".trimIndent(), listOf("\$.hoge: Expected \"fuga\" but got \"INV\"")
            ),
            arguments(
                """{
                |"foo":"bar"
                |}""".trimMargin(), """{
                |"foo":"bar"
                |}""".trimMargin(), listOf<String>()
            )
        ).stream()
    }
}
