package mobi.sevenwinds.app.author

import io.restassured.RestAssured
import mobi.sevenwinds.common.ServerTest
import mobi.sevenwinds.common.jsonBody
import mobi.sevenwinds.common.toResponse
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class AuthorApiKtTest : ServerTest() {

    @BeforeEach
    internal fun setUp() {
        transaction { AuthorTable.deleteAll() }
    }

    @Test
    fun testAuthorCreation() {
        addRecord(AuthorRecordRequest(fullName = "Московкин Олег Романович"))
    }

    private fun addRecord(record: AuthorRecordRequest) {
        var id: String
        RestAssured.given()
            .jsonBody(record)
            .post("/author/add")
            .toResponse<AuthorRecord>().let { response ->
                id = response.id!!
                println(response.fullName)
                println(response.creationDateTime)
            }
        RestAssured.given()
            .jsonBody(id)
            .get("/author/")
    }
}