[中文](../zh/structure.md) | [English](structure.md) | [日本語](../ja/structure.md)

## Project Structure and File Roles

### Root Directory

- `backend`: Backend source code (Java)
  - Provides CLI and Web API
  - Responsible for loading rules, parsing docx, calling the model, generating xlsx, and writing logs
- `frontend`: Frontend source code (Vue)
  - Used only for developing the Web UI; the frontend is packaged into the jar upon release
- `executable`: Out-of-the-box directory
  - `spec-qc-*.jar`: Executable jar
  - `config.yaml`: Runtime configuration (DeepSeek/port/work_dir)
  - `start.bat`: Windows startup script (starts web by default without parameters; parameters are passed to the jar)
  - `init_work.bat`: Windows script to initialize the working directory (creates `work/input/output/quality/logs/revise`, etc.)
  - `start.sh`: macOS / Linux startup script (starts web by default without parameters; parameters are passed to the jar)
  - `init_work.sh`: macOS / Linux script to initialize the working directory (creates `work/input/output/quality/logs/revise`, etc.)
  - `work`: Default working directory (example inputs, outputs, rules, logs)
- `work`: Working directory under the repository root (used for development/testing)
- `docs`: Documentation

### `executable/work` Directory

- `input`: Place requirement Word documents to be scanned here (only `.docx` is supported)
- `output`: Scan results (`.xlsx`) and review files (`.review.json`)
- `quality`: Rule files (`.md`)
  - `quality_standard.md`: General rules
  - `banking_quality_standard.md`: Banking industry rules example
- `logs`:
  - `spec-qc.log`: Full scan process log (including failure reasons and stack traces)
  - `large-model.log`: Model request log (does not log sensitive content by default)
- `revise`: Revised docx generated after clicking "Accept"
- `cache.yaml`: Web UI cache for the most recent directory selection