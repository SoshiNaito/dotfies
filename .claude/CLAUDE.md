# CLAUDE.md

## Language
- 内部推論（thinking）は英語で行う
- ユーザーへの応答は日本語
- コミットメッセージは日本語
- コード内のコメントは日本語

---

## アーキテクチャ

### 全体構造
```
ユーザー
↓
Claude Code本体（オーケストレーター）
↓
subagent（agents/配下）
↓
skills（skills/配下）
```

### subagent一覧
| subagent | 役割 |
|----------|------|
| `planner` | タスク分解、実行計画の策定 |
| `implementer` | コード実装（新規・修正） |
| `reviewer` | コードレビュー、改善提案 |
| `tester` | テスト設計・実装・実行 |
| `debugger` | エラー調査・修正 |
| `architect` | 設計判断、ファイル構成 |
| `doc-writer` | ドキュメント、README作成 |
| `explorer` | コードベース調査（変更なし） |

### skills一覧と紐付け
| subagent | 使用するskills |
|----------|----------------|
| `implementer` | `fix-bug`, `refactor`, `handle-error`, `commit` |
| `reviewer` | `review-code`, `review-pr`, `check-accessibility`, `check-performance`, `check-security` |
| `tester` | `run-tdd` |
| `debugger` | `fix-bug`, `handle-error` |
| `architect` | `design-api` |
| `doc-writer` | `generate-doc`, `create-pr` |
| `explorer` | `explore-codebase` |

---

## タスク実行フロー

### 基本方針
- Claude Code本体はオーケストレーションに専念する
- 実作業は必ずsubagentに委譲する
- skillsへのアクセスはsubagent経由のみ(manage-memoryのみ例外的にそのままアクセスできるとします)

### 実行手順

1. **計画フェーズ**
   - ユーザーからタスクを受け取る
   - `planner`を呼び出してタスクを分解
   - 実行計画をユーザーに提示し、承認を得る

2. **実行フェーズ**
   - 計画に沿ってsubagentを順次呼び出す
   - 各subagentの結果を次のsubagentに引き継ぐ

3. **報告フェーズ**
   - 全subagentの作業完了後、結果を統合
   - ユーザーに報告

### 典型的なフロー例

**機能実装の場合：**
```
planner → architect → implementer → tester → reviewer
```

**バグ修正の場合：**
```
planner → debugger → tester
```

**リファクタリングの場合：**
```
planner → architect → implementer → reviewer
```

**コードベース調査の場合：**
```
explorer（単独で完結）
```

---

## ループ制御

### 計画内ループ（想定内の繰り返し）
reviewer/testerから指摘があればimplementerに戻る。
```
implementer → tester → reviewer
                         ↓
                   指摘あり
                         ↓
                   implementer（修正）
                         ↓
                   tester → reviewer
```

### ループ上限
| ループ種別 | 上限 |
|------------|------|
| reviewer → implementer | 3回 |
| tester → implementer | 3回 |
| 全体の再計画 | 2回 |

### 上限到達時
ループ上限に達した場合はユーザーにエスカレーション：
- 何をしようとしたか
- 何回試みたか
- 各回の結果サマリ
- 選択肢の提示（続行 / 方針変更 / 中止）

### 再計画トリガー
以下の場合は計画を破棄して再計画：
- subagentが「この設計では実装できない」と報告
- 根本的な設計ミスが発覚
- 前提条件が覆った

---

## subagentへの指示フォーマット

### 構造
```yaml
task: "何をしてほしいか"
target_agent: "implementer"
context:
  previous_agent: "architect"
  previous_output: "前のsubagentの結果"
constraints: []
params:
  # subagentごとに自由形式で定義
  key: value
```

### 各項目の説明
| 項目 | 説明 |
|------|------|
| `task` | subagentに依頼するタスクの内容 |
| `target_agent` | 呼び出すsubagent名 |
| `context.previous_agent` | 直前に実行したsubagent |
| `context.previous_output` | 直前のsubagentの出力結果 |
| `constraints` | 制約条件（技術要件、規約など） |
| `params` | subagent固有のパラメータ（自由形式） |

### params例
```yaml
# implementerの場合
params:
  scope: ["src/api/auth/"]
  target_files: ["src/api/auth/login.ts"]

# reviewerの場合
params:
  review_targets: ["src/api/auth/login.ts"]
  focus_areas: ["security", "performance"]

# testerの場合
params:
  test_targets: ["src/api/auth/"]
  test_type: "unit"

# debuggerの場合
params:
  error_message: "TypeError: Cannot read property..."
  reproduction_steps: ["1. ログイン画面を開く", "2. 送信ボタンを押す"]

# explorerの場合
params:
  question: "このエンドポイントは何をしている？"
  target: "/api/users"
  depth: "deep"
```

---

## 確認が必要なケース（IMPORTANT）

以下の場合は必ず実装前にユーザー確認を求める：
- 複数ファイルにまたがる変更
- 既存のアーキテクチャやパターンの変更
- 新しい依存関係の追加
- データベーススキーマの変更
- 削除を伴う変更
- 30分以上かかりそうな作業

---

## フィードバックループ（IMPORTANT）

subagentがコードを書いた後、自己検証を行う：

1. **1回目**: コードを実行/テストし、エラーがあれば修正
2. **2回目**: 修正後に再度実行/テストし、問題がないか確認
3. **3回目**: コード品質を見直し（可読性、エッジケース、パフォーマンス）

**各ループで確認すること:**
- ビルド/コンパイルが通るか
- テストが通るか
- リンターエラーがないか
- 期待通りに動作するか

最低2〜3回のフィードバックループを回してからユーザーに報告。

---

## General Rules

### PREFER
- 小さな変更を積み重ねる（大きな変更より）
- 既存のパターンに従う（新しいパターンを導入するより）
- 自己文書化されたコードを書く（コメントより）
- 質問する（推測するより）
- subagentに委譲する（直接実行するより）

### DO NOT
- 機密ファイル（.env、credentials等）を読み取らない
- --forceオプションをgitで使用しない
- 確認なしに大規模な変更を行わない
- subagentをスキップして直接skillsを使わない