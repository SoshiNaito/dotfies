# Claude Code 設定サンプル集

このディレクトリには、dotfilesに含まれるClaude Code設定の使用例が含まれています。

## 目次

1. [セットアップ](#セットアップ)
2. [コマンドの使用例](#コマンドの使用例)
3. [エージェントの活用](#エージェントの活用)
4. [スキルの参照](#スキルの参照)
5. [ワークフロー例](#ワークフロー例)

---

## セットアップ

### 1. dotfilesをシンボリックリンク

```bash
# ホームディレクトリにリンク
ln -sf ~/src/github.com/SoshiNaito/dotfies/.claude ~/.claude

# または、プロジェクトごとにコピー
cp -r ~/src/github.com/SoshiNaito/dotfies/.claude /path/to/your/project/
```

### 2. MCP Serversの環境変数設定

```bash
# ~/.zshrc または ~/.bashrc に追加
export GITHUB_TOKEN="ghp_xxxxxxxxxxxx"
export DATABASE_URL="postgres://user:pass@localhost:5432/dbname"
export SLACK_BOT_TOKEN="xoxb-xxxxxxxxxxxx"
export SLACK_TEAM_ID="T0123456789"
```

### 3. 動作確認

```bash
cd your-project
claude

# 設定が読み込まれているか確認
> /help
```

---

## コマンドの使用例

### `/commit` - コミットメッセージ自動生成

```bash
# 変更をステージング
git add .

# Claudeにコミットを依頼
> /commit

# Claudeが変更内容を分析し、Conventional Commits形式で提案
# 例: feat(auth): ログイン機能にリメンバーミー追加
```

**出力例:**
```
📝 変更内容を分析しました:

変更ファイル: 3
- src/components/LoginForm.tsx (+45, -2)
- src/hooks/useAuth.ts (+20, -5)
- src/types/auth.ts (+8, -0)

提案するコミットメッセージ:
---
feat(auth): ログイン機能にリメンバーミー追加

- チェックボックスUIを追加
- トークン有効期限を30日に延長
- セキュアcookieに保存

Closes #42
---

このメッセージでコミットしますか？ [Y/n]
```

### `/fix-bug` - バグ修正ワークフロー

```bash
# バグの説明を添えて実行
> /fix-bug ログイン時に500エラーが発生する

# Claudeが以下を実行:
# 1. エラーログを検索
# 2. 関連コードを特定
# 3. 再現テストを作成
# 4. 修正を実装
# 5. テストがパスすることを確認
```

### `/refactor` - リファクタリング支援

```bash
# 対象ファイルを指定
> /refactor src/services/userService.ts

# Claudeが以下を実行:
# 1. コードを分析
# 2. 問題点を特定（長い関数、重複など）
# 3. リファクタリング計画を提示
# 4. ユーザー承認後に実行
```

### `/doc` - ドキュメント生成

```bash
# 対象を指定
> /doc src/api/users.ts

# 生成されるもの:
# - JSDoc/TSDocコメント
# - OpenAPI仕様
# - 使用例
```

### `/review-pr` - PRレビュー

```bash
# PR番号を指定
> /review-pr 123

# Claudeが以下を実行:
# 1. PRの差分を取得
# 2. コード品質チェック
# 3. セキュリティチェック
# 4. レビューコメントを作成
```

### `/setup-feature` - 新機能開発開始

```bash
# 機能名を指定
> /setup-feature user-profile

# Claudeが以下を実行:
# 1. feature/user-profile ブランチを作成
# 2. feature-plannerで実装計画を作成
# 3. ユーザー承認を待つ
# 4. feature-implementerでTDD実装
# 5. code-reviewerでレビュー
```

### `/create-pr` - PR作成

```bash
# PRタイトルを指定
> /create-pr ユーザープロフィール機能の追加

# 自動的に以下を実行:
# 1. テスト・Lint・セキュリティチェック
# 2. 変更内容のコミット
# 3. リモートにプッシュ
# 4. PRを作成（説明文も自動生成）
```

---

## エージェントの活用

### feature-planner（設計担当）

```bash
> 新しいAPI認証機能を計画して

# 出力例:
📋 実装計画
目標: JWT認証を使用したAPI認証の実装

ステップ:
1. JWTライブラリのインストール（5分）
2. 認証ミドルウェアの作成（20分）
3. ログインエンドポイントの実装（30分）
4. テストの作成（20分）

変更が必要なファイル:
| ファイルパス | 変更内容 | 優先度 |
|-------------|---------|--------|
| src/middleware/auth.ts | 新規作成 | 高 |
| src/api/auth.ts | ログインAPI | 高 |
...
```

### feature-implementer（実装担当）

```bash
> 上記の計画を実装して

# TDDサイクルで実装:
# RED: テスト作成 → 失敗確認
# GREEN: 最小実装 → テストパス
# REFACTOR: コード改善
```

### code-reviewer（レビュー担当）

```bash
> 今の変更をレビューして

# 出力例:
✅ 承認項目
- 適切なエラーハンドリング
- 型定義が正確

🔧 必須の変更
1. [高] パスワードのバリデーション追加
   場所: src/api/auth.ts:42

💡 提案
- ログインリトライ制限の追加を検討
```

---

## スキルの参照

### security-check（自動ロード）

認証やパスワード処理を行うと自動的にロードされます:

```bash
> ログイン機能を実装して

# 自動的にsecurity-checkスキルを参照
# チェック項目:
# - bcryptでハッシュ化（rounds >= 12）
# - JWT署名の強度
# - HTTP-only cookie使用
# - CSRF対策
```

### tdd-workflow

テスト駆動開発のガイドとして参照されます:

```bash
> ユーザー作成機能をTDDで実装して

# TDDサイクルを自動的に適用:
# 1. 失敗するテストを作成
# 2. 最小実装でパス
# 3. リファクタリング
```

### error-handling

例外処理を実装する際に参照されます:

```bash
> APIのエラーハンドリングを改善して

# スキルから以下を参照:
# - カスタムエラークラスの設計
# - Expressエラーミドルウェア
# - Result型パターン
```

### performance

パフォーマンス最適化時に参照されます:

```bash
> このクエリを最適化して

# スキルから以下を参照:
# - N+1問題の解決
# - インデックス設計
# - キャッシング戦略
```

### accessibility

UIコンポーネント実装時に参照されます:

```bash
> アクセシブルなフォームを作って

# スキルから以下を参照:
# - セマンティックHTML
# - ARIA属性
# - キーボードナビゲーション
```

### api-design

APIエンドポイント設計時に参照されます:

```bash
> ユーザーAPIを設計して

# スキルから以下を参照:
# - RESTful URL設計
# - HTTPステータスコード
# - レスポンス形式
```

---

## ワークフロー例

### 例1: 新機能の追加（フルフロー）

```bash
# 1. 機能開発を開始
> /setup-feature shopping-cart

# 2. 計画の確認・承認
> この計画で進めてください

# 3. 実装中の質問に回答
> カート内の商品数上限は50にしてください

# 4. 実装完了後、レビュー
> /review-pr

# 5. 修正があれば対応
> レビューの指摘を修正して

# 6. PRを作成
> /create-pr ショッピングカート機能の追加
```

### 例2: バグ修正（クイックフロー）

```bash
# 1. バグ修正開始
> /fix-bug 商品価格が0円で表示される #issue-456

# 2. 修正確認
> 全テストを実行して

# 3. コミット
> /commit

# 4. PR作成
> /create-pr Fix: 商品価格の表示バグを修正
```

### 例3: リファクタリング

```bash
# 1. 対象を分析
> /refactor src/services/orderService.ts

# 2. 計画を確認
> この計画で進めてください

# 3. 段階的に実行
> 最初のステップを実行して

# 4. 各ステップ後にテスト
> テストを実行して

# 5. 完了後コミット
> /commit
```

### 例4: ドキュメント作成

```bash
# 1. API仕様書を生成
> /doc src/api/

# 2. READMEを更新
> READMEにAPIの使用例を追加して

# 3. コミット
> /commit
```

---

## トラブルシューティング

### 設定が読み込まれない

```bash
# .claudeディレクトリの存在確認
ls -la .claude/

# パーミッション確認
chmod -R 755 .claude/
```

### MCPサーバーが動作しない

```bash
# 環境変数の確認
echo $GITHUB_TOKEN

# npxが利用可能か確認
npx --version
```

### コマンドが見つからない

```bash
# コマンドファイルの存在確認
ls .claude/commands/

# ファイル形式の確認（UTF-8、LF改行）
file .claude/commands/commit.md
```

---

## 次のステップ

1. 実際のプロジェクトで設定を試す
2. 自分のワークフローに合わせてカスタマイズ
3. チーム用の追加コマンド/スキルを作成

詳細は各設定ファイルのコメントを参照してください。
