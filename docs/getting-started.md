## Getting Started

### 1) 准备工作目录

使用 `executable/` 内置脚本创建工作目录（Windows / macOS / Linux）：

macOS / Linux：

```bash
cd executable
sh init_work.sh
```

Windows：

```bat
cd executable
init_work.bat
```

脚本会在 `executable/work/` 下创建这些目录（如已存在则跳过）：

- `work/input`：放待扫描的需求 Word（.docx）
- `work/output`：输出结果（.xlsx 与 .review.json）
- `work/quality`：规则文件（.md）
- `work/revise`：接受问题后生成修订版 docx

把待扫描的需求 Word（.docx）复制到工作目录的 `input/` 下，例如：

- `executable/work/input/`

把规则文件复制到 `quality/` 下：

- `docs/quality_standard.md`（或仓库根目录 `work/quality/quality_standard.md`）→ `executable/work/quality/quality_standard.md`
- 可选行业规则（如银行）→ `executable/work/quality/`

工作目录结构说明见：

- `docs/structure.md`

### 2) 启动 Web UI

```bash
cd executable
java -jar spec-qc-0.1.0.jar web
```

或直接用脚本启动（macOS / Linux）：

```bash
cd executable
sh start.sh
```

使用外部配置文件（推荐：避免把 token 写进代码库）：

```bash
export SPEC_QC_CONFIG="$HOME/spec-qc/config.yaml"
export DEEPSEEK_API_KEY="..."
cd executable
sh start.sh
```

修改端口号：

- 改配置文件：编辑 `executable/config.yaml` 的 `server.port`
- 或用环境变量覆盖：`export SPEC_QC_PORT=9000`

浏览器访问：

- http://localhost:8765/

在页面中选择：

- 需求目录（req_dir）
- 输出目录（out_dir）
- 规则目录（rules_dir）

### 3) 使用 CLI 扫描

```bash
cd executable
java -jar spec-qc-0.1.0.jar scan -req /path/to/req_dir
```

或直接用脚本运行（命令行参数会透传给 jar）：

```bash
cd executable
sh start.sh scan -req /path/to/req_dir
```

常用参数：

- `--out /path/to/out_dir`：输出目录（默认会落到可写 work/output）
- `--rules /path/to/rules_dir`：规则目录（可放多个 .md 规则文件）

### 4) 查看输出与日志

- 扫描结果：`work/output/*.xlsx`
- 审核文件：`work/output/*.review.json`
- 接受后修订版：`work/revise/*.docx`

日志：

- 扫描全流程：`work/logs/spec-qc.log`
- 模型调用：`work/logs/large-model.log`

排障见：

- `docs/troubleshooting.md`
