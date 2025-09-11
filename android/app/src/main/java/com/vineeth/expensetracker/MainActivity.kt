package com.vineeth.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.ViewModelProvider
import com.vineeth.expensetracker.util.ThemePreferences
import kotlinx.coroutines.launch
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vineeth.expensetracker.data.BudgetRepository
import com.vineeth.expensetracker.data.ExpenseRepository
import com.vineeth.expensetracker.ui.AddExpenseScreen
import com.vineeth.expensetracker.ui.EditExpenseScreen
import com.vineeth.expensetracker.ui.ExpensesScreen
import com.vineeth.expensetracker.ui.ExpensesViewModel
import com.vineeth.expensetracker.ui.ViewModelFactory
import com.vineeth.expensetracker.ui.theme.ExpenseTheme
import java.time.LocalDate
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as ExpenseTrackerApplication
        val repository = ExpenseRepository(app.database.expenseDao())
        val budgetRepository = BudgetRepository(app.database.budgetDao())
        val factory = ViewModelFactory(repository, budgetRepository)
        val viewModel: ExpensesViewModel = ViewModelProvider(this, factory)[ExpensesViewModel::class.java]

        setContent {
            var isDark by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                ThemePreferences.darkModeFlow(this@MainActivity).collect { saved ->
                    isDark = saved
                }
            }
            ExpenseTheme(useDarkTheme = isDark) {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "list") {
                    composable("list") {
                        ExpensesScreen(
                            viewModel = viewModel,
                            onAddClick = { navController.navigate("add") },
                            onToggleTheme = {
                                val next = !isDark
                                isDark = next
                                lifecycleScope.launch { ThemePreferences.setDarkMode(this@MainActivity, next) }
                            },
                            isDark = isDark,
                            onEdit = { exp ->
                                val t = URLEncoder.encode(exp.title, StandardCharsets.UTF_8.toString())
                                val c = URLEncoder.encode(exp.category, StandardCharsets.UTF_8.toString())
                                val p = URLEncoder.encode(exp.person, StandardCharsets.UTF_8.toString())
                                navController.navigate("edit/$t/${exp.amount}/$c/${exp.date}/$p")
                            }
                        )
                    }
                    composable("add") {
                        AddExpenseScreen(
                            onSave = { title, amount, category, date, person ->
                                viewModel.addExpense(title, amount, category, date, person)
                                navController.popBackStack()
                            },
                            onCancel = { navController.popBackStack() },
                            onToggleTheme = {
                                val next = !isDark
                                isDark = next
                                lifecycleScope.launch { ThemePreferences.setDarkMode(this@MainActivity, next) }
                            },
                            isDark = isDark
                        )
                    }
                    composable(
                        route = "edit/{title}/{amount}/{category}/{date}/{person}",
                        arguments = listOf(
                            navArgument("title") { type = NavType.StringType },
                            navArgument("amount") { type = NavType.StringType },
                            navArgument("category") { type = NavType.StringType },
                            navArgument("date") { type = NavType.StringType },
                            navArgument("person") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val args = backStackEntry.arguments!!
                        val t = args.getString("title") ?: ""
                        val a = args.getString("amount")?.toDoubleOrNull() ?: 0.0
                        val c = args.getString("category") ?: ""
                        val d = args.getString("date") ?: LocalDate.now().toString()
                        val p = args.getString("person") ?: ""
                        EditExpenseScreen(
                            initialTitle = t,
                            initialAmount = a,
                            initialCategory = c,
                            initialDate = d,
                            initialPerson = p,
                            onSave = { nt, na, nc, nd, np ->
                                viewModel.addExpense(nt, na, nc, nd, np)
                                navController.popBackStack()
                            },
                            onCancel = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

