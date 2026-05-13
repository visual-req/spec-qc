# Scanning Process

![Scanning Process](../assets/scan-process.svg)

Key points:

- Entry: Web UI or CLI specifies `req_dir`
- Run: load rules and config → extract and chunk text → call the model per chunk → aggregate and de-duplicate
- Output: generate downloadable results, and write progress/logs to help locate failure reasons
