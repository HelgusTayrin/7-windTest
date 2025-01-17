package mobi.sevenwinds.app.author

import org.jetbrains.exposed.dao.*
import java.util.*

object AuthorTable : UUIDTable("author_table") {
    override val id = uuid("id").entityId()
    val fullName = text("full_name")
    val dateTime = datetime("date_time")
}

class AuthorEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<AuthorEntity>(AuthorTable)
    var fullName by AuthorTable.fullName
    var creationDateTime by AuthorTable.dateTime

    fun toResponse(): AuthorRecord {
        return AuthorRecord(this.id.toString(), fullName, creationDateTime.toString())
    }
}