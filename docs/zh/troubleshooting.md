[中文](troubleshooting.md) | [English](../en/troubleshooting.md) | [日本語](../ja/troubleshooting.md)

## 常见问题与排障

### 1) 扫描中途失败，如何定位原因

优先查看日志：

- `work/logs/spec-qc.log`：扫描全流程（会记录失败发生在哪个文件、哪段规则、以及异常堆栈）
- `work/logs/large-model.log`：模型请求与响应摘要（默认不记录敏感内容）

### 2) Model output is not valid JSON

原因：模型返回被截断或未按约定输出 JSON。

当前策略：

- 提示词限制输出规模（issues 条数与字段长度）
- 解析器对截断 JSON 做了尽力修复（补齐括号/回退截断尾部）

如果仍频繁出现：

- 降低单次规则量（减少规则文件规模，或拆分规则文件）
- 检查网络/代理导致的响应截断
- 打开敏感日志仅用于短期排障：
  - `SPEC_QC_LOG_SENSITIVE=true`

### 3) 连接不上大模型 / 请求超时

现象：

- 扫描到“请求模型”阶段后失败，报错包含 `request failed` / `ConnectException` / `timeout`

处理机制：

- 单次请求使用连接超时与请求超时控制（连接建立失败/超时会抛异常）
- 当前文件会被标记为“失败”，并把错误信息写入输出 xlsx 的错误行
- 详细错误栈会写入 `work/logs/spec-qc.log`，请求事件与摘要会写入 `work/logs/large-model.log`

排查建议：

- 检查 `deepseek.base_url` 是否可达（企业网络/代理/内网网关场景尤其常见）
- 检查 `DEEPSEEK_API_KEY` 是否已设置且有效
- 若需要代理，确保 Java 进程已配置代理（例如通过系统代理或为 Java 设置 http/https 代理参数）

### 3) Operation not permitted / 无法写入输出

原因：目标输出目录不可写（macOS 隐私权限/沙盒目录/企业策略）。

解决：

- 在 Web UI 里把输出目录改到可写路径
- 或设置 `SPEC_QC_WORK_DIR` 指向可写 work 目录

### 4) .doc 文件不支持

当前仅支持 `.docx`。遇到 `.doc` 会标记失败并写入错误信息，请先转换为 `.docx`。
