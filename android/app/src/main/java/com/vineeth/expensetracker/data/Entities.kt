package com.vineeth.expensetracker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.time.LocalDate

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: String,
    @ColumnInfo(name = "date") val date: LocalDate,
    val person: String
)

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "year") val year: Int,
    @ColumnInfo(name = "month") val month: Int, // 1..12
    @ColumnInfo(name = "category") val category: String? = null, // null == overall
    @ColumnInfo(name = "amount") val amount: Double
)

class Converters {
    @TypeConverter
    fun fromDate(date: LocalDate?): String? = date?.toString()

    @TypeConverter
    fun toDate(value: String?): LocalDate? = value?.let(LocalDate::parse)
}
