[中文](../zh/structure.md) | [English](../en/structure.md) | [日本語](structure.md)

## プロジェクト構造とファイルの役割

### ルートディレクトリ

- `backend`：バックエンドソースコード（Java）
  - CLIおよびWeb APIを提供します
  - ルールの読み込み、docxの解析、モデルの呼び出し、xlsxの生成、ログの書き込みを担当します
- `frontend`：フロントエンドソースコード（Vue）
  - Web UIの開発にのみ使用されます。リリース時、フロントエンドはjarにパッケージ化されます
- `executable`：すぐに使えるディレクトリ
  - `spec-qc-*.jar`：実行可能なjar
  - `config.yaml`：実行設定（DeepSeek/ポート/work_dir）
  - `start.bat`：Windows起動スクリプト（パラメータなしの場合はデフォルトでwebを起動します。パラメータはjarに渡されます）
  - `init_work.bat`：作業ディレクトリを初期化するWindowsスクリプト（`work/input/output/quality/logs/revise` などを作成します）
  - `start.sh`：macOS / Linux起動スクリプト（パラメータなしの場合はデフォルトでwebを起動します。パラメータはjarに渡されます）
  - `init_work.sh`：作業ディレクトリを初期化するmacOS / Linuxスクリプト（`work/input/output/quality/logs/revise` などを作成します）
  - `work`：デフォルトの作業ディレクトリ（サンプルの入力、出力、ルール、ログ）
- `work`：リポジトリルート下の作業ディレクトリ（開発/テストに使用）
- `docs`：ドキュメント

### `executable/work` ディレクトリ

- `input`：スキャン対象の要件Wordドキュメントを配置します（`.docx` のみサポート）
- `output`：スキャン結果（`.xlsx`）とレビューファイル（`.review.json`）
- `quality`：ルールファイル（`.md`）
  - `quality_standard.md`：汎用ルール
  - `banking_quality_standard.md`：銀行業界ルールの例
- `logs`：
  - `spec-qc.log`：スキャンプロセス全体のログ（失敗理由とスタックトレースを含む）
  - `large-model.log`：モデルリクエストログ（デフォルトでは機密内容は記録されません）
- `revise`：「承認」をクリックした後に生成される改訂版docx
- `cache.yaml`：最近のディレクトリ選択用のWeb UIキャッシュ