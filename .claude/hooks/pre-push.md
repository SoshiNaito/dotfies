---
name: pre-push
description: プッシュ前に最終チェックを実行します
---

# プッシュ前フック

## 実行するチェック

### 1. ブランチ確認
```bash
# 現在のブランチを確認
git branch --show-current
```
- `main`/`master`への直接プッシュは警告
- 保護ブランチへのプッシュは確認を求める

### 2. コミット履歴確認
```bash
# プッシュされるコミットを表示
git log origin/$(git branch --show-current)..HEAD --oneline
```
- WIPコミットがないか確認
- fixup/squashコミットがないか確認

### 3. テスト実行
```bash
npm test
```
- 全テストがパスすることを確認
- カバレッジが閾値を満たすか確認

### 4. Lintチェック
```bash
npm run lint
```
- Lintエラーがないことを確認

### 5. ビルド確認
```bash
npm run build
```
- ビルドが成功することを確認
- TypeScriptの型エラーがないことを確認

### 6. セキュリティスキャン
```bash
npm audit --production
```
- 本番依存関係に高リスクの脆弱性がないことを確認

### 7. コミットメッセージ確認
プッシュされるコミットが:
- Conventional Commits形式に従っているか
- 意味のあるメッセージか

## チェック結果レポート

```
🚀 プッシュ前チェック

📋 チェック結果:
✅ ブランチ: feature/new-feature (OK)
✅ コミット: 3件 (WIPなし)
✅ テスト: 45件パス
✅ Lint: エラーなし
✅ ビルド: 成功
⚠️  セキュリティ: 低リスク1件

📊 変更サマリー:
- 変更ファイル: 8
- 追加: +250行
- 削除: -50行

🔗 プッシュ先: origin/feature/new-feature

続行しますか？ [Y/n]
```

## 問題検出時の対応

### WIPコミットがある場合
```
⚠️  WIPコミットが含まれています:
   abc1234 WIP: 認証機能

💡 提案:
- git rebase -i でコミットを整理
- git commit --amend でメッセージを修正
```

### テスト失敗の場合
```
❌ テストが失敗しました

失敗したテスト:
1. UserService › createUser › should hash password
   Expected: true
   Received: false
   at src/services/user.test.ts:42

💡 プッシュをキャンセルしました。テストを修正してください。
```

### mainブランチへの直接プッシュ
```
⚠️  警告: mainブランチへ直接プッシュしようとしています

💡 提案:
- feature/xxx ブランチを作成してPRを経由することを推奨
- どうしても必要な場合は明示的に確認

続行しますか？ [y/N]
```

## スキップ条件
以下の場合はチェックをスキップ可能:
- `--no-verify` フラグが指定された場合
- CI環境での自動プッシュ
- hotfixブランチの緊急デプロイ
