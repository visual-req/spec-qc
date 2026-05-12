[中文](README.md) | [English](README_en.md) | [日本語](README_ja.md)

# spec-qc (AI Requirement Quality Scan)

This project is used for batch scanning requirement Word documents (`.docx`), outputting quality issues to Excel (`.xlsx`), and providing a local Web UI for directory selection, progress tracking, issue acceptance/rejection, and result downloading.

## Directory Structure

- `backend`: Backend (Java / Spring Boot / Picocli), handles scanning, rule loading, Excel writing, Web API
- `frontend`: Frontend (Vue), build artifacts are bundled into the backend jar
- `executable`: Out-of-the-box directory (`config.yaml`, `spec-qc-*.jar`, `work` example directory, Windows startup scripts)
- `work`: Default working directory (`input`/`output`/`quality`/`logs`/`revise`/`cache.yaml`)
- `docs`: Detailed documentation and extended rule guidelines

## Quick Start (Web UI)

1) Configure Large Model Access (e.g., DeepSeek)

Edit `executable/config.yaml`:

```yaml
llm:
  base_url: "https://api.deepseek.com/v1"
  api_key: "YOUR_DEEPSEEK_API_KEY"
  model: "deepseek-chat"

server:
  host: "0.0.0.0"
  port: 8765

work_dir: "work"
```

You can also use environment variables to override:

```bash
export LLM_API_KEY="..."
export LLM_BASE_URL="https://api.deepseek.com/v1"
export LLM_MODEL="deepseek-chat"
export SPEC_QC_WORK_DIR="/abs/path/to/work"
```

2) Start

macOS / Linux:

```bash
cd executable
java -jar spec-qc-0.1.0.jar web
```

Windows:

```bat
cd executable
start.bat
```

Access in browser:

- http://localhost:8765/

## Command Line Scan (CLI)

```bash
cd executable
java -jar spec-qc-0.1.0.jar scan -req /path/to/req_dir
```

Optional parameters:

- `--out /path/to/out_dir`: Output directory (defaults to writable `work/output`)
- `--rules /path/to/rules_dir`: Custom rules directory (can contain multiple `.md` rule files)

## Rules and Industry Boundaries

- General rules: `work/quality/quality_standard.md`
- Industry rules example (Banking): `work/quality/banking_quality_standard.md`
- Each quality file needs to declare "Applicable Industry" at the beginning to avoid cross-industry false positives (you can specify the industry using the `SPEC_QC_INDUSTRY` environment variable).

For details on how to add new rules and file formats, see:

- `docs/en/rules.md`

## Logs and Troubleshooting

- Full scanning process logs: `work/logs/spec-qc.log` (includes mid-scan failure reasons, file names, rule segments, exception stacks)
- Model call logs: `work/logs/large-model.log` (does not log `api_key`; sensitive content is not logged by default)

For common issues and solutions, see:

- `docs/en/troubleshooting.md`

For more structural details, see:

- `docs/en/structure.md`

## Security Warning

- Do not commit the real `llm.api_key` or `LLM_API_KEY` to the repository; injecting it via environment variables is recommended.