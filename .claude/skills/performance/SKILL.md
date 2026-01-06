---
name: performance
description: パフォーマンス最適化のチェックリストとベストプラクティス。遅いコードや最適化が必要な場合に参照。
---

# パフォーマンス最適化ガイド

## データベース最適化

### N+1クエリ問題

```typescript
// ❌ 悪い例: N+1クエリ
const users = await db.users.findAll()
for (const user of users) {
  user.posts = await db.posts.findByUserId(user.id) // N回クエリ
}

// ✅ 良い例: JOINまたはeager loading
const users = await db.users.findAll({
  include: [{ model: Post }]
})

// ✅ 良い例: バッチクエリ
const users = await db.users.findAll()
const userIds = users.map(u => u.id)
const posts = await db.posts.findAll({ where: { userId: userIds } })
const postsByUser = groupBy(posts, 'userId')
users.forEach(u => u.posts = postsByUser[u.id] || [])
```

### インデックス

```sql
-- 頻繁に検索するカラムにインデックス
CREATE INDEX idx_users_email ON users(email);

-- 複合インデックス（カラム順序が重要）
CREATE INDEX idx_orders_user_date ON orders(user_id, created_at);

-- 部分インデックス
CREATE INDEX idx_active_users ON users(email) WHERE active = true;
```

### クエリ最適化

```typescript
// ❌ 悪い例: 全件取得
const allUsers = await db.users.findAll()
const activeCount = allUsers.filter(u => u.active).length

// ✅ 良い例: DBでカウント
const activeCount = await db.users.count({ where: { active: true } })

// ❌ 悪い例: SELECT *
SELECT * FROM users WHERE id = 1

// ✅ 良い例: 必要なカラムのみ
SELECT id, name, email FROM users WHERE id = 1
```

## フロントエンド最適化

### React メモ化

```tsx
// useMemo: 計算結果をキャッシュ
const expensiveValue = useMemo(() => {
  return items.filter(item => item.price > 100).sort((a, b) => b.price - a.price)
}, [items])

// useCallback: 関数をキャッシュ
const handleClick = useCallback((id: string) => {
  setSelectedId(id)
}, [])

// React.memo: コンポーネントの再レンダリング防止
const ListItem = React.memo(({ item, onClick }: Props) => {
  return <div onClick={() => onClick(item.id)}>{item.name}</div>
})
```

### 遅延読み込み

```tsx
// コンポーネントの遅延読み込み
const HeavyComponent = React.lazy(() => import('./HeavyComponent'))

// Suspenseでラップ
<Suspense fallback={<Loading />}>
  <HeavyComponent />
</Suspense>

// 画像の遅延読み込み
<img src={url} loading="lazy" alt="..." />
```

### バンドルサイズ削減

```typescript
// ❌ 悪い例: ライブラリ全体をインポート
import _ from 'lodash'

// ✅ 良い例: 必要な関数のみ
import debounce from 'lodash/debounce'

// ❌ 悪い例: moment.js（大きい）
import moment from 'moment'

// ✅ 良い例: date-fns（ツリーシェイク可能）
import { format } from 'date-fns'
```

## バックエンド最適化

### キャッシング

```typescript
// Redis キャッシュ
async function getUserWithCache(id: string): Promise<User> {
  const cacheKey = `user:${id}`

  // キャッシュチェック
  const cached = await redis.get(cacheKey)
  if (cached) {
    return JSON.parse(cached)
  }

  // DBから取得
  const user = await db.users.findById(id)

  // キャッシュに保存（TTL: 1時間）
  await redis.setex(cacheKey, 3600, JSON.stringify(user))

  return user
}

// キャッシュ無効化
async function updateUser(id: string, data: Partial<User>) {
  await db.users.update(id, data)
  await redis.del(`user:${id}`)
}
```

### ページネーション

```typescript
// オフセットベース（小規模向け）
async function getUsers(page: number, limit: number) {
  const offset = (page - 1) * limit
  return db.users.findAll({ limit, offset })
}

// カーソルベース（大規模向け）
async function getUsers(cursor?: string, limit: number = 20) {
  const where = cursor ? { id: { gt: cursor } } : {}
  const users = await db.users.findAll({ where, limit: limit + 1 })

  const hasMore = users.length > limit
  const items = hasMore ? users.slice(0, -1) : users
  const nextCursor = hasMore ? items[items.length - 1].id : null

  return { items, nextCursor, hasMore }
}
```

### 並列処理

```typescript
// ❌ 悪い例: 順次実行
const user = await getUser(id)
const posts = await getPosts(userId)
const comments = await getComments(postIds)

// ✅ 良い例: 並列実行（依存関係がない場合）
const [user, settings, notifications] = await Promise.all([
  getUser(id),
  getSettings(id),
  getNotifications(id),
])
```

## チェックリスト

### データベース
- [ ] N+1クエリがないか
- [ ] 適切なインデックスがあるか
- [ ] 必要なカラムのみ取得しているか
- [ ] ページネーションを実装しているか

### フロントエンド
- [ ] 不要な再レンダリングがないか
- [ ] 遅延読み込みを使用しているか
- [ ] バンドルサイズは適切か
- [ ] 画像は最適化されているか

### バックエンド
- [ ] キャッシングを活用しているか
- [ ] 非同期処理を並列化しているか
- [ ] 重い処理はバックグラウンドジョブにしているか

## 計測ツール

```bash
# Lighthouse (フロントエンド)
npx lighthouse https://example.com --view

# バンドル分析
npx webpack-bundle-analyzer stats.json

# Node.js プロファイリング
node --prof app.js
node --prof-process isolate-*.log > profile.txt
```
