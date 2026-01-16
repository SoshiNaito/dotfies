---
name: design-api
description: RESTful API設計のベストプラクティスとガイドライン。APIエンドポイント実装時に参照。
---

# Design API Skill

RESTful API設計のガイドを提供します。

## 基本原則

1. **リソース指向**: URLはリソース（名詞）を表す
2. **HTTPメソッド**: 動作はHTTPメソッドで表現
3. **ステートレス**: 各リクエストは独立
4. **一貫性**: 命名規則とレスポンス形式を統一

## URL設計

### 基本ルール
```
✅ 良い例:
GET    /users           # ユーザー一覧
GET    /users/123       # 特定ユーザー
POST   /users           # ユーザー作成
PUT    /users/123       # ユーザー更新（全体）
PATCH  /users/123       # ユーザー更新（部分）
DELETE /users/123       # ユーザー削除

❌ 悪い例:
GET    /getUsers        # 動詞を含む
GET    /user/123        # 単数形
POST   /createUser      # 動詞を含む
POST   /users/123/delete # DELETEを使うべき
```

### リソースの関係
```
# ネストされたリソース
GET    /users/123/posts           # ユーザー123の投稿一覧
POST   /users/123/posts           # ユーザー123に投稿作成

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
```
// 単一リソース
{
  "data": {
    "id": "123",
    "name": "田中太郎",
    "email": "tanaka@example.com"
  }
}

// コレクション
{
  "data": [...],
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
```
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "入力内容に問題があります",
    "details": [
      { "field": "email", "message": "有効なメールアドレスを入力してください" }
    ]
  }
}
```

## 認証

### Bearer Token
```
Authorization: Bearer <token>
```

### APIキー
```
X-API-Key: <api-key>
```

## バージョニング

```
# URLベース（推奨）
GET /api/v1/users
GET /api/v2/users

# ヘッダーベース
Accept: application/vnd.api+json; version=2
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

### ドキュメント
- [ ] OpenAPI/Swagger仕様がある
- [ ] エンドポイント一覧がある
- [ ] リクエスト/レスポンス例がある
