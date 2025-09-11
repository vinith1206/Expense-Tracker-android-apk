package com.vineeth.expensetracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vineeth.expensetracker.data.BudgetRepository
import com.vineeth.expensetracker.data.ExpenseRepository

class ViewModelFactory(
    private val expenseRepository: ExpenseRepository,
    private val budgetRepository: BudgetRepository?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpensesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExpensesViewModel(expenseRepository, budgetRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
