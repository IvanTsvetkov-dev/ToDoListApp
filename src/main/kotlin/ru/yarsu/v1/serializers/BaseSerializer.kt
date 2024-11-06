package ru.yarsu.v1.serializers

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import java.io.StringWriter

open class BaseSerializer {
    fun serializeError(textError: String): String {
        val stringWriter = StringWriter()
        val factory = JsonFactory()
        val outputGenerator: JsonGenerator = factory.createGenerator(stringWriter)
        val printer = DefaultPrettyPrinter()
        printer.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE)
        outputGenerator.prettyPrinter = printer

        with(outputGenerator) {
            writeStartObject()

            writeFieldName("error")

            writeString(textError)

            writeEndObject()

            close()
        }
        return stringWriter.toString()
    }
}
