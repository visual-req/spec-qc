[中文](README.md) | [English](README_en.md) | [日本語](README_ja.md)

# spec-qc（AI 要件品質スキャン）

このプロジェクトは、要件定義のWordドキュメント（`.docx`）を一括スキャンし、品質の問題をExcel（`.xlsx`）に出力し、ローカルのWeb UIを提供して、ディレクトリの選択、進捗の確認、問題の個別承認/拒否、結果のダウンロードを行うためのものです。

## ディレクトリ構成

- `backend`：バックエンド（Java / Spring Boot / Picocli）。スキャン、ルールの読み込み、Excelの書き込み、Web APIを担当
- `frontend`：フロントエンド（Vue）。ビルド成果物はバックエンドのjarに組み込まれます
- `executable`：すぐに使えるディレクトリ（`config.yaml`、`spec-qc-*.jar`、`work`サンプルディレクトリ、Windows起動スクリプト）
- `work`：デフォルトの作業ディレクトリ（`input`/`output`/`quality`/`logs`/`revise`/`cache.yaml`）
- `docs`：詳細な説明と拡張ルールのガイドライン

## クイックスタート（Web UI）

1) 大規模モデルアクセスの設定（DeepSeekを例に）

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

環境変数で上書きすることもできます：

```bash
export LLM_API_KEY="..."
export LLM_BASE_URL="https://api.deepseek.com/v1"
export LLM_MODEL="deepseek-chat"
export SPEC_QC_WORK_DIR="/abs/path/to/work"
```

2) 起動

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

ブラウザでアクセス：

- http://localhost:8765/

## コマンドラインスキャン（CLI）

```bash
cd executable
java -jar spec-qc-0.1.0.jar scan -req /path/to/req_dir
```

オプションパラメータ：

- `--out /path/to/out_dir`：出力ディレクトリ（デフォルトでは書き込み可能な `work/output` に保存されます）
- `--rules /path/to/rules_dir`：カスタムルールディレクトリ（ディレクトリ内に複数の `.md` ルールファイルを配置可能）

## ルールと業界の境界

- 汎用ルール：`work/quality/quality_standard.md`
- 業界ルールの例（銀行）：`work/quality/banking_quality_standard.md`
- 各品質ファイルの先頭に「適用業界」を宣言し、スキャン時の業界外の誤スキャンを防ぎます（環境変数 `SPEC_QC_INDUSTRY` で業界を指定可能）。

ルールの追加方法とファイルフォーマットについては、以下を参照してください：

- `docs/ja/rules.md`

## ログとトラブルシューティング

- スキャン全体のログ：`work/logs/spec-qc.log`（途中の失敗理由、ファイル名、ルールのセグメント、例外スタックを含む）
- モデル呼び出しログ：`work/logs/large-model.log`（`api_key`は記録されません。デフォルトでは機密内容も記録されません）

よくある質問と解決方法については、以下を参照してください：

- `docs/ja/troubleshooting.md`

構造の詳細については、以下を参照してください：

- `docs/ja/structure.md`

## セキュリティに関する注意

- 実際の `llm.api_key` または `LLM_API_KEY` をリポジトリにコミットしないでください。環境変数を使用して注入することをお勧めします。