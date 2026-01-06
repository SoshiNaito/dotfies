# シナリオ3: コードレビューワークフロー

チームメンバーのPR #42 をレビューする例です。

## 前提条件

- GitHub CLIがインストール済み
- リポジトリへのアクセス権限がある

## ステップ

### Step 1: PRレビューを開始

```
あなた: /review-pr 42
```

**Claudeの動作:**
```
🔍 PR #42 を取得中...

gh pr view 42 --json title,body,author,files,additions,deletions
gh pr diff 42
```

### Step 2: PR情報の表示

**Claudeの出力:**
```
📋 PR #42 情報

タイトル: feat: 商品検索にフィルター機能を追加
作成者: @team-member
ブランチ: feature/product-filter → main

変更サマリー:
- ファイル数: 6
- 追加: +180行
- 削除: -12行

変更ファイル:
1. src/api/products.ts
2. src/services/productService.ts
3. src/repositories/productRepository.ts
4. src/types/product.ts
5. src/api/products.test.ts
6. docs/api-spec.md

レビューを開始します...
```

### Step 3: 詳細レビュー

**Claudeの出力:**
```
🔍 コードレビュー結果

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

✅ 承認項目

1. **型定義が適切**
   - ProductFilter 型が明確に定義されている
   - Optional なフィールドが正しくマークされている

2. **テストカバレッジ**
   - 新機能のテストが追加されている
   - エッジケース（空のフィルター）もテストされている

3. **ドキュメント更新**
   - API仕様書が更新されている

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🔧 必須の変更

**優先度: 高**

1. **SQLインジェクションの可能性**
   - 場所: `src/repositories/productRepository.ts:45`

   ```typescript
   // ❌ 現在のコード
   const query = `SELECT * FROM products WHERE name LIKE '%${filter.name}%'`

   // ✅ 修正案
   const query = `SELECT * FROM products WHERE name LIKE $1`
   const params = [`%${filter.name}%`]
   ```

   - 理由: ユーザー入力が直接クエリに結合されている

2. **入力バリデーション不足**
   - 場所: `src/api/products.ts:28`

   ```typescript
   // ❌ 現在のコード
   const filter = req.query as ProductFilter

   // ✅ 修正案
   const filter = validateProductFilter(req.query)
   ```

   - 理由: クエリパラメータが検証されていない

**優先度: 中**

3. **N+1クエリの可能性**
   - 場所: `src/services/productService.ts:32`

   ```typescript
   // ❌ 現在のコード
   const products = await this.repository.findAll(filter)
   for (const product of products) {
     product.category = await this.categoryRepository.findById(product.categoryId)
   }

   // ✅ 修正案
   const products = await this.repository.findAllWithCategory(filter)
   // または
   const categoryIds = [...new Set(products.map(p => p.categoryId))]
   const categories = await this.categoryRepository.findByIds(categoryIds)
   ```

   - 理由: 商品数が多いとパフォーマンス問題が発生

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

💡 提案（任意）

1. **ページネーションの追加**
   - 商品数が増えた場合に備えて、ページネーションの実装を検討
   - 例: `?page=1&per_page=20`

2. **キャッシュの検討**
   - 頻繁にアクセスされる検索結果はRedisでキャッシュ可能

3. **ログの追加**
   - 検索クエリをログに記録すると、後の分析に役立つ

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📊 メトリクス

- テストカバレッジ: 72% (目標: 80%)
- 新規テスト: 5件
- 循環的複雑度: 適正

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📝 総評

機能自体は良く実装されていますが、セキュリティ上の問題（SQLインジェクション）
があるため、修正が必要です。

結果: 🔄 修正リクエスト
```

### Step 4: レビューコメントを投稿

```
あなた: このレビュー結果をGitHubに投稿して
```

**Claudeの動作:**
```bash
gh pr review 42 --request-changes --body "$(cat <<'EOF'
## コードレビュー結果

### 必須の修正

1. **SQLインジェクションの可能性** - `src/repositories/productRepository.ts:45`

   ユーザー入力が直接クエリに結合されています。パラメータ化クエリを使用してください。

   ```typescript
   // 修正案
   const query = `SELECT * FROM products WHERE name LIKE $1`
   const params = [`%${filter.name}%`]
   ```

2. **入力バリデーション不足** - `src/api/products.ts:28`

   クエリパラメータの検証を追加してください。

3. **N+1クエリ** - `src/services/productService.ts:32`

   ループ内でのDBクエリを避け、バッチ取得を検討してください。

### 提案

- ページネーションの追加を検討
- 検索クエリのログ記録

修正後、再レビューをリクエストしてください。
EOF
)"
```

**Claudeの出力:**
```
✅ レビューを投稿しました

PR #42 に "修正リクエスト" を送信
コメント: 3件の必須修正、2件の提案

リンク: https://github.com/owner/repo/pull/42#pullrequestreview-xxxxx
```

### Step 5: 修正後の再レビュー

```
あなた: PR #42 が修正されたので再レビューして
```

**Claudeの動作:**
```
🔍 PR #42 の差分を再取得中...

前回のレビュー以降の変更:
- src/repositories/productRepository.ts (+5, -3)
- src/api/products.ts (+12, -2)
- src/services/productService.ts (+8, -6)
```

**Claudeの出力:**
```
🔍 再レビュー結果

前回の指摘事項:

1. ✅ SQLインジェクション → 修正済み
   パラメータ化クエリが使用されている

2. ✅ 入力バリデーション → 修正済み
   zodによるスキーマ検証が追加されている

3. ✅ N+1クエリ → 修正済み
   JOINを使用した単一クエリに変更されている

新たな問題: なし

📊 メトリクス:
- テストカバレッジ: 82% (目標達成)

結果: ✅ 承認

GitHubに承認を投稿しますか？
```

### Step 6: 承認を投稿

```
あなた: はい
```

**Claudeの動作:**
```bash
gh pr review 42 --approve --body "LGTM! 全ての指摘事項が修正されました。"
```

**Claudeの出力:**
```
✅ PR #42 を承認しました

メッセージ: LGTM! 全ての指摘事項が修正されました。

次のステップ:
- マージ待ち
- CIが全てパスすることを確認
```

## レビューチェックリスト

このワークフローで確認される項目:

### コード品質
- [ ] 命名が明確
- [ ] 関数が短くシンプル
- [ ] 重複コードがない

### セキュリティ
- [ ] SQLインジェクション対策
- [ ] XSS対策
- [ ] 入力バリデーション
- [ ] 認証・認可

### パフォーマンス
- [ ] N+1クエリがない
- [ ] 適切なインデックス

### テスト
- [ ] 十分なカバレッジ
- [ ] エッジケースのテスト

### ドキュメント
- [ ] API仕様の更新
- [ ] 変更履歴の記載
