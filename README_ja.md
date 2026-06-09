<h3 align="center">spec-review</h3>
<p align="center">SpecQC：ルール + LLM による要件品質チェックツール。<code>.docx</code> を一括スキャンして指摘を出力し、ローカル Web UI でレビューとエクスポートを行えます。</p>
<p align="center">
  <a href="https://github.com/visual-req/spec-review/releases"><img src="https://img.shields.io/github/v/release/visual-req/spec-review" alt="Release"></a>
  <a href="https://github.com/visual-req/spec-review"><img src="https://img.shields.io/github/stars/visual-req/spec-review?style=flat-square" alt="Stars"></a>
  <a href="https://github.com/visual-req/spec-review/issues"><img src="https://img.shields.io/github/issues/visual-req/spec-review?style=flat-square" alt="Issues"></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/license-MIT-blue?style=flat-square" alt="License"></a>
</p>
<p align="center">
  <a href="README_en.md">English</a> · <a href="README.md">中文</a> · <a href="README_ja.md">日本語</a>
  <br/>
  <a href="docs/ja/getting-started.md">はじめに</a> · <a href="docs/ja/manual.md">マニュアル</a> · <a href="docs/ja/rules.md">ルール</a> · <a href="docs/ja/troubleshooting.md">トラブルシューティング</a>
</p>
<hr />

Version: 0.1.0 · License: MIT ([LICENSE](LICENSE))

## 信頼性の理由

- ルール駆動：ルールファイルで判定基準を拘束し、自由生成だけに依存しない
- 追跡可能：各指摘に根拠の抜粋と位置情報が残り、人手で素早く確認できる
- 回帰可能：ルール/プロンプト調整後に過去サンプルで再スキャンし、誤検知と見逃しを継続的に低減できる

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
