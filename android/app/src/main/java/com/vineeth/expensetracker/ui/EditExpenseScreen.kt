package com.vineeth.expensetracker.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
fun EditExpenseScreen(
    initialTitle: String,
    initialAmount: Double,
    initialCategory: String,
    initialDate: String,
    initialPerson: String,
    onSave: (title: String, amount: Double, category: String, date: LocalDate, person: String) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }
    var amountText by remember { mutableStateOf(initialAmount.toString()) }
    var category by remember { mutableStateOf(initialCategory) }
    var dateText by remember { mutableStateOf(initialDate) }
    var person by remember { mutableStateOf(initialPerson) }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = amountText, onValueChange = { amountText = it }, label = { Text("Amount") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = dateText, onValueChange = { dateText = it }, label = { Text("Date (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = person, onValueChange = { person = it }, label = { Text("Person") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))
            Button(onClick = {
                val amount = amountText.toDoubleOrNull() ?: 0.0
                val date = runCatching { LocalDate.parse(dateText) }.getOrElse { LocalDate.now() }
                onSave(title, amount, category, date, person)
            }, modifier = Modifier.fillMaxWidth()) { Text("Save Changes") }
            Spacer(Modifier.height(8.dp))
            Button(onClick = onCancel, modifier = Modifier.fillMaxWidth()) { Text("Cancel") }
        }
    }
}
