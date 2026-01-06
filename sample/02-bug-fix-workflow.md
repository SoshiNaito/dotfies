# シナリオ2: バグ修正ワークフロー

「ユーザー一覧で削除済みユーザーが表示される」バグを修正する例です。

## 前提条件

- 該当のバグがIssue #78として報告されている
- 開発環境が構築済み

## ステップ

### Step 1: バグ修正を開始

```
あなた: /fix-bug ユーザー一覧で削除済みユーザーが表示される #78
```

**Claudeの動作:**

```
🐛 バグ修正ワークフロー開始

Issue #78: ユーザー一覧で削除済みユーザーが表示される

1. バグの理解
   - 症状: 論理削除されたユーザーが一覧に表示される
   - 再現: GET /api/users を実行

2. 関連コードを調査中...
```

### Step 2: 原因の特定

**Claudeの出力:**
```
🔍 調査結果

原因を特定しました:

📁 src/repositories/userRepository.ts:24
---
async findAll(): Promise<User[]> {
  return this.db.users.findMany()  // ← deletedAt を考慮していない
}
---

問題: findMany() がすべてのユーザーを返している
期待: deletedAt が null のユーザーのみを返すべき

📁 関連ファイル:
- src/api/users.ts:15 (このリポジトリを使用)
- src/models/user.ts (deletedAt フィールドあり)

修正方針:
findAll() に where: { deletedAt: null } を追加

この方針で修正してよろしいですか？
```

### Step 3: 承認して修正開始

```
あなた: はい、修正してください
```

**Claudeの動作（TDDサイクル）:**

```
🔴 RED: 失敗するテストを作成

// src/repositories/userRepository.test.ts
describe('UserRepository', () => {
  describe('findAll', () => {
    it('削除済みユーザーを含まない', async () => {
      // Arrange
      await db.users.create({ email: 'active@test.com', deletedAt: null })
      await db.users.create({ email: 'deleted@test.com', deletedAt: new Date() })

      // Act
      const users = await userRepository.findAll()

      // Assert
      expect(users).toHaveLength(1)
      expect(users[0].email).toBe('active@test.com')
    })
  })
})

テスト実行...
❌ FAIL: 期待値 1、実際 2
```

```
🟢 GREEN: 修正を実装

// src/repositories/userRepository.ts
async findAll(): Promise<User[]> {
  return this.db.users.findMany({
    where: { deletedAt: null }
  })
}

テスト実行...
✅ PASS: 削除済みユーザーを含まない
```

```
🔄 REFACTOR: 追加の改善

// 他のメソッドも確認
// findById も同様の問題があるため修正

async findById(id: string): Promise<User | null> {
  return this.db.users.findFirst({
    where: { id, deletedAt: null }
  })
}
```

### Step 4: 全テスト実行

```
あなた: 全テストを実行して
```

**Claudeの出力:**
```
🧪 テスト実行結果

npm test

  UserRepository
    ✓ 削除済みユーザーを含まない (12ms)
    ✓ IDで削除済みでないユーザーを取得 (8ms)
    ✓ 削除済みユーザーのIDでは null を返す (6ms)

  UserAPI
    ✓ GET /users は削除済みを含まない (45ms)
    ✓ GET /users/:id は削除済みで 404 (32ms)

  ... (他の既存テスト)

Test Suites: 12 passed, 12 total
Tests:       89 passed, 89 total
Time:        4.521s

✅ 全テストパス
リグレッションなし
```

### Step 5: コミット

```
あなた: /commit
```

**Claudeの出力:**
```
📝 変更内容を分析しました:

変更ファイル: 2
- src/repositories/userRepository.ts (+4, -2)
- src/repositories/userRepository.test.ts (+25, -0)

提案するコミットメッセージ:
---
fix(users): 削除済みユーザーが一覧に表示される問題を修正

- findAll() に deletedAt: null 条件を追加
- findById() にも同様の条件を追加
- 再発防止のテストを追加

Fixes #78
---

このメッセージでコミットしますか？ [Y/n]
```

### Step 6: PR作成

```
あなた: /create-pr
```

**Claudeの動作:**
```
🚀 PR作成

最終チェック実行中...
✅ テスト: 89件パス
✅ Lint: エラーなし
✅ 型チェック: エラーなし

git push origin fix/issue-78
gh pr create ...
```

**作成されるPR:**
```markdown
## Summary
- 削除済みユーザーが一覧に表示されるバグを修正
- findAll() と findById() に deletedAt フィルターを追加

## Root Cause
UserRepository の findAll() が deletedAt を考慮せず
すべてのユーザーを返していた

## Changes
- `src/repositories/userRepository.ts`: where 条件を追加
- `src/repositories/userRepository.test.ts`: テストを追加

## Test plan
- [x] 新規テスト追加（3件）
- [x] 既存テスト全てパス
- [x] 手動確認: 削除済みユーザーが表示されないこと

Fixes #78
```

## 完了

バグ修正からPR作成までが完了しました。

## チェックリスト

- [x] 原因を特定した
- [x] 失敗するテストを作成した
- [x] 最小限の修正を実装した
- [x] 関連箇所も修正した（findById）
- [x] 全テストがパスした
- [x] PRを作成した
