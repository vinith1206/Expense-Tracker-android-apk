package com.vineeth.expensetracker.ui

fun recognizeCategoryForTitle(title: String): String {
    val t = title.lowercase()
    return when {
        listOf("grocery", "groceries", "supermarket", "mart", "bigbasket", "blinkit").any { it in t } -> "Groceries"
        listOf("rent", "lease").any { it in t } -> "Rent"
        listOf("electric", "electricity", "water", "sewage", "gas", "bill", "utility").any { it in t } -> "Utilities"
        listOf("school", "college", "tuition", "course", "exam", "udemy", "coursera").any { it in t } -> "Education"
        listOf("uber", "ola", "bus", "metro", "train", "taxi", "cab", "auto", "flight", "ticket").any { it in t } -> "Transport"
        listOf("fuel", "petrol", "diesel", "gasoline").any { it in t } -> "Fuel"
        listOf("med", "hospital", "clinic", "pharmacy", "chemist", "doctor").any { it in t } -> "Medical"
        listOf("emi", "loan", "mortgage").any { it in t } -> "EMI/Loans"
        listOf("mobile", "internet", "broadband", "fiber", "recharge", "wifi").any { it in t } -> "Mobile/Internet"
        listOf("restaurant", "dining", "dine", "cafe", "coffee", "food", "swiggy", "zomato").any { it in t } -> "Dining Out"
        listOf("household", "cleaning", "detergent", "utensil", "home needs").any { it in t } -> "Household"
        listOf("insurance", "premium").any { it in t } -> "Insurance"
        listOf("saving", "deposit", "rd", "fd", "sip").any { it in t } -> "Savings"
        else -> "Other"
    }
}
