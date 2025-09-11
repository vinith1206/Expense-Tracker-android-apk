import Foundation
import SQLite

final class DatabaseService {
    static let shared = DatabaseService()

    private let db: Connection
    private let expenses = Table("expenses")

    private let id = Expression<Int64>("id")
    private let title = Expression<String>("title")
    private let amount = Expression<Double>("amount")
    private let category = Expression<String?>("category")
    private let spentAt = Expression<String>("spent_at")
    private let person = Expression<String?>("person")

    private init() {
        do {
            let url = DatabaseService.ensureDatabaseInDocuments()
            db = try Connection(url.path)
            try self.createTablesIfNeeded()
        } catch {
            fatalError("Database init failed: \(error)")
        }
    }

    private func createTablesIfNeeded() throws {
        try db.run(expenses.create(ifNotExists: true) { t in
            t.column(id, primaryKey: .autoincrement)
            t.column(title)
            t.column(amount)
            t.column(category)
            t.column(spentAt)
            t.column(person)
        })
    }

    static func ensureDatabaseInDocuments() -> URL {
        let fm = FileManager.default
        let docs = fm.urls(for: .documentDirectory, in: .userDomainMask).first!
        let dst = docs.appendingPathComponent("expenses.db")

        if !fm.fileExists(atPath: dst.path) {
            if let bundled = Bundle.main.url(forResource: "expenses", withExtension: "db") {
                try? fm.copyItem(at: bundled, to: dst)
            }
        }
        return dst
    }

    func list(person: String? = nil, month: String? = nil) throws -> [Expense] {
        var query = expenses.order(Expression<String>("spent_at").desc, id.desc)
        if let person = person, !person.isEmpty {
            query = query.filter(self.person == person)
        }
        if let month = month, let (start, end) = monthBounds(month) {
            let spentExpr = Expression<String>("spent_at")
            query = query.filter(spentExpr >= start && spentExpr < end)
        }
        return try db.prepare(query).map { row in
            Expense(
                id: row[id],
                title: row[title],
                amount: row[amount],
                category: row[category],
                spentAtISO: row[spentAt],
                person: row[person]
            )
        }
    }

    func insert(expense: Expense) throws -> Int64 {
        let insert = expenses.insert(
            title <- expense.title,
            amount <- expense.amount,
            category <- expense.category,
            spentAt <- expense.spentAtISO,
            person <- expense.person
        )
        return try db.run(insert)
    }

    func update(expense: Expense) throws {
        guard let expenseId = expense.id else { return }
        let row = expenses.filter(id == expenseId)
        try db.run(row.update(
            title <- expense.title,
            amount <- expense.amount,
            category <- expense.category,
            spentAt <- expense.spentAtISO,
            person <- expense.person
        ))
    }

    func delete(expenseId: Int64) throws {
        let row = expenses.filter(id == expenseId)
        _ = try db.run(row.delete())
    }
}

func monthBounds(_ yearMonth: String) -> (String, String)? {
    let comps = yearMonth.split(separator: "-")
    guard comps.count == 2, let year = Int(comps[0]), let month = Int(comps[1]), month >= 1, month <= 12 else {
        return nil
    }
    var start = DateComponents()
    start.year = year
    start.month = month
    start.day = 1
    let cal = Calendar(identifier: .gregorian)
    guard let startDate = cal.date(from: start) else { return nil }
    let nextDate = cal.date(byAdding: .month, value: 1, to: startDate)!
    let iso = ISO8601DateFormatter()
    return (iso.string(from: startDate), iso.string(from: nextDate))
}


