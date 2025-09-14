# 💰 Expense Tracker - Android App

A modern Android expense tracking application built with Jetpack Compose, featuring smooth scrolling, budget management, and intuitive user interface.

## 🚀 Features

- **Modern UI**: Built with Jetpack Compose and Material Design 3
- **Smooth Scrolling**: Whole-screen scrolling for better user experience
- **Budget Management**: Set and reset monthly budgets with progress tracking
- **Expense Tracking**: Add, edit, and delete expenses with swipe gestures
- **Category Recognition**: Smart category detection with emoji icons
- **Multi-person Support**: Track expenses for different people
- **Search & Filter**: Search by title, category, or person
- **Date Range Filtering**: Filter by all time, this week, or this month
- **CSV Export**: Export expenses to CSV format
- **Dark/Light Theme**: Toggle between dark and light themes
- **Local Storage**: SQLite database for reliable data persistence

## 🛠️ Technical Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with Repository pattern
- **Database**: Room (SQLite)
- **Dependency Injection**: Manual ViewModel Factory
- **State Management**: StateFlow and Compose State
- **Build System**: Gradle with Kotlin DSL

## 📱 Screenshots

The app features a clean, modern interface with:
- Top app bar with theme toggle and export functionality
- Search bar for quick expense lookup
- Date range filter chips
- Budget card with circular progress indicator
- Person filter chips
- Swipeable expense cards with edit/delete actions

## 🏗️ Project Structure

```
android/
├── app/
│   ├── src/main/
│   │   ├── java/com/vineeth/expensetracker/
│   │   │   ├── data/           # Database entities, DAOs, repositories
│   │   │   ├── ui/             # Compose screens and ViewModels
│   │   │   ├── util/           # Utility classes
│   │   │   ├── MainActivity.kt # Main activity
│   │   │   └── ExpenseTrackerApplication.kt
│   │   ├── res/                # Resources (drawables, strings, etc.)
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

## 🚀 Getting Started

### Prerequisites

- Android Studio Arctic Fox or later
- JDK 11 or later
- Android SDK API level 24 or later

### Installation

1. Clone the repository
2. Open the project in Android Studio
3. Sync the project with Gradle files
4. Run the app on an emulator or physical device

### Building

```bash
cd android
./gradlew assembleDebug
```

### Running Tests

```bash
cd android
./gradlew test
```

## 📊 Database Schema

The app uses Room database with two main entities:

- **ExpenseEntity**: Stores expense data (title, amount, category, date, person)
- **BudgetEntity**: Stores monthly budget information

## 🎨 UI Components

- **ExpensesScreen**: Main screen with expense list and filters
- **AddExpenseScreen**: Screen for adding new expenses
- **EditExpenseScreen**: Screen for editing existing expenses
- **Custom Components**: PersonFilterChip, category emoji mapping

## 🔧 Configuration

The app is configured for:
- Minimum SDK: API 24 (Android 7.0)
- Target SDK: API 34 (Android 14)
- Compile SDK: API 34
- Kotlin version: 2.0.20
- Compose BOM: Latest stable

## 📱 Download APK

You can download the pre-built APK from the releases section and try it yourself!

## 📝 License

This project is for educational and personal use.
