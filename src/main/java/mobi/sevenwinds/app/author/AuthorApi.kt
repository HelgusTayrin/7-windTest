package mobi.sevenwinds.app.author

import com.papsign.ktor.openapigen.annotations.parameters.QueryParam
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route

fun NormalOpenAPIRoute.author() {
    route("/author") {
        route("/add").post<Unit, AuthorRecord, AuthorRecordRequest>(info("Создать автора")) { param, body ->
            respond(AuthorService.createAuthor(body))
        }
    }

        route("/getList") {
            get<AuthorStatsParam, AuthorStatsResponse>(info("Получить список авторов")) { param ->
                respond(AuthorService.getAuthorList(param))
            }
        }
}

data class AuthorRecordRequest(
    val fullName: String
)

data class AuthorRecord(
    val id: String? = null,
    val fullName: String? = null,
    val creationDateTime: String? = null
)

data class AuthorStatsParam(
    @QueryParam("Общее кол-во записей") val limit: Int?,
    @QueryParam("Смещение списка") val offset: Int?,
)

class AuthorStatsResponse(
    val total: Int,
    val items: List<AuthorRecord>
)