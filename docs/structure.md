## 项目结构与文件作用

### 根目录

- backend：后端源码（Java）
  - 提供 CLI 与 Web API
  - 负责加载规则、解析 docx、调用模型、生成 xlsx、写入日志
- frontend：前端源码（Vue）
  - 仅用于开发 Web UI；发布时前端已打包进 jar
- executable：开箱即用目录
  - spec-qc-*.jar：可执行 jar
  - config.yaml：运行配置（DeepSeek/端口/work_dir）
  - start.bat/init_work.bat：Windows 启动/初始化脚本
  - start.sh：macOS / Linux 启动脚本（无参数默认启动 web；参数会透传给 jar）
  - init_work.sh：macOS / Linux 初始化工作目录脚本（创建 work/input/output/quality/logs/revise 等）
  - work：默认工作目录（示例输入、输出、规则、日志）
- work：仓库根目录下的工作目录（用于开发/测试）
- docs：文档

### executable/work 目录

- input：放待扫描的需求 Word（仅支持 .docx）
- output：扫描结果（.xlsx）与 review 文件（.review.json）
- quality：规则文件（.md）
  - quality_standard.md：通用规则
  - banking_quality_standard.md：银行行业规则示例
- logs：
  - spec-qc.log：扫描全流程日志（含失败原因与堆栈）
  - large-model.log：模型请求日志（默认不记录敏感内容）
- revise：点击“接受”后生成的修订版 docx
- cache.yaml：Web UI 最近一次目录选择缓存
