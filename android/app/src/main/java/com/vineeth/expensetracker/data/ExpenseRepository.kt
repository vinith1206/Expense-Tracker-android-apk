package com.vineeth.expensetracker.data

import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val dao: ExpenseDao) {
    val expenses: Flow<List<ExpenseEntity>> = dao.observeAll()
    val totalAmount: Flow<Double?> = dao.observeTotal()
    val totalsByCategory: Flow<List<CategoryTotal>> = dao.observeTotalsByCategory()

    suspend fun addExpense(expense: ExpenseEntity) = dao.upsert(expense)
    suspend fun deleteExpense(expense: ExpenseEntity) = dao.delete(expense)
}
