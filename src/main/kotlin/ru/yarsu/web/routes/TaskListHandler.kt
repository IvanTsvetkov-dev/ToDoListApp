package ru.yarsu.web.routes

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status

class TaskListHandler : HttpHandler{
    override fun invoke(request: Request): Response {
        return Response(Status.LOCKED)
    }
}
