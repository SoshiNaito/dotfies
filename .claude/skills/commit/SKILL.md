---
name: commit
description: 変更内容を分析し、適切なコミットメッセージを生成してコミットします。
---

# Commit Skill

変更内容を分析し、適切なコミットメッセージを生成してコミットします。

## 手順

1. `git status` と `git diff --staged` で変更内容を確認
2. 変更の種類を分類:
   - feat: 新機能
   - fix: バグ修正
   - refactor: リファクタリング
   - docs: ドキュメント
   - test: テスト
   - chore: その他
3. Conventional Commits形式でメッセージを生成
4. ユーザーに確認を求める
5. 承認後にコミット実行

オプション: $ARGUMENTS

## コミットメッセージ形式
```
<type>(<scope>): <subject>

<body>

<footer>
```

## 例
```
feat(auth): ログイン機能にリメンバーミー追加

- チェックボックスUIを追加
- トークン有効期限を30日に延長
- セキュアcookieに保存

Closes #123
```

今すぐ変更内容を分析してコミットメッセージを提案してください。
