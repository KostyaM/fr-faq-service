package faq.fastreport.ru.db.session

import faq.fastreport.ru.db.intArray
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.uuid
import org.ktorm.support.postgresql.textArray
import java.util.*

/**
 *  @param id - id записи в БД
 *  @param userId - UUID для пользователя, который будет в JWT
 *  @param stage - Цепочка пдействий пользователя, которая привела его к текущему дереву решений
 * */
interface UserSession : Entity<UserSession> {
    companion object : Entity.Factory<UserSession>()

    val id: Long
    val userId: UUID
    val stage: Array<Int?>?
}

object UserSessions : Table<UserSession>("user_session") {
    val id = long("id").primaryKey().bindTo(UserSession::id)
    val userId = uuid("user_id").bindTo(UserSession::userId)
    val stage = intArray("stage").bindTo(UserSession::stage)
}