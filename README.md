

---

## 💰 Expense Tracker (Standalone App)

The repository also includes a fully featured Expense Tracker tailored for an Indian middle-class family context. It is intentionally separated from the calculator and runs from `expenses_app.py` with its own routes and pages.

### 🏃 Run Locally (Expense Tracker)

1. Install dependencies
   ```bash
   pip install -r requirements.txt
   ```
2. Start the Expense Tracker on a custom port (recommended 5051)
   ```bash
   PORT=5051 FLASK_ENV=development python expenses_app.py
   ```
3. Open the UI
   - Visit `https://vineeth-expense-tracker-84i6.vercel.app/expenses`

Notes:
- Uses SQLite for storage (created automatically).
- Exports supported: CSV and Excel (XLSX). `openpyxl` is included in `requirements.txt`.
- API respects filters like `person` and `month` (e.g., `/api/expenses?person=vineeth&month=2025-09`).

### 📊 Features
- Category budgets with progress bars per month and person
- Multi-person support with person filter
- Monthly salary input and spent % indicator (negative when overspent)
- Pie chart by category (Chart.js) with amount and percentage tooltips
- CSV and Excel exports that respect current filters

### 🚀 Deploy on Vercel (Expense Tracker)

`vercel.json` is preconfigured to serve `expenses_app.py`:

1. Install Vercel CLI (optional)
   ```bash
   npm i -g vercel
   ```
2. Deploy
   ```bash
   vercel
   # or
   vercel --prod
   ```

After deployment, open your Vercel URL and visit `/expenses` (e.g., `https://your-app.vercel.app/expenses`).
