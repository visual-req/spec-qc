[中文](README.md) | [English](README_en.md) | [日本語](README_ja.md)

# spec-qc（AI 需求质量扫描）

本项目用于批量扫描需求 Word 文档（.docx），输出质量问题到 Excel（.xlsx），并提供本地 Web UI 用于选择目录、查看进度、逐条接受/拒绝问题点、下载结果。

## 目录结构

- backend：后端（Java / Spring Boot / Picocli），负责扫描、规则加载、写 Excel、Web API
- frontend：前端（Vue），编译产物打进后端 jar
- executable：开箱即用目录（config.yaml、spec-qc-*.jar、work 示例目录、Windows 启动脚本）
- work：默认工作目录（input/output/quality/logs/revise/cache.yaml）
- docs：更详细的说明与扩展规则指南

## 快速开始（Web UI）

1) 配置大模型访问（以 DeepSeek 为例）

编辑 `executable/config.yaml`：

```yaml
deepseek:
  base_url: "https://api.deepseek.com/v1"
  api_key: "YOUR_DEEPSEEK_API_KEY"
  model: "deepseek-chat"

server:
  host: "0.0.0.0"
  port: 8765

work_dir: "work"
```

也可用环境变量覆盖：

```bash
export DEEPSEEK_API_KEY="..."
export DEEPSEEK_BASE_URL="https://api.deepseek.com/v1"
export DEEPSEEK_MODEL="deepseek-chat"
export SPEC_QC_WORK_DIR="/abs/path/to/work"
```

2) 启动

macOS / Linux：

```bash
cd executable
java -jar spec-qc-0.1.0.jar web
```

Windows：

```bat
cd executable
start.bat
```

浏览器访问：

- http://localhost:8765/

## 命令行扫描（CLI）

```bash
cd executable
java -jar spec-qc-0.1.0.jar scan -req /path/to/req_dir
```

可选参数：

- `--out /path/to/out_dir`：输出目录（默认会落到可写 work/output）
- `--rules /path/to/rules_dir`：自定义规则目录（目录下可放多个 .md 规则文件）

## 规则与行业边界

- 通用规则：work/quality/quality_standard.md
- 行业规则示例（银行）：work/quality/banking_quality_standard.md
- 每个 quality 文件开头需要声明“适用行业”，扫描时会避免跨行业误扫（可用环境变量 `SPEC_QC_INDUSTRY` 指定行业）

如何新增规则与文件格式见：

- `docs/zh/rules.md`

## 日志与排障

- 扫描全流程日志：work/logs/spec-qc.log（包含扫描中途失败原因、文件名、规则段、异常堆栈）
- 模型调用日志：work/logs/large-model.log（不记录 api_key；默认不记录敏感内容）

常见问题与解决方式见：

- `docs/zh/troubleshooting.md`

更多结构说明见：

- `docs/zh/structure.md`
- 图示：`docs/zh/work-principle.md` / `docs/zh/scan-process.md` / `docs/zh/quality-tuning.md`

## 安全提示

- 不要把真实 `deepseek.api_key` 或 `DEEPSEEK_API_KEY` 提交到仓库；推荐用环境变量注入
