package ru.yarsu

import java.util.*

class WorkFlowWithUsers(
    private val usersData: List<User>,
) {
    fun getUserByUUIDAuthor(uuid: UUID): User? = usersData.find { it.id == uuid }
}
