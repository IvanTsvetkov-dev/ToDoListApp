package ru.yarsu.v2.serializers

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import ru.yarsu.TaskModel
import java.io.StringWriter

class TaskShowSerializer : BaseSerializer() {
    fun serializeeTask(
        taskById: TaskModel,
        authorEmail: String?,
        categoryDescription: String,
    ): String {
        val stringWriter = StringWriter()
        val factory = JsonFactory()
        val outputGenerator: JsonGenerator = factory.createGenerator(stringWriter)
        val printer = DefaultPrettyPrinter()
        printer.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE)
        outputGenerator.prettyPrinter = printer

        with(outputGenerator) {
            writeStartObject()

            writeFieldName("Id")
            writeString(taskById.id.toString())

            writeFieldName("Title")
            writeString(taskById.title)

            writeFieldName("RegistrationDateTime")
            writeString(taskById.registrationDateTime.toString())

            writeFieldName("StartDateTime")
            writeString(taskById.startDateTime.toString())

            writeFieldName("EndDateTime")
            if (taskById.endDateTime == null) {
                writeNull()
            } else {
                writeString(taskById.endDateTime.toString())
            }

            writeFieldName("Importance")
            writeString(taskById.importance)

            writeFieldName("Urgency")
            writeBoolean(taskById.urgency)

            writeFieldName("Percentage")
            writeNumber(taskById.percentage)

            writeFieldName("Description")
            writeString(taskById.description)

            writeFieldName("Author")
            writeString(taskById.author.toString())

            writeFieldName("AuthorEmail")
            writeString(authorEmail)

            writeFieldName("Category")
            writeString(taskById.category.toString())

            writeFieldName("CategoryDescription")
            writeString(categoryDescription)

            writeEndObject()
            close()
        }
        return stringWriter.toString()
    }

    fun serializeNotFoundTask(
        taskId: String,
        errorMessage: String,
    ): String {
        val stringWriter = StringWriter()
        val factory = JsonFactory()
        val outputGenerator: JsonGenerator = factory.createGenerator(stringWriter)
        val printer = DefaultPrettyPrinter()
        printer.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE)
        outputGenerator.prettyPrinter = printer

        with(outputGenerator) {
            writeStartObject()

            writeFieldName("task-id")
            writeString(taskId)

            writeFieldName("error")
            writeString(errorMessage)

            writeEndObject()
            close()
        }
        return stringWriter.toString()
    }
}
