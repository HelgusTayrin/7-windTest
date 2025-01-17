package mobi.sevenwinds.app.author

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.Instant
import java.util.*

object AuthorService {

    suspend fun createAuthor(body: AuthorRecordRequest): AuthorRecord = withContext(Dispatchers.IO) {
        transaction {
            return@transaction AuthorEntity.new {
                this.fullName = body.fullName
                this.creationDateTime = Instant.now().toDateTime()
            }.toResponse()
        }
    }

    suspend fun getAuthorById(authorId: String): AuthorRecord = withContext(Dispatchers.IO) {
        transaction {
            val query = AuthorTable.select(AuthorTable.id.eq(UUID.fromString(authorId)))
            val data = AuthorEntity.wrapRows(query).map { it.toResponse() }[0]
            return@transaction data
        }
    }

    suspend fun getAuthorList(param: AuthorStatsParam): AuthorStatsResponse = withContext(Dispatchers.IO) {
        val query = AuthorTable
            .selectAll()
            .limit(param.limit?: 10, param.offset?: 0)
        transaction {

            val total = query.count()

            val data = AuthorEntity.wrapRows(query).map { it.toResponse() }

            return@transaction AuthorStatsResponse(
                total = total,
                items = data
            )
        }
    }
}