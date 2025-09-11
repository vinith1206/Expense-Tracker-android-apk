package com.vineeth.expensetracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY date DESC, id DESC")
    fun observeAll(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<ExpenseEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(expense: ExpenseEntity): Long

    @Update
    suspend fun update(expense: ExpenseEntity)

    @Delete
    suspend fun delete(expense: ExpenseEntity)

    @Query("SELECT SUM(amount) FROM expenses")
    fun observeTotal(): Flow<Double?>

    @Query("SELECT category, SUM(amount) as total FROM expenses GROUP BY category ORDER BY total DESC")
    fun observeTotalsByCategory(): Flow<List<CategoryTotal>>

    @Query("SELECT COUNT(*) FROM expenses")
    suspend fun count(): Long
}

data class CategoryTotal(
    val category: String,
    val total: Double
)
