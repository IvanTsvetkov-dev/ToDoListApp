package ru.yarsu.handler.v1

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status

class PingHandler : HttpHandler {
    override fun invoke(request: Request) : Response = Response(Status.OK)
}
