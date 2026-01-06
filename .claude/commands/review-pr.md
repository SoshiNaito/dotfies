プルリクエストをレビューします。

PR番号: $ARGUMENTS

## ワークフロー

### 1. PR情報取得
```bash
gh pr view $ARGUMENTS --json title,body,author,files,additions,deletions
gh pr diff $ARGUMENTS
```

### 2. 変更ファイル一覧
```bash
gh pr view $ARGUMENTS --json files --jq '.files[].path'
```

### 3. レビュー観点

#### コード品質
- [ ] 命名が明確か
- [ ] 関数が短くシンプルか
- [ ] 重複コードがないか
- [ ] エラーハンドリングが適切か

#### セキュリティ
- [ ] 入力バリデーションがあるか
- [ ] SQLインジェクション対策があるか
- [ ] 機密情報がハードコードされていないか

#### テスト
- [ ] テストが追加されているか
- [ ] エッジケースがカバーされているか

#### パフォーマンス
- [ ] N+1クエリがないか
- [ ] 不要なループがないか

### 4. レビューコメント形式
```
## 総評
[全体的な評価]

## 必須の修正
1. **[問題]** - `file.ts:42`
   - 問題点: ...
   - 提案: ...

## 提案（任意）
- ...

## 良い点
- ...
```

### 5. レビュー結果の投稿
```bash
gh pr review $ARGUMENTS --approve --body "LGTM!"
gh pr review $ARGUMENTS --request-changes --body "修正をお願いします"
gh pr review $ARGUMENTS --comment --body "コメントです"
```

今すぐPRの内容を取得してレビューを開始してください。
