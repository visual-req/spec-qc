[中文](../zh/getting-started.md) | [English](getting-started.md) | [日本語](../ja/getting-started.md)

## Getting Started

### 1) Prepare the Working Directory

Use the built-in scripts in `executable/` to create the working directory (Windows / macOS / Linux):

macOS / Linux:

```bash
cd executable
sh init_work.sh
```

Windows:

```bat
cd executable
init_work.bat
```

The script will create these directories under `executable/work/` (skips if they already exist):

- `work/input`: Place the requirement Word documents (`.docx`) to be scanned here.
- `work/output`: Output results (`.xlsx` and `.review.json`).
- `work/quality`: Rule files (`.md`).
- `work/revise`: Generates a revised docx after accepting issues.

Copy the requirement Word documents (`.docx`) to be scanned into the `input/` directory of the working directory, for example:

- `executable/work/input/`

Copy the rule files into `quality/`:

- `docs/quality_standard.md` (or `work/quality/quality_standard.md` in the repository root) → `executable/work/quality/quality_standard.md`
- Optional industry rules (e.g., banking) → `executable/work/quality/`

For an explanation of the working directory structure, see:

- `structure.md`

### 2) Start the Web UI

```bash
cd executable
java -jar spec-qc-0.1.0.jar web
```

Or start directly with the script (macOS / Linux):

```bash
cd executable
sh start.sh
```

Using an external configuration file (Recommended: avoids hardcoding tokens in the codebase):

```bash
export SPEC_QC_CONFIG="$HOME/spec-qc/config.yaml"
export LLM_API_KEY="..."
cd executable
sh start.sh
```

To modify the port number:

- Modify the configuration file: Edit `server.port` in `executable/config.yaml`
- Or override with an environment variable: `export SPEC_QC_PORT=9000`

Access in browser:

- http://localhost:8765/

Select the following in the page:

- Requirement directory (`req_dir`)
- Output directory (`out_dir`)
- Rules directory (`rules_dir`)

### 3) Scanning using CLI

```bash
cd executable
java -jar spec-qc-0.1.0.jar scan -req /path/to/req_dir
```

Or run directly with the script (command line parameters are passed through to the jar):

```bash
cd executable
sh start.sh scan -req /path/to/req_dir
```

Common parameters:

- `--out /path/to/out_dir`: Output directory (defaults to writable `work/output`)
- `--rules /path/to/rules_dir`: Rules directory (can contain multiple `.md` rule files)

### 4) View Outputs and Logs

- Scan results: `work/output/*.xlsx`
- Review files: `work/output/*.review.json`
- Revised versions after acceptance: `work/revise/*.docx`

Logs:

- Full scanning process: `work/logs/spec-qc.log`
- Model calls: `work/logs/large-model.log`

For troubleshooting, see:

- `troubleshooting.md`