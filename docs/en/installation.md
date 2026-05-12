[中文](../zh/installation.md) | [English](installation.md) | [日本語](../ja/installation.md)

## Installation and Environment Setup

### 1) Runtime Environment (Out-of-the-box jar)

- Java: Requires JDK/JRE 17+
- Network: Can access DeepSeek API (or your configured `base_url`)

Verify Java:

```bash
java -version
```

### 2) Get the Executable Package Directory

The repository already provides `executable/` as an out-of-the-box directory (containing `config.yaml`, `spec-qc-*.jar`, and an example `work/`).

Key files:

- `executable/config.yaml`: Runtime configuration (DeepSeek/port/work_dir)
- `executable/spec-qc-*.jar`: Executable jar
- `executable/work/`: Default working directory (`input`/`output`/`quality`/`logs`/`revise`/`cache.yaml`)

### 3) Configure Large Language Model (LLM)

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

It is recommended to inject the key using environment variables:

```bash
export LLM_API_KEY="..."
```

Optional environment variables:

- `SPEC_QC_WORK_DIR`: Overrides the working directory (absolute path is more reliable)
- `SPEC_QC_HOST` / `SPEC_QC_PORT`: Overrides Web listening address and port
- `SPEC_QC_INDUSTRY`: Specifies the current scanning industry (used for rule boundaries)

### 4) Build from Source (Optional)

Only necessary if you need to customize the backend/frontend and repackage.

- Java 17 + Maven
- Node.js (for building frontend resources)

Build the backend in the repository root directory (this will trigger the frontend build):

```bash
mvn -f backend/pom.xml -DskipTests package
```

### 5) Run with Docker (Optional)

Use the `Dockerfile` in the repository root directory to build the image (it will complete the frontend and backend packaging inside the container and produce a runnable jar):

```bash
docker build -t spec-qc .
```

Web mode example (Recommended: put configuration and working directories outside the repository to avoid committing tokens to git):

```bash
docker run --rm \
  -p 8765:8765 \
  -e SPEC_QC_CONFIG="/data/config.yaml" \
  -e LLM_API_KEY="..." \
  -v "/abs/path/to/spec-qc-data:/data" \
  spec-qc
```

The `/abs/path/to/spec-qc-data/` directory should ideally contain:

- `config.yaml` (you can write `work_dir: "work"`, which will be resolved relative to this file as `/data/work`)
- `work/` (`input`/`output`/`quality`/`logs`/`revise`/`cache.yaml`)

CLI scan example (overrides the default image command and directly runs the scan subcommand):

```bash
docker run --rm \
  -e LLM_API_KEY="..." \
  -v "/abs/path/to/req_dir:/data/req:ro" \
  -v "/abs/path/to/out_dir:/data/out" \
  -v "/abs/path/to/rules_dir:/data/rules:ro" \
  spec-qc scan -req /data/req --out /data/out --rules /data/rules
```