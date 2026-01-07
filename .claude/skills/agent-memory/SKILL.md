# エージェントメモリ

会話をまたいで残る知識を保存するための永続的なメモリ空間です。

**保存場所:** `.claude/skills/agent-memory/memories/`

## 積極的な活用

保存する価値のある発見があったときにメモリを保存してください：
- 調査に手間がかかった研究結果
- コードベースの分かりにくいパターンや落とし穴
- 厄介な問題の解決策
- アーキテクチャの決定とその理由
- 後で再開する可能性のある作業中のタスク

関連する作業を始めるときにメモリを確認してください：
- 問題のある箇所を調査する前
- 以前触れた機能に取り組むとき
- 会話が途切れた後に作業を再開するとき

必要に応じてメモリを整理してください：
- 同じトピックに関する散らばったメモリを統合する
- 古くなったり置き換えられた情報を削除する
- 作業が完了、ブロック、中止されたときにstatusフィールドを更新する

## フォルダ構成

可能な限り、メモリをカテゴリフォルダに整理してください。決まった構造はありません。内容に合ったカテゴリを自由に作成してください。

ガイドライン：
- フォルダ名とファイル名にはケバブケースを使用
- 知識ベースが増えたら統合や再編成を行う

例：
```text
memories/
├── file-processing/
│   └── large-file-memory-issue.md
├── dependencies/
│   └── iconv-esm-problem.md
└── project-context/
    └── december-2025-work.md
```

これは一例です。実際の内容に応じて自由に構成してください。

## フロントマター

すべてのメモリには`summary`フィールドを含むフロントマターが必要です。summaryは、全文を読む必要があるかどうか判断できる程度に簡潔にしてください。

**必須：**
```yaml
---
summary: "このメモリの内容を1〜2行で説明"
created: 2025-01-15  # YYYY-MM-DD形式
---
```

**任意：**
```yaml
---
summary: "大きなファイル処理時のワーカースレッドのメモリリーク - 原因と解決策"
created: 2025-01-15
updated: 2025-01-20
status: in-progress  # in-progress | resolved | blocked | abandoned
tags: [performance, worker, memory-leak]
related: [src/core/file/fileProcessor.ts]
---
```

## 検索ワークフロー

関連するメモリを効率的に見つけるために、summaryを優先的に検索してください：

```bash
# 1. カテゴリ一覧を表示
ls .claude/skills/agent-memory/memories/

# 2. すべてのsummaryを表示
rg "^summary:" .claude/skills/agent-memory/memories/ --no-ignore --hidden

# 3. キーワードでsummaryを検索
rg "^summary:.*キーワード" .claude/skills/agent-memory/memories/ --no-ignore --hidden -i

# 4. タグで検索
rg "^tags:.*キーワード" .claude/skills/agent-memory/memories/ --no-ignore --hidden -i

# 5. 全文検索（summary検索で不十分な場合）
rg "キーワード" .claude/skills/agent-memory/memories/ --no-ignore --hidden -i

# 6. 関連があれば特定のメモリファイルを読む
```

**注意:** メモリファイルはgitignoreされているため、ripgrepでは`--no-ignore`と`--hidden`フラグを使用してください。

## 操作

### 保存

1. 内容に適したカテゴリを決める
2. 既存のカテゴリが合うか確認するか、新しいカテゴリを作成
3. 必須のフロントマター付きでファイルを書き込む（現在の日付には`date +%Y-%m-%d`を使用）

```bash
mkdir -p .claude/skills/agent-memory/memories/category-name/
# 注意: 誤って上書きしないよう、書き込む前にファイルの存在を確認してください
cat > .claude/skills/agent-memory/memories/category-name/filename.md << 'EOF'
---
summary: "このメモリの簡単な説明"
created: 2025-01-15
---

# タイトル

内容をここに...
EOF
```

### メンテナンス

- **更新**: 情報が変わったら、内容を更新しフロントマターに`updated`フィールドを追加
- **削除**: 関連性がなくなったメモリを削除
  ```bash
  trash .claude/skills/agent-memory/memories/category-name/filename.md
  # 空になったカテゴリフォルダを削除
  rmdir .claude/skills/agent-memory/memories/category-name/ 2>/dev/null || true
  ```
- **統合**: 関連するメモリが増えたらマージする
- **再編成**: 知識ベースが増えたら、より適切なカテゴリにメモリを移動

## ガイドライン

1. **自己完結したノートを書く**: 読み手が事前知識なしで内容を理解し行動できるよう、完全なコンテキストを含める
2. **summaryは決定的に**: summaryを読めば詳細が必要かどうか分かるようにする
3. **最新の状態を維持**: 古くなった情報は更新または削除する
4. **実用的に**: すべてではなく、本当に役立つものだけを保存する

## 内容の参考

詳細なメモリを書くときは、以下を含めることを検討してください：
- **コンテキスト**: 目標、背景、制約
- **状態**: 完了したこと、進行中のこと、ブロックされていること
- **詳細**: 重要なファイル、コマンド、コードスニペット
- **次のステップ**: 次にやること、未解決の疑問

すべてのメモリにすべてのセクションが必要なわけではありません。関連するものを使ってください。