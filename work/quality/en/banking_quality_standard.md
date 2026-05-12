## Banking Quality Standards (Example)

### Applicable Industries
- Applicable industry: Banking/Finance
- Industry boundary: Enable only when the scanned requirement belongs to the banking/finance industry; otherwise skip by default.

### Rule List

#### 1. Monetary fields must be clearly defined (amount/currency/precision)
- Category: Unclear definition
- Description: For amount-related fields, if currency, precision, rounding rules, and display format are not defined, implementation and reconciliation may diverge.
- Bad example: amount=100 (currency/precision not specified); fee calculated by ratio (rounding not specified)
- Recommendation: Specify currency (CNY/USD), precision (decimal places), rounding rules (round half up / floor / banker’s rounding), display & storage formats, and provide boundary examples.
- When to use: Transaction amount, fees, interest, penalty interest, discounts, refunds, reconciliation, clearing, etc.

#### 2. Time fields must be clearly defined (posting date/trade date/settlement date)
- Category: Unclear definition
- Description: In banking, concepts like “posting date / trade date / settlement date / interest start date / interest payment date” differ. Mixing them or leaving them undefined leads to inconsistent flows, reconciliation, and reporting definitions.
- Bad example: “Based on trade date” (trade date not defined); “T+1 posting” (day-cut definition not specified)
- Recommendation: Define each time field’s meaning, source, time zone, and day-cut. For T+N, specify T0 and the calendar type (natural day / business day / trading day).
- When to use: Posting, reconciliation, clearing, interest accrual, fund posting, reporting/statistics
