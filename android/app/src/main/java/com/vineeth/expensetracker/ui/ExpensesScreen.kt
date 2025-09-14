package com.vineeth.expensetracker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.vineeth.expensetracker.util.CsvExporter
import com.vineeth.expensetracker.ui.theme.AccentPrimary
import com.vineeth.expensetracker.data.ExpenseEntity

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExpensesScreen(
    viewModel: ExpensesViewModel,
    onAddClick: () -> Unit,
    onToggleTheme: () -> Unit,
    isDark: Boolean,
    onEdit: (ExpenseEntity) -> Unit
) {
    val context = LocalContext.current
    val expenses = viewModel.expenses.collectAsState().value
    val total = viewModel.total.collectAsState().value
    val budget = viewModel.monthlyBudget.collectAsState().value ?: 0.0
    val persons = viewModel.persons.collectAsState().value

    var categoryExpanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedPersonUi by remember { mutableStateOf<String?>(null) }
    var selectedRange by remember { mutableStateOf(DateRange.ALL) }

    var showBudgetDialog by remember { mutableStateOf(false) }
    var budgetText by remember { mutableStateOf("") }
    var showResetBudgetDialog by remember { mutableStateOf(false) }
    
    // Smooth scrolling state
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val ctx = LocalContext.current
                            val logoId = remember { ctx.resources.getIdentifier("ic_app_logo", "drawable", ctx.packageName) }
                            if (logoId != 0) {
                                Icon(
                                    painter = androidx.compose.ui.res.painterResource(id = logoId),
                                    contentDescription = null,
                                    tint = Color.Unspecified
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "ExpenseTracker",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = "Vineeth Expense Tracker",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFB0B0B0),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onToggleTheme) {
                        Icon(
                            imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (isDark) "Light Mode" else "Dark Mode"
                        )
                    }
                    IconButton(onClick = {
                        val uri = CsvExporter.exportExpenses(context, expenses)
                        val share = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                            type = "text/csv"
                            putExtra(android.content.Intent.EXTRA_STREAM, uri)
                            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(android.content.Intent.createChooser(share, "Export CSV"))
                    }) {
                        Icon(Icons.Default.Upload, contentDescription = "Export CSV")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Add, contentDescription = "Add expense", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.fillMaxSize().padding(innerPadding), color = MaterialTheme.colorScheme.background) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Spacing below header
                item { Spacer(modifier = Modifier.height(8.dp)) }

                // Search bar above filters
                item {
                    var localQuery by rememberSaveable { mutableStateOf("") }
                    OutlinedTextField(
                        value = localQuery,
                        onValueChange = { localQuery = it; viewModel.setSearchQuery(it) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        placeholder = { Text("Search title, category, person") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item { Spacer(modifier = Modifier.height(6.dp)) }

                // Date range chips with accent
                item {
                    val chipShape = RoundedCornerShape(24.dp)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        FilterChip(
                            selected = selectedRange == DateRange.ALL,
                            onClick = { selectedRange = DateRange.ALL; viewModel.setDateRange(DateRange.ALL) },
                            shape = chipShape,
                            label = { Text("All") },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                selectedContainerColor = AccentPrimary,
                                labelColor = if (selectedRange == DateRange.ALL) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                        FilterChip(
                            selected = selectedRange == DateRange.THIS_WEEK,
                            onClick = { selectedRange = DateRange.THIS_WEEK; viewModel.setDateRange(DateRange.THIS_WEEK) },
                            shape = chipShape,
                            leadingIcon = { Text("📅") },
                            label = { Text("This Week") },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                selectedContainerColor = AccentPrimary,
                                labelColor = if (selectedRange == DateRange.THIS_WEEK) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                        FilterChip(
                            selected = selectedRange == DateRange.THIS_MONTH,
                            onClick = { selectedRange = DateRange.THIS_MONTH; viewModel.setDateRange(DateRange.THIS_MONTH) },
                            shape = chipShape,
                            leadingIcon = { Text("🗓️") },
                            label = { Text("This Month") },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                selectedContainerColor = AccentPrimary,
                                labelColor = if (selectedRange == DateRange.THIS_MONTH) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }

                // Total & Budget card with circular progress
                item {
                    ElevatedCard(modifier = Modifier.fillMaxWidth().shadow(1.dp)) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Total Spent", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    text = "₹ %.2f".format(total),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { showBudgetDialog = true }, 
                                        shape = RoundedCornerShape(24.dp)
                                    ) { 
                                        Text("Set Budget") 
                                    }
                                    if (budget > 0) {
                                        OutlinedButton(
                                            onClick = { showResetBudgetDialog = true },
                                            shape = RoundedCornerShape(24.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Refresh,
                                                contentDescription = "Reset Budget",
                                                modifier = Modifier.width(16.dp)
                                            )
                                            Spacer(Modifier.width(4.dp))
                                            Text("Reset")
                                        }
                                    }
                                }
                            }
                            val percent = if (budget > 0) (total / budget).coerceIn(0.0, 1.0) else 0.0
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(
                                    progress = { percent.toFloat() },
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                    color = AccentPrimary
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = if (budget > 0) "${(percent * 100).toInt()}%" else "0%",
                                    style = MaterialTheme.typography.labelLarge,
                                    textAlign = TextAlign.Center
                                )
                                if (budget > 0) {
                                    Text(
                                        text = "₹ %.0f of ₹ %.0f".format(total, budget),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }

                // Person filters
                item {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        PersonFilterChip(
                            label = "All People",
                            selected = selectedPersonUi == null,
                            onClick = {
                                selectedPersonUi = null
                                viewModel.setPersonFilter(null)
                            }
                        )
                        persons.forEach { p ->
                            val label = if (p.equals("self", ignoreCase = true)) "Me" else p
                            PersonFilterChip(
                                label = label,
                                selected = selectedPersonUi == p,
                                onClick = {
                                    selectedPersonUi = p
                                    viewModel.setPersonFilter(p)
                                }
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }

                // Expenses list
                items(expenses, key = { it.id }) { expense ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->
                            when (value) {
                                SwipeToDismissBoxValue.EndToStart -> { viewModel.deleteExpense(expense); true }
                                SwipeToDismissBoxValue.StartToEnd -> { onEdit(expense); false }
                                else -> false
                            }
                        }
                    )
                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            val direction = dismissState.dismissDirection
                            Row(modifier = Modifier.fillMaxWidth().height(72.dp)) {
                                // Edit (left)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxSize()
                                        .background(
                                            if (direction == SwipeToDismissBoxValue.StartToEnd) MaterialTheme.colorScheme.tertiaryContainer else Color.Transparent
                                        )
                                        .padding(horizontal = 16.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        text = "Edit",
                                        color = if (direction == SwipeToDismissBoxValue.StartToEnd) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                // Delete (right)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxSize()
                                        .background(
                                            if (direction == SwipeToDismissBoxValue.EndToStart) MaterialTheme.colorScheme.errorContainer else Color.Transparent
                                        )
                                        .padding(horizontal = 16.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Text(
                                        text = "Delete",
                                        color = if (direction == SwipeToDismissBoxValue.EndToStart) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    ) {
                        ElevatedCard(modifier = Modifier.fillMaxWidth().shadow(1.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = categoryEmoji(expense.category), style = MaterialTheme.typography.titleLarge)
                                    Spacer(Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = expense.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = buildString {
                                                append(expense.category)
                                                if (expense.person.isNotBlank()) append(" · ${expense.person}")
                                            },
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = expense.date.toString(),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Text(
                                    text = "₹ %.2f".format(expense.amount),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showBudgetDialog) {
            AlertDialog(
                onDismissRequest = { showBudgetDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        val amount = budgetText.toDoubleOrNull() ?: 0.0
                        val now = java.time.LocalDate.now()
                        viewModel.setMonthlyBudget(now.year, now.month.value, amount)
                        showBudgetDialog = false
                    }) { Text("Save") }
                },
                dismissButton = { TextButton(onClick = { showBudgetDialog = false }) { Text("Cancel") } },
                title = { Text("Monthly Budget") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = budgetText,
                            onValueChange = { budgetText = it },
                            label = { Text("Amount") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            )
        }

        if (showResetBudgetDialog) {
            AlertDialog(
                onDismissRequest = { showResetBudgetDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val now = java.time.LocalDate.now()
                            viewModel.deleteMonthlyBudget(now.year, now.month.value)
                            showResetBudgetDialog = false
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) { 
                        Text("Reset") 
                    }
                },
                dismissButton = { 
                    TextButton(onClick = { showResetBudgetDialog = false }) { 
                        Text("Cancel") 
                    } 
                },
                title = { Text("Reset Monthly Budget") },
                text = { 
                    Text("Are you sure you want to reset your monthly budget? This will remove the budget completely and cannot be undone.") 
                }
            )
        }
    }
}

@Composable
private fun PersonFilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(24.dp)
    val colors = FilterChipDefaults.filterChipColors(
        containerColor = MaterialTheme.colorScheme.surface,
        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        selectedContainerColor = AccentPrimary,
        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
    )
    val border = FilterChipDefaults.filterChipBorder(
        enabled = true,
        selected = selected,
        borderColor = MaterialTheme.colorScheme.outline,
        selectedBorderColor = AccentPrimary
    )
    FilterChip(
        selected = selected,
        onClick = onClick,
        shape = shape,
        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
        label = {
            Text(
                text = label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium)
            )
        },
        colors = colors,
        border = border,
        modifier = Modifier.defaultMinSize(minWidth = 88.dp)
    )
}

private fun categoryEmoji(category: String): String = when {
    category.contains("grocery", true) || category.contains("grocer", true) -> "🛒"
    category.contains("rent", true) -> "🏠"
    category.contains("dining", true) || category.contains("food", true) || category.contains("restaurant", true) -> "🍔"
    category.contains("transport", true) || category.contains("cab", true) || category.contains("auto", true) -> "🚕"
    category.contains("fuel", true) || category.contains("petrol", true) -> "⛽"
    category.contains("mobile", true) || category.contains("internet", true) -> "📶"
    category.contains("medical", true) || category.contains("health", true) -> "💊"
    category.contains("education", true) || category.contains("tuition", true) -> "🎓"
    category.contains("utilities", true) || category.contains("electric", true) || category.contains("water", true) -> "💡"
    else -> "💸"
}

