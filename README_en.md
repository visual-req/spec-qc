<h3 align="center">spec-review</h3>
<p align="center">SpecQC: a rule + LLM based requirement quality checker that batch-scans <code>.docx</code>, outputs issues, and provides a local Web UI for review and export.</p>
<p align="center">
  <a href="https://github.com/visual-req/spec-review/releases"><img src="https://img.shields.io/github/v/release/visual-req/spec-review" alt="Release"></a>
  <a href="https://github.com/visual-req/spec-review"><img src="https://img.shields.io/github/stars/visual-req/spec-review?style=flat-square" alt="Stars"></a>
  <a href="https://github.com/visual-req/spec-review/issues"><img src="https://img.shields.io/github/issues/visual-req/spec-review?style=flat-square" alt="Issues"></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/license-MIT-blue?style=flat-square" alt="License"></a>
</p>
<p align="center">
  <a href="README_en.md">English</a> · <a href="README.md">中文</a> · <a href="README_ja.md">日本語</a>
  <br/>
  <a href="docs/en/getting-started.md">Getting started</a> · <a href="docs/en/manual.md">Manual</a> · <a href="docs/en/rules.md">Rules</a> · <a href="docs/en/troubleshooting.md">Troubleshooting</a>
</p>
<hr />

Version: 0.1.0 · License: MIT ([LICENSE](LICENSE))

## Why This Is Reliable

- Rule-driven: findings are constrained by rule files rather than unconstrained free-form generation
- Traceable: each issue keeps evidence snippets and location info for quick human verification
- Regressable: after tuning rules/prompts, you can re-scan historical samples to reduce false positives and misses

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
