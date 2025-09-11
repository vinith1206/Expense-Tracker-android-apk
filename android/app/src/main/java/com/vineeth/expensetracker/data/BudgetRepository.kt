package com.vineeth.expensetracker.data

import kotlinx.coroutines.flow.Flow

class BudgetRepository(private val dao: BudgetDao) {
    fun observeOverall(year: Int, month: Int): Flow<BudgetEntity?> = dao.observeOverall(year, month)
    fun observeCategory(year: Int, month: Int, category: String): Flow<BudgetEntity?> = dao.observeCategory(year, month, category)
    suspend fun setOverall(year: Int, month: Int, amount: Double) {
        dao.insert(BudgetEntity(year = year, month = month, amount = amount))
    }
}
