[中文](../zh/getting-started.md) | [English](../en/getting-started.md) | [日本語](getting-started.md)

## Getting Started

### 0) Fork（開発協力）

二次開発や変更の取り込み（PR）を行う場合は、GitHub の Fork ワークフローを推奨します：

- GitHub で Fork：`https://github.com/visual-req/spec-review`
- 自分の Fork をクローン（`<you>` を自分のユーザー名に置き換え）：

```bash
git clone https://github.com/<you>/spec-review.git
cd spec-review
```

- upstream を追加（本家の更新を取り込むため）：

```bash
git remote add upstream https://github.com/visual-req/spec-review.git
git remote -v
```

- ブランチ作成 → コミット → push：

```bash
git checkout -b feat/your-change
git commit -am "..."
git push -u origin feat/your-change
```

- GitHub 上で `visual-req/spec-review` の `main` に向けて Pull Request を作成

### 1) 作業ディレクトリの準備

`executable/` に組み込まれているスクリプトを使用して、作業ディレクトリを作成します（Windows / macOS / Linux）：

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

スクリプトは `executable/work/` の下に以下のディレクトリを作成します（既存の場合はスキップされます）：

- `work/input`：スキャン対象の要件Wordドキュメント（`.docx`）を配置します。
- `work/output`：出力結果（`.xlsx` および `.review.json`）。
- `work/quality`：ルールファイル（`.md`）。
- `work/revise`：問題を承認した後に生成される改訂版のdocx。

スキャン対象の要件Wordドキュメント（`.docx`）を作業ディレクトリの `input/` の下にコピーします。例：

- `executable/work/input/`

ルールファイルを `quality/` の下にコピーします：

- `docs/quality_standard.md`（またはリポジトリルートの `work/quality/quality_standard.md`）→ `executable/work/quality/quality_standard.md`
- オプションの業界ルール（例：銀行）→ `executable/work/quality/`

作業ディレクトリの構成については、以下を参照してください：

- `structure.md`

### 2) Web UI の起動

```bash
cd executable
java -jar spec-qc-0.1.0.jar web
```

または、スクリプトで直接起動します（macOS / Linux）：

```bash
cd executable
sh start.sh
```

外部設定ファイルを使用する（推奨：コードベースにトークンを書き込むのを避けるため）：

```bash
export SPEC_QC_CONFIG="$HOME/spec-qc/config.yaml"
export LLM_API_KEY="..."
cd executable
sh start.sh
```

ポート番号の変更：

- 設定ファイルの変更：`executable/config.yaml` の `server.port` を編集します。
- または環境変数で上書きします：`export SPEC_QC_PORT=9000`

ブラウザでアクセス：

- http://localhost:8765/

ページ内で以下を選択します：

- 要件ディレクトリ（`req_dir`）
- 出力ディレクトリ（`out_dir`）
- ルールディレクトリ（`rules_dir`）

### 3) CLIを使用したスキャン

```bash
cd executable
java -jar spec-qc-0.1.0.jar scan -req /path/to/req_dir
```

またはスクリプトで直接実行します（コマンドライン引数はjarに渡されます）：

```bash
cd executable
sh start.sh scan -req /path/to/req_dir
```

よく使うパラメータ：

- `--out /path/to/out_dir`：出力ディレクトリ（デフォルトでは書き込み可能な `work/output` に保存されます）
- `--rules /path/to/rules_dir`：ルールディレクトリ（複数の `.md` ルールファイルを配置可能）

### 4) 出力とログの確認

- スキャン結果：`work/output/*.xlsx`
- レビューファイル：`work/output/*.review.json`
- 承認後の改訂版：`work/revise/*.docx`

ログ：

- スキャンプロセス全体：`work/logs/spec-qc.log`
- モデル呼び出し：`work/logs/large-model.log`

トラブルシューティングについては、以下を参照してください：

- `troubleshooting.md`
