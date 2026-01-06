# シナリオ1: 新機能開発ワークフロー

ユーザー認証機能を新規追加する例です。

## 前提条件

- Node.jsプロジェクトが存在
- Git リポジトリが初期化済み
- dotfilesの設定が適用済み

## ステップ

### Step 1: 機能ブランチを作成

```
あなた: /setup-feature auth-login
```

**Claudeの動作:**
```bash
git checkout -b feature/auth-login
```

### Step 2: 実装計画の作成

**Claudeの出力:**
```
📋 実装計画

**目標:** JWTを使用したユーザー認証機能の実装

**ステップ:**

1. **依存関係のインストール**
   - bcrypt, jsonwebtoken, cookie-parser
   - 担当: implementer

2. **データモデルの作成**
   - User モデル（email, passwordHash, createdAt）
   - 担当: implementer

3. **認証サービスの実装**
   - hashPassword, verifyPassword, generateToken
   - 担当: implementer

4. **APIエンドポイントの実装**
   - POST /api/auth/register
   - POST /api/auth/login
   - POST /api/auth/logout
   - 担当: implementer

5. **ミドルウェアの実装**
   - 認証チェックミドルウェア
   - 担当: implementer

6. **テストの作成**
   - 各機能のユニットテスト
   - 統合テスト
   - 担当: implementer

**変更が必要なファイル:**
| ファイルパス | 変更内容 | 優先度 |
|-------------|---------|--------|
| package.json | 依存関係追加 | 高 |
| src/models/user.ts | 新規作成 | 高 |
| src/services/auth.ts | 新規作成 | 高 |
| src/api/auth.ts | 新規作成 | 高 |
| src/middleware/auth.ts | 新規作成 | 高 |

**⚠️ リスクと考慮事項:**
1. パスワードは必ずbcrypt (rounds=12) でハッシュ化
2. JWTシークレットは環境変数から取得
3. HTTP-only cookieにトークンを保存

この計画で進めてよろしいですか？
```

### Step 3: 承認して実装開始

```
あなた: はい、この計画で進めてください
```

**Claudeの動作（TDDサイクル）:**

```
🔴 RED: テストを作成

// src/services/auth.test.ts
describe('AuthService', () => {
  describe('hashPassword', () => {
    it('パスワードをハッシュ化する', async () => {
      const password = 'testPassword123'
      const hash = await authService.hashPassword(password)

      expect(hash).not.toBe(password)
      expect(hash).toHaveLength(60) // bcrypt hash length
    })
  })
})

テスト実行...
❌ FAIL: hashPasswordが定義されていません
```

```
🟢 GREEN: 最小実装

// src/services/auth.ts
import bcrypt from 'bcrypt'

const SALT_ROUNDS = 12

export async function hashPassword(password: string): Promise<string> {
  return bcrypt.hash(password, SALT_ROUNDS)
}

テスト実行...
✅ PASS: パスワードをハッシュ化する
```

```
🔄 REFACTOR: コード改善

（今回は改善点なし、次のテストへ）
```

### Step 4: 実装進捗の確認

```
あなた: 進捗を教えて
```

**Claudeの出力:**
```
📊 実装進捗

✅ 完了:
1. 依存関係のインストール
2. データモデルの作成
3. 認証サービスの実装

🔄 進行中:
4. APIエンドポイントの実装 (2/3完了)
   - ✅ POST /api/auth/register
   - ✅ POST /api/auth/login
   - 🔄 POST /api/auth/logout

⏳ 未着手:
5. ミドルウェアの実装
6. テストの作成

テストカバレッジ: 78%
```

### Step 5: 実装完了後のレビュー

```
あなた: 実装が完了したのでレビューして
```

**Claudeの出力:**
```
🔍 コードレビュー結果

✅ 承認項目:
- TDDで実装されている
- セキュリティベストプラクティスに従っている
- 型定義が正確

🔧 必須の変更:
なし

💡 提案（任意）:
1. ログインリトライ制限の追加を検討
   - 場所: src/api/auth.ts
   - 理由: ブルートフォース攻撃対策

📊 メトリクス:
- テストカバレッジ: 85%
- 変更ファイル数: 8
- 追加/削除行数: +450/-0

総評: ✅ 承認
```

### Step 6: コミット

```
あなた: /commit
```

**Claudeの出力:**
```
📝 変更内容を分析しました:

提案するコミットメッセージ:
---
feat(auth): JWT認証機能を追加

- ユーザー登録・ログイン・ログアウトAPI
- bcrypt (rounds=12) によるパスワードハッシュ化
- HTTP-only cookieでトークン管理
- 認証ミドルウェアの実装

Closes #15
---

このメッセージでコミットしますか？ [Y/n]
```

### Step 7: PR作成

```
あなた: /create-pr ユーザー認証機能の追加
```

**Claudeの動作:**
```bash
# 最終チェック
npm test
npm run lint
npm audit

# プッシュ
git push -u origin feature/auth-login

# PR作成
gh pr create --title "ユーザー認証機能の追加" --body "..."
```

**作成されるPR:**
```markdown
## Summary
- JWT認証を使用したユーザー認証機能を追加
- 登録、ログイン、ログアウトのAPIエンドポイント
- 認証ミドルウェアによるアクセス制御

## Test plan
- [x] ユニットテスト（カバレッジ85%）
- [x] 統合テスト
- [x] 手動テスト（Postman）

## Security checklist
- [x] パスワードはbcryptでハッシュ化
- [x] JWTシークレットは環境変数から取得
- [x] HTTP-only cookieを使用
- [x] XSS/CSRF対策済み

Closes #15
```

## 完了

これで新機能の開発からPR作成までが完了しました。

## 所要時間の目安

| ステップ | 目安 |
|---------|------|
| 計画作成 | 5分 |
| 実装（TDD） | 60-90分 |
| レビュー | 5分 |
| コミット・PR | 5分 |
| **合計** | **75-105分** |
