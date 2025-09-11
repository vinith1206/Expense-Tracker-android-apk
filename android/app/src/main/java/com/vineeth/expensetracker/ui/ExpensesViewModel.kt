package com.vineeth.expensetracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vineeth.expensetracker.data.BudgetRepository
import com.vineeth.expensetracker.data.ExpenseEntity
import com.vineeth.expensetracker.data.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

enum class DateRange { ALL, THIS_WEEK, THIS_MONTH, CUSTOM }

data class DateRangeState(val type: DateRange, val start: LocalDate?, val end: LocalDate?)

class ExpensesViewModel(
    private val repository: ExpenseRepository,
    private val budgetRepository: BudgetRepository? = null
) : ViewModel() {
    private val selectedCategory = MutableStateFlow<String?>(null)
    private val selectedPerson = MutableStateFlow<String?>(null)
    private val dateRange = MutableStateFlow<DateRangeState>(DateRangeState(DateRange.ALL, null, null))
    private val searchQuery = MutableStateFlow("")

    // Distinct persons from ALL expenses (not filtered) for chips
    val persons: StateFlow<List<String>> = repository.expenses
        .map { list ->
            list.mapNotNull { it.person.takeIf { p -> p.isNotBlank() } }
                .map { it.trim() }
                .distinct()
                .sorted()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private fun currentRange(): Pair<LocalDate?, LocalDate?> {
        return when (val r = dateRange.value) {
            DateRangeState(DateRange.ALL, null, null) -> null to null
            DateRangeState(DateRange.THIS_WEEK, null, null) -> {
                val today = LocalDate.now()
                val start = today.minusDays(((today.dayOfWeek.value - 1).toLong()))
                val end = start.plusDays(6)
                start to end
            }
            DateRangeState(DateRange.THIS_MONTH, null, null) -> {
                val today = LocalDate.now()
                val start = today.with(TemporalAdjusters.firstDayOfMonth())
                val end = today.with(TemporalAdjusters.lastDayOfMonth())
                start to end
            }
            else -> r.start to r.end
        }
    }

    val expenses = repository.expenses
        .combine(selectedCategory) { list, cat ->
            if (cat.isNullOrEmpty()) list else list.filter { it.category == cat }
        }
        .combine(selectedPerson) { list, person ->
            if (person.isNullOrEmpty()) list else list.filter { it.person.equals(person, ignoreCase = true) }
        }
        .combine(dateRange) { list, _ ->
            val (start, end) = currentRange()
            if (start == null || end == null) list else list.filter { it.date in start..end }
        }
        .combine(searchQuery) { list, q ->
            val query = q.trim().lowercase()
            if (query.isEmpty()) list else list.filter {
                it.title.lowercase().contains(query) ||
                it.category.lowercase().contains(query) ||
                it.person.lowercase().contains(query)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val total: StateFlow<Double> = expenses
        .map { list -> list.sumOf { it.amount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    fun setCategoryFilter(category: String?) { selectedCategory.value = category }
    fun setPersonFilter(person: String?) { selectedPerson.value = person }
    fun setDateRange(range: DateRange) { dateRange.value = DateRangeState(range, null, null) }
    fun setCustomRange(start: LocalDate, end: LocalDate) { dateRange.value = DateRangeState(DateRange.CUSTOM, start, end) }
    fun setSearchQuery(q: String) { searchQuery.value = q }

    fun addExpense(
        title: String,
        amount: Double,
        category: String,
        date: LocalDate,
        person: String
    ) {
        viewModelScope.launch {
            repository.addExpense(
                ExpenseEntity(
                    title = title,
                    amount = amount,
                    category = category,
                    date = date,
                    person = person
                )
            )
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch { repository.deleteExpense(expense) }
    }

    // Budget
    val monthlyBudget: StateFlow<Double?> = (budgetRepository?.let { repo ->
        val today = LocalDate.now()
        repo.observeOverall(today.year, today.month.value).map { it?.amount }
    } ?: MutableStateFlow<Double?>(null)).stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), null
    )

    fun setMonthlyBudget(year: Int, month: Int, amount: Double) {
        val repo = budgetRepository ?: return
        viewModelScope.launch { repo.setOverall(year, month, amount) }
    }
}
