import Foundation

struct Expense: Identifiable, Codable, Equatable {
    var id: Int64?
    var title: String
    var amount: Double
    var category: String?
    var spentAtISO: String
    var person: String?

    var spentDate: Date? {
        ISO8601DateFormatter().date(from: spentAtISO)
    }
}


