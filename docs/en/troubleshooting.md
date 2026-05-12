[中文](../zh/troubleshooting.md) | [English](troubleshooting.md) | [日本語](../ja/troubleshooting.md)

## Common Issues and Troubleshooting

### 1) Scanning Fails Midway, How to Locate the Cause

Check the logs first:

- `work/logs/spec-qc.log`: Full scanning process (records which file and rule segment failed, along with the exception stack trace).
- `work/logs/large-model.log`: Model request and response summaries (does not log sensitive content by default).

### 2) Model output is not valid JSON

Reason: The model's response was truncated or it did not output JSON as agreed.

Current Strategy:

- The prompt limits the output size (number of issues and field lengths).
- The parser makes a best-effort attempt to repair truncated JSON (adding missing brackets/rolling back truncated ends).

If this still occurs frequently:

- Reduce the number of rules per request (reduce the size of rule files, or split rule files).
- Check for network/proxy issues causing response truncation.
- Turn on sensitive logging purely for short-term troubleshooting:
  - `SPEC_QC_LOG_SENSITIVE=true`

### 3) Cannot Connect to the LLM / Request Timeout

Symptoms:

- Scanning fails at the "request model" stage, error includes `request failed` / `ConnectException` / `timeout`.

Handling Mechanism:

- A single request is controlled by connection timeout and request timeout settings (connection failure/timeout throws an exception).
- The current file will be marked as "failed", and the error message will be written to the error row of the output xlsx.
- Detailed error stacks will be written to `work/logs/spec-qc.log`, and request events/summaries to `work/logs/large-model.log`.

Troubleshooting Suggestions:

- Check if `llm.base_url` is reachable (especially common in corporate networks/proxies/intranet gateways).
- Check if `LLM_API_KEY` is set and valid.
- If a proxy is required, ensure the Java process is configured to use it (e.g., via system proxy or setting Java http/https proxy parameters).

### 4) Operation not permitted / Cannot Write Output

Reason: The target output directory is not writable (macOS privacy permissions/sandbox directories/corporate policies).

Solution:

- Change the output directory to a writable path in the Web UI.
- Or set `SPEC_QC_WORK_DIR` to point to a writable `work` directory.

### 5) .doc Files Are Not Supported

Currently, only `.docx` is supported. Encountering a `.doc` file will mark it as failed and write an error message. Please convert it to `.docx` first.