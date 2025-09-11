import SwiftUI

struct ExpensesListView: View {
    @State private var expenses: [Expense] = []
    @State private var showAdd = false
    @State private var filterPerson: String = ""
    @State private var filterMonth: String = "" // YYYY-MM

    var body: some View {
        NavigationStack {
            List {
                ForEach(expenses) { exp in
                    VStack(alignment: .leading, spacing: 4) {
                        HStack {
                            Text(exp.title)
                                .font(.headline)
                            Spacer()
                            Text(exp.amount, format: .currency(code: Locale.current.currency?.identifier ?? "USD"))
                                .font(.subheadline)
                        }
                        HStack(spacing: 8) {
                            if let cat = exp.category, !cat.isEmpty {
                                Label(cat, systemImage: "tag")
                                    .font(.caption)
                                    .foregroundStyle(.secondary)
                            }
                            if let person = exp.person, !person.isEmpty {
                                Label(person, systemImage: "person")
                                    .font(.caption)
                                    .foregroundStyle(.secondary)
                            }
                            if let d = exp.spentDate {
                                Label(d, format: .dateTime.year().month().day().hour().minute(), systemImage: "calendar")
                                    .font(.caption)
                                    .foregroundStyle(.secondary)
                            }
                        }
                    }
                    .swipeActions {
                        Button(role: .destructive) {
                            delete(expense: exp)
                        } label: {
                            Label("Delete", systemImage: "trash")
                        }
                    }
                }
            }
            .navigationTitle("Expenses")
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Menu {
                        TextField("Person", text: $filterPerson)
                        TextField("Month YYYY-MM", text: $filterMonth)
                        Button("Apply Filters") { load() }
                        Button("Clear") {
                            filterPerson = ""; filterMonth = ""; load()
                        }
                    } label: {
                        Label("Filter", systemImage: "line.3.horizontal.decrease.circle")
                    }
                }
                ToolbarItem(placement: .topBarTrailing) {
                    Button {
                        showAdd = true
                    } label: {
                        Label("Add", systemImage: "plus")
                    }
                }
            }
            .sheet(isPresented: $showAdd) {
                AddEditExpenseView { new in
                    do {
                        var toInsert = new
                        toInsert.spentAtISO = toInsert.spentAtISO.isEmpty ? ISO8601DateFormatter().string(from: Date()) : toInsert.spentAtISO
                        let newId = try DatabaseService.shared.insert(expense: toInsert)
                        var saved = toInsert
                        saved.id = newId
                        expenses.insert(saved, at: 0)
                    } catch {
                        print("Insert failed: \(error)")
                    }
                }
                .presentationDetents([.medium, .large])
            }
            .onAppear { load() }
        }
    }

    private func load() {
        do {
            expenses = try DatabaseService.shared.list(person: filterPerson.isEmpty ? nil : filterPerson,
                                                       month: filterMonth.isEmpty ? nil : filterMonth)
        } catch {
            print("Load failed: \(error)")
        }
    }

    private func delete(expense: Expense) {
        guard let id = expense.id else { return }
        do {
            try DatabaseService.shared.delete(expenseId: id)
            expenses.removeAll { $0.id == id }
        } catch {
            print("Delete failed: \(error)")
        }
    }
}

struct AddEditExpenseView: View {
    var onSave: (Expense) -> Void

    @Environment(\.dismiss) private var dismiss
    @State private var title: String = ""
    @State private var amount: String = ""
    @State private var category: String = ""
    @State private var person: String = ""
    @State private var date = Date()

    var body: some View {
        NavigationStack {
            Form {
                Section("Details") {
                    TextField("Title", text: $title)
                    TextField("Amount", text: $amount)
                        .keyboardType(.decimalPad)
                    TextField("Category", text: $category)
                    TextField("Person", text: $person)
                    DatePicker("When", selection: $date, displayedComponents: [.date, .hourAndMinute])
                }
            }
            .navigationTitle("Add Expense")
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Button("Cancel") { dismiss() }
                }
                ToolbarItem(placement: .topBarTrailing) {
                    Button("Save") { save() }.disabled(!canSave)
                }
            }
        }
    }

    private var canSave: Bool {
        !title.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty && Double(amount) != nil
    }

    private func save() {
        guard let amt = Double(amount) else { return }
        let iso = ISO8601DateFormatter().string(from: date)
        let new = Expense(id: nil, title: title.trimmingCharacters(in: .whitespacesAndNewlines), amount: amt, category: category.isEmpty ? nil : category, spentAtISO: iso, person: person.isEmpty ? nil : person)
        onSave(new)
        dismiss()
    }
}


