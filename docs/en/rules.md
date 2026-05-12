[中文](../zh/rules.md) | [English](rules.md) | [日本語](../ja/rules.md)

## How to Add/Maintain Inspection Rules

### Where to Put Rule Files

- General rule file: `work/quality/quality_standard.md`
- Industry rule files: Recommended to be placed in the same directory, for example:
  - `work/quality/banking_quality_standard.md`
- You can specify an additional rules directory via parameters at runtime:
  - CLI: `java -jar spec-qc-*.jar scan -req ... --rules /path/to/rules_dir`
  - Web UI: Fill in the "Rules Directory" field

### Industry Boundaries (Avoid Cross-Industry False Positives)

Each quality file needs to declare the applicable industry at the beginning, for example:

```md
### Applicable Industry
- 适用行业：银行/金融
```
*(Note: Keep the Chinese prefix `适用行业：` as the parser might rely on it, depending on the implementation. Or adjust accordingly).*

- For general rules, it is recommended to write: `适用行业：通用（跨行业）` (Applicable Industry: General (Cross-Industry))
- When scanning, you can specify the current scanning industry via an environment variable:

```bash
export SPEC_QC_INDUSTRY="银行"
```

Rule files that do not match the industry will be skipped (general rules are always available).

### Suggested Format for a Single Rule

It is recommended to keep a unified structure for rule blocks to facilitate stable model hits and output:

```md
#### 123. Rule Title (Clearly state the trigger condition and the nature of the issue)
- Issue Category: XXX
- Keywords: Keywords "A/B/C"
- Issue Description: One sentence explaining why it is an issue
- Error Example: Give 1-2 typical examples (the more specific, the better)
- Solution: Provide an actionable way to complete/fix it (request output in table format `suggestion_html` if necessary)
- Solution Benefit: Optional
- Application Scenario: Optional (used to constrain and avoid generalized false positives)
- Related Quality Standard: Accuracy/Completeness/Consistency/Maintainability...
```

### Asking the "Suggestion Section" to Output as a Table

If you want the suggestion for a rule to be presented as a table, explicitly request it in the rule text and describe the table columns:

- Glossary/Terminology: Request a glossary table (Term/Definition/Scope/Example/Remarks)
- Permissions: Request an RBAC matrix and a Data Permission matrix
- Boundaries/Exceptions: Request a list of boundary conditions and expected behaviors
- User Journey/Object Journey: Request a journey matrix (horizontal axis for lifecycle)

The system side also enforces mandatory table output for some specific rules (see backend prompt constraints).