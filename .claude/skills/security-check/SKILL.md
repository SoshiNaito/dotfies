---
name: security-check
description: 認証、データ処理、一般的な脆弱性に関するセキュリティ検証チェックリスト。認証、パスワード、機密データを扱う際に自動的にロードされます。
---

# セキュリティチェック

## 🔐 認証と認可

### パスワードセキュリティ
- [ ] bcrypt/argon2でハッシュ化（bcrypt rounds ≥ 12）
- [ ] パスワードは平文で保存しない
- [ ] パスワードはログに出力しない
- [ ] パスワードリセット時にトークン使用

### トークン管理
- [ ] JWTは強力なシークレットで署名
- [ ] トークンに有効期限を設定
- [ ] リフレッシュトークンの実装
- [ ] HTTP-only cookieに保存
- [ ] Secure flagを設定（HTTPS環境）

### セッション管理
- [ ] セッションタイムアウト実装
- [ ] ログアウト時にセッション破棄
- [ ] 並行セッション制限

## 🛡️ 入力バリデーション

### SQLインジェクション対策
```typescript
// ❌ 悪い例: 文字列結合
const query = `SELECT * FROM users WHERE id = ${userId}`

// ✅ 良い例: パラメータ化クエリ
const query = `SELECT * FROM users WHERE id = $1`
db.query(query, [userId])
```

### XSS対策
```typescript
// ❌ 悪い例: 生のHTMLをレンダリング
<div dangerouslySetInnerHTML={{ __html: userInput }} />

// ✅ 良い例: エスケープ
<div>{userInput}</div>
```

### 入力サニタイゼーション
- [ ] 全入力をバリデーション
- [ ] ホワイトリスト方式を採用
- [ ] 型チェック実装
- [ ] 長さ制限を設定

## 🌐 APIセキュリティ

### CORS設定
```typescript
// ✅ 良い例: 特定オリジンのみ許可
app.use(cors({
  origin: ['https://yourapp.com'],
  credentials: true
}))
```

### レート制限
```typescript
// ✅ 良い例: レート制限実装
import rateLimit from 'express-rate-limit'

const limiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15分
  max: 100 // 最大100リクエスト
})

app.use('/api/', limiter)
```

### CSRF保護
- [ ] CSRFトークンを実装
- [ ] SameSite cookie属性を設定
- [ ] POSTリクエストでトークンを検証

## 🔒 データ保護

### 機密データ
- [ ] 環境変数で機密情報を管理
- [ ] `.env`ファイルを`.gitignore`に追加
- [ ] APIキーをコードにハードコードしない
- [ ] ログに機密情報を出力しない

### 暗号化
- [ ] 通信はHTTPSを使用
- [ ] 保存時に機密データを暗号化
- [ ] TLS 1.2以上を使用

## 🚨 一般的な脆弱性

### OWASP Top 10チェック
1. [ ] インジェクション攻撃対策
2. [ ] 認証の脆弱性対策
3. [ ] センシティブデータの露出対策
4. [ ] XML External Entities (XXE)対策
5. [ ] アクセス制御の不備対策
6. [ ] セキュリティ設定ミス対策
7. [ ] クロスサイトスクリプティング(XSS)対策
8. [ ] 安全でないデシリアライゼーション対策
9. [ ] 既知の脆弱性を持つコンポーネント対策
10. [ ] 不十分なロギング・監視対策

## 🛠️ セキュリティツール

### 実行コマンド
```bash
# 依存関係の脆弱性チェック
npm audit

# セキュリティスキャン
npm run security:scan

# 静的解析
npm run lint:security
```

## 📝 検証スクリプト

自動セキュリティチェックを実行するには `scripts/security_scan.py` を使用:
```bash
python scripts/security_scan.py --path ./src
```

## 🔍 レビュー質問
実装前に自問：
1. このコードは信頼できない入力を扱うか？
2. 認証・認可が必要か？
3. 機密情報を扱うか？
4. 外部APIと通信するか？
5. ユーザーデータを保存するか？