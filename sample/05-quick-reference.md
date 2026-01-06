# クイックリファレンス

よく使うコマンドとスキルの早見表です。

## コマンド一覧

| コマンド | 用途 | 例 |
|---------|------|-----|
| `/setup-feature <name>` | 新機能開発を開始 | `/setup-feature user-auth` |
| `/commit` | コミットメッセージ生成 | `/commit` |
| `/create-pr <title>` | PR作成 | `/create-pr 認証機能の追加` |
| `/fix-bug <description>` | バグ修正開始 | `/fix-bug #123 ログインエラー` |
| `/refactor <path>` | リファクタリング | `/refactor src/services/` |
| `/doc <path>` | ドキュメント生成 | `/doc src/api/` |
| `/review-pr <number>` | PRレビュー | `/review-pr 42` |

## エージェント

| エージェント | 役割 | 自動起動 |
|-------------|------|---------|
| feature-planner | 実装計画を作成 | /setup-feature時 |
| feature-implementer | TDDで実装 | 計画承認後 |
| code-reviewer | コードレビュー | 実装完了後 |

## スキル

| スキル | 用途 | 自動ロード条件 |
|--------|------|---------------|
| security-check | セキュリティ検証 | 認証・パスワード処理時 |
| tdd-workflow | TDDガイド | テスト作成時 |
| error-handling | 例外処理パターン | try-catch実装時 |
| performance | 最適化チェック | パフォーマンス議論時 |
| accessibility | a11yガイド | UI実装時 |
| api-design | REST API設計 | API実装時 |

## よく使うフレーズ

### 開発開始
```
新しい機能を追加したい → /setup-feature <機能名>
バグを修正したい → /fix-bug <説明>
コードを改善したい → /refactor <パス>
```

### 開発中
```
進捗を確認 → 「進捗を教えて」
計画を承認 → 「この計画で進めてください」
テスト実行 → 「テストを実行して」
```

### 完了時
```
コミット → /commit
PRを作成 → /create-pr <タイトル>
レビュー → /review-pr <番号>
```

## Gitブランチ命名規則

```
feature/<機能名>    # 新機能
fix/<issue番号>     # バグ修正
refactor/<対象>     # リファクタリング
docs/<内容>         # ドキュメント
```

## コミットメッセージ形式

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Type一覧
| Type | 用途 |
|------|------|
| feat | 新機能 |
| fix | バグ修正 |
| refactor | リファクタリング |
| docs | ドキュメント |
| test | テスト |
| chore | その他 |

### 例
```
feat(auth): ログイン機能を追加

- JWTトークンによる認証
- リメンバーミー機能
- パスワードリセット

Closes #123
```

## セキュリティチェックリスト

実装時に確認:

- [ ] パスワード → bcrypt (rounds=12)
- [ ] SQL → パラメータ化クエリ
- [ ] HTML出力 → エスケープ
- [ ] Cookie → HTTP-only, Secure
- [ ] API → 認証・認可チェック
- [ ] 入力 → バリデーション
- [ ] 機密情報 → 環境変数

## テストカバレッジ目標

| 種類 | 目標 |
|------|------|
| ステートメント | > 80% |
| ブランチ | > 75% |
| 関数 | > 80% |
| 行 | > 80% |

## パフォーマンスチェック

- [ ] N+1クエリなし
- [ ] 必要なカラムのみSELECT
- [ ] 適切なインデックス
- [ ] ページネーション
- [ ] 必要に応じてキャッシュ

## トラブルシューティング

### コマンドが動かない
```bash
# .claudeディレクトリ確認
ls -la .claude/

# ファイル権限確認
chmod -R 755 .claude/
```

### MCPサーバーエラー
```bash
# 環境変数確認
echo $GITHUB_TOKEN

# npx確認
npx --version
```

### テスト失敗
```bash
# 単体で実行
npm test -- --watch

# 特定ファイル
npm test -- path/to/test.ts
```
