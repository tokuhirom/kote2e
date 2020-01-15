package kote2e.matcher

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeType
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class FuzzyMatcher(
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
) {
    fun match(node: JsonNode, expected: String): List<String> {
        val expectedNode = objectMapper.readTree(expected)

        return walk(node, expectedNode, "$")
    }

    fun match(got: String, expected: String): List<String> {
        return match(objectMapper.readTree(got), expected)
    }

    private fun walk(got: JsonNode, expected: JsonNode, path: String): List<String> {
        when (expected.nodeType!!) {
            JsonNodeType.ARRAY -> {
                if (got.nodeType == JsonNodeType.ARRAY) {
                    val expectedList = expected.asSequence().toList()
                    val gotList = got.asSequence().toList()
                    if (gotList.size != expectedList.size) {
                        return listOf("$path: Expected list size is ${expectedList.size} but got ${gotList.size}: ${got.toPrettyString()}")
                    }

                    return expectedList.indices
                        .flatMap { i ->
                            walk(got.elementAt(i), expected.elementAt(i), path + "." + (i + 1))
                        }.toList()
                } else {
                    return listOf("$path: Expected ${expected.toPrettyString()} but got ${got.toPrettyString()}")
                }
            }
            JsonNodeType.BOOLEAN -> {
                if (got.nodeType == JsonNodeType.BOOLEAN) {
                    if (got.booleanValue() == expected.booleanValue()) {
                        return listOf()
                    } else {
                        return listOf("$path: Expected ${expected.toPrettyString()} but got ${got.toPrettyString()}")
                    }
                } else {
                    return listOf("$path: Expected ${expected.toPrettyString()} but got ${got.toPrettyString()}")
                }
            }
            JsonNodeType.NUMBER -> {
                if (got.nodeType == JsonNodeType.NUMBER) {
                    if (got.numberValue() == expected.numberValue()) {
                        return listOf()
                    } else {
                        return listOf("$path: Expected ${expected.toPrettyString()} but got ${got.toPrettyString()}")
                    }
                } else {
                    return listOf("$path: Expected ${expected.toPrettyString()} but got ${got.toPrettyString()}")
                }
            }
            JsonNodeType.NULL -> {
                if (got.nodeType == JsonNodeType.NULL) {
                    return listOf()
                } else {
                    return listOf("$path: Expected ${expected.toPrettyString()} but got ${got.toPrettyString()}")
                }
            }
            JsonNodeType.OBJECT -> {
                if (got.nodeType == JsonNodeType.OBJECT) {
                    val retval = ArrayList<String>()

                    expected.fields().forEach { mutableEntry: MutableMap.MutableEntry<String, JsonNode>? ->
                        val key = mutableEntry?.key
                        if (got.has(key)) {
                            val r = walk(mutableEntry?.value!!, got[key], path + "." + key)
                            retval.addAll(r)
                        } else {
                            retval.add("$path: Missing element $key")
                        }
                    }

                    val unknownFields = got.fieldNames().asSequence().toSet()
                        .minus(
                            expected.fieldNames().asSequence().toSet()
                        )
                    if (!unknownFields.isEmpty()) {
                        retval.add("$path: Unknown fields ${unknownFields}")
                    }
                    return retval
                } else {
                    return listOf("$path: Expected ${expected.toPrettyString()} but got ${got.toPrettyString()}")
                }
            }
            JsonNodeType.STRING -> {
                if (got.nodeType == JsonNodeType.STRING) {
                    if (got.asText() == expected.asText()) {
                        return listOf()
                    }
                }
                if (expected.asText().startsWith("#")) {
                    return matchMarker(expected.asText(), got, path)
                }
                return listOf("$path: Expected ${expected.toPrettyString()} but got ${got.toPrettyString()}")
            }
            JsonNodeType.MISSING, JsonNodeType.POJO, JsonNodeType.BINARY -> {
                throw IllegalArgumentException("JSON shouldn't include ${expected.nodeType}. But got ${expected.toPrettyString()}")

            }
        }
    }

    private fun matchMarker(marker: String, got: JsonNode, path: String): List<String> {
        // TODO more marker types https://github.com/intuit/karate#ignore-or-validate
        when (marker) {
            "#number" -> {
                if (got.isNumber) {
                    return listOf()
                } else {
                    return listOf("Expected number at $path but got ${got.nodeType}(${got.toPrettyString()})")
                }
            }
        }
        return listOf()
    }
}