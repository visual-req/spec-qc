[中文](../zh/installation.md) | [English](../en/installation.md) | [日本語](installation.md)

## インストールと環境構築

### 1) 実行環境（すぐに使えるjar）

- Java：JDK/JRE 17以降が必要です
- ネットワーク：DeepSeek API（または設定した `base_url`）にアクセスできること

Javaの確認：

```bash
java -version
```

### 2) 実行可能パッケージディレクトリの取得

リポジトリにはすでに `executable/` がすぐに使えるディレクトリとして提供されています（`config.yaml`、`spec-qc-*.jar`、サンプルの `work/` が含まれています）。

重要なファイル：

- `executable/config.yaml`：実行設定（DeepSeek/ポート/work_dir）
- `executable/spec-qc-*.jar`：実行可能なjar
- `executable/work/`：デフォルトの作業ディレクトリ（`input`/`output`/`quality`/`logs`/`revise`/`cache.yaml`）

### 3) LLM の設定

`executable/config.yaml` を編集します：

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

環境変数を使用してキーを注入することを推奨します：

```bash
export LLM_API_KEY="..."
```

オプションの環境変数：

- `SPEC_QC_WORK_DIR`：作業ディレクトリを上書きします（絶対パスがより確実です）
- `SPEC_QC_HOST` / `SPEC_QC_PORT`：Webのリッスンアドレスとポートを上書きします
- `SPEC_QC_INDUSTRY`：現在のスキャン業界を指定します（ルールの境界に使用されます）

### 4) ソースからのビルド（オプション）

バックエンド/フロントエンドをカスタマイズして再パッケージする必要がある場合のみ使用します。

- Java 17 + Maven
- Node.js（フロントエンドリソースのビルドに使用）

リポジトリのルートディレクトリでバックエンドをビルドします（これによりフロントエンドのビルドもトリガーされます）：

```bash
mvn -f backend/pom.xml -DskipTests package
```

### 5) Dockerでの実行（オプション）

リポジトリのルートディレクトリにある `Dockerfile` を使用してイメージをビルドします（コンテナ内でフロントエンドとバックエンドのパッケージングを完了し、実行可能なjarを生成します）：

```bash
docker build -t spec-qc .
```

Webモードの例（推奨：トークンをgitにコミットするのを避けるため、設定と作業ディレクトリをリポジトリの外部に配置します）：

```bash
docker run --rm \
  -p 8765:8765 \
  -e SPEC_QC_CONFIG="/data/config.yaml" \
  -e LLM_API_KEY="..." \
  -v "/abs/path/to/spec-qc-data:/data" \
  spec-qc
```

`/abs/path/to/spec-qc-data/` ディレクトリには以下を含めることをお勧めします：

- `config.yaml`（`work_dir: "work"` と記述でき、このファイルからの相対パス `/data/work` として解決されます）
- `work/`（`input`/`output`/`quality`/`logs`/`revise`/`cache.yaml`）

CLIスキャンの例（イメージのデフォルトコマンドを上書きし、scanサブコマンドを直接実行します）：

```bash
docker run --rm \
  -e LLM_API_KEY="..." \
  -v "/abs/path/to/req_dir:/data/req:ro" \
  -v "/abs/path/to/out_dir:/data/out" \
  -v "/abs/path/to/rules_dir:/data/rules:ro" \
  spec-qc scan -req /data/req --out /data/out --rules /data/rules
```