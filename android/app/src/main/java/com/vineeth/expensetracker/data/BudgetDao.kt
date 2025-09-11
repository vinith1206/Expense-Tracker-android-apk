package com.vineeth.expensetracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: BudgetEntity): Long

    @Query("SELECT * FROM budgets WHERE year = :year AND month = :month AND category IS NULL LIMIT 1")
    fun observeOverall(year: Int, month: Int): Flow<BudgetEntity?>

    @Query("SELECT * FROM budgets WHERE year = :year AND month = :month AND category = :category LIMIT 1")
    fun observeCategory(year: Int, month: Int, category: String): Flow<BudgetEntity?>
}
