package com.vineeth.expensetracker.util

import android.content.Context
import androidx.core.content.FileProvider
import com.vineeth.expensetracker.data.ExpenseEntity
import java.io.File

object CsvExporter {
    fun exportExpenses(context: Context, expenses: List<ExpenseEntity>): android.net.Uri {
        val cacheDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val file = File(cacheDir, "expenses.csv")
        file.printWriter().use { out ->
            out.println("Title,Amount,Category,Date,Person")
            expenses.forEach { e ->
                val safeTitle = e.title.replace(",", " ")
                val safePerson = e.person.replace(",", " ")
                out.println("$safeTitle,${e.amount},${e.category},${e.date},$safePerson")
            }
        }
        return FileProvider.getUriForFile(
            context,
            "com.vineeth.expensetracker.fileprovider",
            file
        )
    }
}
