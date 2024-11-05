package ru.yarsu.v1.serializers

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import ru.yarsu.TasksForListCommand
import java.io.StringWriter

class TaskListSerializer: BaseSerializer(){
    fun taskList(taskList: List<TasksForListCommand>) : String{
        val stringWriter = StringWriter()
        val mapper = jacksonObjectMapper()
        val printer = DefaultPrettyPrinter()
        printer.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE)
        mapper.enable(SerializationFeature.INDENT_OUTPUT)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .writer(printer)
            .writeValue(stringWriter, taskList)

        return stringWriter.toString()
    }
}
