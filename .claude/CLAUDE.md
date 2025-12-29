# プロジェクト名
[プロジェクトの簡潔な説明]

## アーキテクチャ
- **バックエンド:** Node.js + Express + PostgreSQL
- **フロントエンド:** React + TypeScript + Vite
- **認証:** JWT (HTTP-only cookies)
- **デプロイ:** Docker + AWS ECS

## プロジェクト構造
```
src/
├── api/           # RESTful APIエンドポイント
├── models/        # データモデル
├── services/      # ビジネスロジック
├── middleware/    # Express middleware
└── utils/         # ユーティリティ関数
```

## コーディング規約
1. **スタイル:** ESLint + Prettier設定に従う
2. **テスト:** Jest + React Testing Library
3. **命名規則:**
   - ファイル: kebab-case
   - コンポーネント: PascalCase
   - 関数: camelCase
   - 定数: UPPER_SNAKE_CASE

## 開発ワークフロー
1. 機能ブランチを作成 (`feature/xxx`)
2. TDDでテストを先に書く
3. 実装してテストをパス
4. コードレビューを受ける
5. mainにマージ

## 重要な制約
- パスワードは bcrypt (rounds=12) でハッシュ化
- 全APIエンドポイントに認証を実装
- 環境変数は `.env` で管理（.gitignoreに追加済み）
- SQLインジェクション対策：必ずparameterized queries使用
- XSS対策：出力時に必ずエスケープ

## 依存関係
- Node.js: v20.x
- PostgreSQL: v15.x
- Redis: v7.x (セッション管理用)

## よく使うコマンド
```bash
npm run dev          # 開発サーバー起動
npm test             # テスト実行
npm run test:watch   # テストwatch mode
npm run lint         # Lint実行
npm run db:migrate   # DB migration実行
```

## 参考ドキュメント
- API仕様: `docs/api-spec.md`
- DB設計: `docs/database-schema.md`
- セキュリティガイド: `docs/security.md`
```
