ドキュメント生成ワークフローを開始します。

対象: $ARGUMENTS

## ドキュメント種別

### 1. コードドキュメント (JSDoc/TSDoc)
```typescript
/**
 * ユーザーを作成する
 * @param data - ユーザー作成データ
 * @returns 作成されたユーザー
 * @throws {ValidationError} 入力が無効な場合
 * @example
 * const user = await createUser({ email: 'test@example.com' })
 */
```

### 2. README.md
```markdown
# プロジェクト名

## 概要
## インストール
## 使用方法
## API
## 開発
## ライセンス
```

### 3. API仕様書 (OpenAPI/Swagger)
```yaml
paths:
  /users:
    post:
      summary: ユーザー作成
      requestBody:
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateUserDto'
```

### 4. 変更履歴 (CHANGELOG)
```markdown
## [1.2.0] - 2024-01-15
### Added
- ログイン機能
### Fixed
- パスワードリセットのバグ
```

## ワークフロー

1. **対象の特定**
   - ファイル/関数/モジュール
   - 既存ドキュメントの有無

2. **コード分析**
   - 公開APIの抽出
   - パラメータと戻り値
   - 例外とエッジケース

3. **ドキュメント生成**
   - 適切な形式で作成
   - 例を含める
   - リンクを追加

4. **レビュー**
   - 正確性確認
   - わかりやすさ確認

## コマンド
```bash
# TypeDoc生成
npx typedoc --out docs src/

# API仕様からHTMLを生成
npx redoc-cli bundle openapi.yaml
```

今すぐ対象を分析してドキュメントを生成してください。
