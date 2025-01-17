package mobi.sevenwinds.app.budget

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import mobi.sevenwinds.app.author.AuthorRecord
import mobi.sevenwinds.app.author.AuthorService
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import kotlin.coroutines.suspendCoroutine

object BudgetService {
    suspend fun addRecord(body: BudgetRecordRequest): BudgetRecord = withContext(Dispatchers.IO) {
        transaction {
            if (body.author == null) {
                    return@transaction BudgetEntity.new {
                        this.year = body.year
                        this.month = body.month
                        this.amount = body.amount
                        this.type = body.type
                    }.toResponse()
            } else {
                return@transaction BudgetEntity.new {
                    this.year = body.year
                    this.month = body.month
                    this.amount = body.amount
                    this.type = body.type
                    this.author = UUID.fromString(body.author)
                }.toResponse()
            }
        }
    }

    suspend fun getYearStats(param: BudgetYearParam): BudgetYearStatsResponse = withContext(Dispatchers.IO) {
        transaction {
            val query = BudgetTable
                .select { BudgetTable.year eq param.year }
                .limit(param.limit?: 100, param.offset?: 0)

            val total = query.count()

            val data = BudgetEntity.wrapRows(query).map { it.toResponse() }

            val budgetList = mutableListOf<BudgetRecord>()

            if (param.pagingOffset == null && param.pagingLimit == null) {
                var i = 0
                while (i < data.size) {
                    budgetList.add(data[i])
                    i++
                }
            } else {
                for (i in param.pagingOffset!! .. param.pagingLimit!!) {
                    budgetList.add(data[i])
                }
            }

            budgetList.sortWith(
                compareBy<BudgetRecord> {
                    it.month
                }.thenByDescending {
                    it.amount
                }
            )

            val finalData = mutableListOf<Pair<BudgetRecord, AuthorRecord?>>()

            async {
                budgetList.forEach { budget ->
                    if (budget.author == null) {
                        finalData.add(budget to null)
                    } else finalData.add(budget to AuthorService.getAuthorById(budget.author!!))
                }
            }

            val sumByType = data.groupBy { it.type.name }.mapValues { it.value.sumOf { v -> v.amount } }

            return@transaction BudgetYearStatsResponse(
                total = total,
                totalByType = sumByType,
                items = finalData
            )
        }
    }
}