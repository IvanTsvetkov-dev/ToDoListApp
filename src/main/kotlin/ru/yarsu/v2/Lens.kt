package ru.yarsu.v2

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.http4k.core.ContentType
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.contentType

fun <T> jsonResponseLens(): (T, Status) -> Response {
    val objectMapper = jacksonObjectMapper()
    return { data: T, status: Status ->
        val jsonString = objectMapper.writeValueAsString(data)
        Response(status)
            .body(jsonString)
            .contentType(ContentType.APPLICATION_JSON)
    }
}
