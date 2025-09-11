package com.vineeth.expensetracker

import android.app.Application
import androidx.room.Room
import com.vineeth.expensetracker.data.AppDatabase
import com.vineeth.expensetracker.data.ExpenseEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class ExpenseTrackerApplication : Application() {
    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "expenses.db"
        ).fallbackToDestructiveMigration().build()

        // Seed basic sample data on first launch
        CoroutineScope(Dispatchers.IO).launch {
            if (database.expenseDao().count() == 0L) {
                val today = LocalDate.now()
                val samples = listOf(
                    ExpenseEntity(title = "Vegetables & Milk", amount = 650.0, category = "Groceries", date = today.minusDays(1), person = "Family"),
                    ExpenseEntity(title = "House Rent", amount = 15000.0, category = "Rent", date = today.minusDays(3), person = "Self"),
                    ExpenseEntity(title = "Mobile Recharge", amount = 249.0, category = "Mobile/Internet", date = today.minusDays(2), person = "Self"),
                    ExpenseEntity(title = "Auto to Office", amount = 120.0, category = "Transport", date = today.minusDays(1), person = "Self"),
                    ExpenseEntity(title = "Petrol", amount = 1200.0, category = "Fuel", date = today.minusDays(4), person = "Self"),
                    ExpenseEntity(title = "Dinner Out", amount = 900.0, category = "Dining Out", date = today.minusDays(5), person = "Family"),
                    ExpenseEntity(title = "Electricity Bill", amount = 2100.0, category = "Utilities", date = today.minusDays(7), person = "Family"),
                    ExpenseEntity(title = "Tuition Fees", amount = 3000.0, category = "Education", date = today.minusDays(6), person = "Child"),
                    ExpenseEntity(title = "Health Medicines", amount = 450.0, category = "Medical", date = today.minusDays(2), person = "Parent")
                )
                samples.forEach { database.expenseDao().upsert(it) }
            }
        }
    }
}
