---
name: api-design
description: RESTful API設計のベストプラクティスとガイドライン。APIエンドポイント実装時に参照。
---

# RESTful API設計ガイド

## 基本原則

1. **リソース指向**: URLはリソース（名詞）を表す
2. **HTTPメソッド**: 動作はHTTPメソッドで表現
3. **ステートレス**: 各リクエストは独立
4. **一貫性**: 命名規則とレスポンス形式を統一

## URL設計

### 基本ルール

```
# ✅ 良い例
GET    /users           # ユーザー一覧
GET    /users/123       # 特定ユーザー
POST   /users           # ユーザー作成
PUT    /users/123       # ユーザー更新（全体）
PATCH  /users/123       # ユーザー更新（部分）
DELETE /users/123       # ユーザー削除

# ❌ 悪い例
GET    /getUsers
GET    /user/123        # 複数形を使う
POST   /createUser
POST   /users/123/delete
```

### リソースの関係

```
# ネストされたリソース
GET    /users/123/posts           # ユーザー123の投稿一覧
POST   /users/123/posts           # ユーザー123に投稿作成
GET    /users/123/posts/456       # 特定の投稿

# 浅いネスト（推奨：2階層まで）
GET    /posts/456                 # 投稿を直接取得
GET    /posts?user_id=123         # クエリでフィルタ
```

### クエリパラメータ

```
# フィルタリング
GET /posts?status=published&author_id=123

# ソート
GET /posts?sort=-created_at,title   # -は降順

# ページネーション
GET /posts?page=2&per_page=20
GET /posts?cursor=abc123&limit=20   # カーソルベース

# フィールド選択
GET /users/123?fields=id,name,email

# 関連リソースの埋め込み
GET /posts/123?include=author,comments
```

## HTTPステータスコード

| コード | 意味 | 使用場面 |
|--------|------|----------|
| 200 | OK | 成功（データあり） |
| 201 | Created | リソース作成成功 |
| 204 | No Content | 成功（データなし、DELETE等） |
| 400 | Bad Request | リクエスト不正 |
| 401 | Unauthorized | 認証が必要 |
| 403 | Forbidden | 権限なし |
| 404 | Not Found | リソースなし |
| 409 | Conflict | 競合（重複等） |
| 422 | Unprocessable Entity | バリデーションエラー |
| 429 | Too Many Requests | レート制限 |
| 500 | Internal Server Error | サーバーエラー |

## レスポンス形式

### 成功レスポンス

```json
// 単一リソース
{
  "data": {
    "id": "123",
    "type": "user",
    "attributes": {
      "name": "田中太郎",
      "email": "tanaka@example.com"
    }
  }
}

// コレクション
{
  "data": [
    { "id": "1", "name": "..." },
    { "id": "2", "name": "..." }
  ],
  "meta": {
    "total": 100,
    "page": 1,
    "per_page": 20
  },
  "links": {
    "self": "/users?page=1",
    "next": "/users?page=2",
    "last": "/users?page=5"
  }
}
```

### エラーレスポンス

```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "入力内容に問題があります",
    "details": [
      {
        "field": "email",
        "message": "有効なメールアドレスを入力してください"
      },
      {
        "field": "password",
        "message": "8文字以上で入力してください"
      }
    ]
  }
}
```

## 認証

### Bearer Token

```http
GET /api/users HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
```

### APIキー

```http
GET /api/data HTTP/1.1
X-API-Key: your-api-key
```

## バージョニング

```
# URLベース（推奨）
GET /api/v1/users
GET /api/v2/users

# ヘッダーベース
GET /api/users
Accept: application/vnd.api+json; version=2
```

## 実装例

### Express Router

```typescript
import { Router } from 'express'

const router = Router()

// 一覧取得
router.get('/', async (req, res) => {
  const { page = 1, per_page = 20, sort, ...filters } = req.query

  const { data, total } = await userService.findAll({
    filters,
    sort: parseSort(sort),
    pagination: { page: Number(page), perPage: Number(per_page) }
  })

  res.json({
    data,
    meta: { total, page: Number(page), per_page: Number(per_page) },
    links: buildPaginationLinks(req, total, Number(page), Number(per_page))
  })
})

// 単一取得
router.get('/:id', async (req, res) => {
  const user = await userService.findById(req.params.id)
  if (!user) {
    return res.status(404).json({
      error: { code: 'NOT_FOUND', message: 'ユーザーが見つかりません' }
    })
  }
  res.json({ data: user })
})

// 作成
router.post('/', validateBody(createUserSchema), async (req, res) => {
  const user = await userService.create(req.body)
  res.status(201).json({ data: user })
})

// 更新
router.patch('/:id', validateBody(updateUserSchema), async (req, res) => {
  const user = await userService.update(req.params.id, req.body)
  res.json({ data: user })
})

// 削除
router.delete('/:id', async (req, res) => {
  await userService.delete(req.params.id)
  res.status(204).send()
})

export default router
```

### OpenAPI仕様

```yaml
openapi: 3.0.0
info:
  title: User API
  version: 1.0.0

paths:
  /users:
    get:
      summary: ユーザー一覧取得
      parameters:
        - name: page
          in: query
          schema:
            type: integer
            default: 1
        - name: per_page
          in: query
          schema:
            type: integer
            default: 20
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/UserList'

    post:
      summary: ユーザー作成
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateUser'
      responses:
        '201':
          description: 作成成功
        '422':
          description: バリデーションエラー

components:
  schemas:
    User:
      type: object
      properties:
        id:
          type: string
        name:
          type: string
        email:
          type: string
          format: email
```

## チェックリスト

### URL設計
- [ ] リソース名は複数形の名詞
- [ ] ネストは2階層まで
- [ ] 動詞を使っていない

### HTTPメソッド
- [ ] GET: 取得（べき等、安全）
- [ ] POST: 作成
- [ ] PUT/PATCH: 更新
- [ ] DELETE: 削除

### レスポンス
- [ ] 適切なステータスコード
- [ ] 一貫したレスポンス形式
- [ ] エラーメッセージが明確

### セキュリティ
- [ ] 認証が実装されている
- [ ] 認可チェックがある
- [ ] レート制限がある
- [ ] 入力バリデーションがある
