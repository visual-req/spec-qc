# Quality Tuning

![Quality Tuning](../assets/quality-tuning.svg)

Key points:

- Rule tuning: split/supplement rule items, add industry rules, reduce false positives and cross-industry misjudgments
- Prompts and parameters: chunking strategy, output format constraints, examples and terminology alignment to improve consistency and actionability
- Iteration loop: scan output → human review → adjust rules/prompts → re-scan for verification

## Adding Industry Rules (Recommended)

- Add an industry rule file under `work/quality` (example: `banking_quality_standard.md`) and declare the applicable industry at the beginning (e.g., `适用行业：银行/金融`) to avoid cross-industry false positives
- Group rules by quality dimensions (e.g., use `####` as dimension headings). Each rule should include problem description / counterexample / solution / benefit / applicable scenarios for stable model hits
- After adding or adjusting rules, run regression scans on a set of historical sample documents, and iterate via a feedback loop: “rejection reason → rule adjustment”
