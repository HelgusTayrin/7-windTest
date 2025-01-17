package mobi.sevenwinds.app.budget

import org.jetbrains.exposed.dao.*
import java.util.*

object BudgetTable : UUIDTable("budget_table") {
    override val id = uuid("id").entityId()
    val year = integer("year")
    val month = integer("month")
    val amount = integer("amount")
    val type = enumerationByName("type", 100, BudgetType::class)
    val author = uuid("author").nullable()
}

class BudgetEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<BudgetEntity>(BudgetTable)

    var year by BudgetTable.year
    var month by BudgetTable.month
    var amount by BudgetTable.amount
    var type by BudgetTable.type
    var author by BudgetTable.author

    fun toResponse(): BudgetRecord {
        return if (author == null) {
            BudgetRecord(this.id.toString(), year, month, amount, type)
        } else BudgetRecord(this.id.toString(), year, month, amount, type, author.toString())
    }
}