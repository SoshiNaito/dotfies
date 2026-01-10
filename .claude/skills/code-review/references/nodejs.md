# Node.js チェック項目

Node.js 固有のコードレビュー観点。

※ JavaScript / TypeScript の基本的なチェック項目は `javascript.md`, `typescript.md` を参照。

---

## モジュールシステム

### ESModules vs CommonJS
- [ ] ESModules (`import/export`) または CommonJS (`require/module.exports`) の一貫した使用
- [ ] `package.json` の `type` フィールドの確認
- [ ] `.mjs` / `.cjs` 拡張子の適切な使用

```javascript
// ESModules（推奨）
import express from 'express';
import { readFile } from 'fs/promises';
export const handler = () => {};

// CommonJS
const express = require('express');
const { readFile } = require('fs/promises');
module.exports = { handler };
```

### モジュール設計
- [ ] 循環依存がない
- [ ] バレルファイル（`index.js`）の適切な使用
- [ ] 副作用のあるインポートを避ける

---

## 環境・設定管理

### 環境変数
- [ ] 環境変数の適切な管理（`dotenv` など）
- [ ] 機密情報がハードコードされていない
- [ ] 必須環境変数のバリデーション
- [ ] `.env` ファイルの `.gitignore` 登録

```javascript
// 環境変数のバリデーション
const requiredEnvVars = ['DATABASE_URL', 'JWT_SECRET', 'PORT'];

for (const envVar of requiredEnvVars) {
  if (!process.env[envVar]) {
    throw new Error(`Missing required environment variable: ${envVar}`);
  }
}

const config = {
  port: parseInt(process.env.PORT, 10) || 3000,
  dbUrl: process.env.DATABASE_URL,
  jwtSecret: process.env.JWT_SECRET,
  nodeEnv: process.env.NODE_ENV || 'development',
};
```

### 設定の構造化
- [ ] 環境ごとの設定分離
- [ ] 設定の型定義（TypeScript）
- [ ] デフォルト値の設定

---

## 非同期処理

### Promise / async-await
- [ ] `async/await` の使用
- [ ] エラーハンドリング（try-catch）
- [ ] `Promise.all` / `Promise.allSettled` の活用
- [ ] コールバックスタイルの回避

```javascript
// 推奨
async function fetchData() {
  try {
    const [users, posts] = await Promise.all([
      fetchUsers(),
      fetchPosts(),
    ]);
    return { users, posts };
  } catch (error) {
    logger.error('Failed to fetch data', error);
    throw error;
  }
}

// 非推奨: コールバック地獄
function fetchData(callback) {
  fetchUsers((err, users) => {
    if (err) return callback(err);
    fetchPosts((err, posts) => {
      // ...
    });
  });
}
```

### イベントループ
- [ ] ブロッキング処理を避ける
- [ ] CPU集約的な処理は Worker Threads を検討
- [ ] `setImmediate` / `process.nextTick` の理解

---

## エラーハンドリング

### グローバルエラーハンドリング
- [ ] `uncaughtException` のハンドリング
- [ ] `unhandledRejection` のハンドリング
- [ ] グレースフルシャットダウン

```javascript
process.on('uncaughtException', (error) => {
  logger.fatal('Uncaught Exception', error);
  process.exit(1);
});

process.on('unhandledRejection', (reason, promise) => {
  logger.error('Unhandled Rejection', { reason, promise });
});

// グレースフルシャットダウン
process.on('SIGTERM', async () => {
  logger.info('SIGTERM received, shutting down gracefully');
  await server.close();
  await db.disconnect();
  process.exit(0);
});
```

### エラークラス
- [ ] カスタムエラークラスの定義
- [ ] エラーの種類による分類
- [ ] スタックトレースの保持

```javascript
class AppError extends Error {
  constructor(message, statusCode, code) {
    super(message);
    this.statusCode = statusCode;
    this.code = code;
    this.isOperational = true;
    Error.captureStackTrace(this, this.constructor);
  }
}

class NotFoundError extends AppError {
  constructor(resource) {
    super(`${resource} not found`, 404, 'NOT_FOUND');
  }
}
```

---

## ファイルシステム

### ファイル操作
- [ ] `fs/promises` の使用（Promise ベース）
- [ ] ストリームの活用（大きなファイル）
- [ ] パス操作に `path` モジュールを使用
- [ ] 適切なエラーハンドリング

```javascript
import { readFile, writeFile } from 'fs/promises';
import { createReadStream, createWriteStream } from 'fs';
import path from 'path';

// 小さなファイル
const content = await readFile(path.join(__dirname, 'data.json'), 'utf-8');

// 大きなファイル（ストリーム）
const readStream = createReadStream('large-file.txt');
const writeStream = createWriteStream('output.txt');
readStream.pipe(writeStream);
```

### パス操作
- [ ] `path.join()` / `path.resolve()` の使用
- [ ] プラットフォーム間の互換性
- [ ] `__dirname`, `__filename` の理解（ESModules では `import.meta.url`）

```javascript
// ESModules での __dirname 相当
import { fileURLToPath } from 'url';
import path from 'path';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
```

---

## ストリーム

- [ ] 大きなデータにはストリームを使用
- [ ] バックプレッシャーの考慮
- [ ] ストリームのエラーハンドリング
- [ ] `pipeline` の活用

```javascript
import { pipeline } from 'stream/promises';
import { createReadStream, createWriteStream } from 'fs';
import { createGzip } from 'zlib';

// pipeline でストリーム処理
await pipeline(
  createReadStream('input.txt'),
  createGzip(),
  createWriteStream('output.txt.gz')
);
```

---

## セキュリティ

### 入力バリデーション
- [ ] ユーザー入力のサニタイズ
- [ ] パストラバーサル攻撃の防止
- [ ] コマンドインジェクションの防止

```javascript
import path from 'path';

// パストラバーサル対策
function safeReadFile(userInput) {
  const safePath = path.normalize(userInput).replace(/^(\.\.(\/|\\|$))+/, '');
  const fullPath = path.join(ALLOWED_DIR, safePath);
  
  // ALLOWED_DIR 内のファイルのみ許可
  if (!fullPath.startsWith(ALLOWED_DIR)) {
    throw new Error('Access denied');
  }
  
  return readFile(fullPath);
}
```

### 依存関係
- [ ] `npm audit` での脆弱性チェック
- [ ] 依存関係の定期的な更新
- [ ] `package-lock.json` のコミット

---

## ログ

### ロギング
- [ ] 構造化ログの使用（JSON形式）
- [ ] 適切なログレベル（error, warn, info, debug）
- [ ] 機密情報をログに出力しない
- [ ] リクエストID / 相関IDの付与

```javascript
import pino from 'pino';

const logger = pino({
  level: process.env.LOG_LEVEL || 'info',
  formatters: {
    level: (label) => ({ level: label }),
  },
});

// 使用例
logger.info({ userId: user.id, action: 'login' }, 'User logged in');
logger.error({ err: error, requestId }, 'Request failed');
```

---

## プロセス管理

### シグナルハンドリング
- [ ] `SIGTERM`, `SIGINT` のハンドリング
- [ ] グレースフルシャットダウン
- [ ] リソースのクリーンアップ

### ヘルスチェック
- [ ] ヘルスチェックエンドポイント
- [ ] 依存サービスの状態確認

```javascript
app.get('/health', async (req, res) => {
  const health = {
    status: 'ok',
    timestamp: new Date().toISOString(),
    checks: {
      database: await checkDatabase(),
      redis: await checkRedis(),
    },
  };
  
  const isHealthy = Object.values(health.checks).every(c => c.status === 'ok');
  res.status(isHealthy ? 200 : 503).json(health);
});
```

---

## テスト

- [ ] ユニットテストの実装
- [ ] 統合テストの実装
- [ ] モック/スタブの適切な使用
- [ ] テストカバレッジ

```javascript
// Jest の例
describe('UserService', () => {
  let userService;
  let mockRepository;

  beforeEach(() => {
    mockRepository = {
      findById: jest.fn(),
      save: jest.fn(),
    };
    userService = new UserService(mockRepository);
  });

  describe('findById', () => {
    it('should return user when found', async () => {
      const mockUser = { id: '1', name: 'Test' };
      mockRepository.findById.mockResolvedValue(mockUser);

      const result = await userService.findById('1');

      expect(result).toEqual(mockUser);
      expect(mockRepository.findById).toHaveBeenCalledWith('1');
    });
  });
});
```

---

## パフォーマンス

- [ ] メモリリークの防止
- [ ] イベントリスナーの解除
- [ ] コネクションプーリング（DB, HTTP）
- [ ] キャッシュの活用

```javascript
// コネクションプーリング（例: pg）
import { Pool } from 'pg';

const pool = new Pool({
  max: 20,
  idleTimeoutMillis: 30000,
  connectionTimeoutMillis: 2000,
});

// HTTPエージェントの設定
import { Agent } from 'http';

const agent = new Agent({
  keepAlive: true,
  maxSockets: 50,
});
```

---

## クイックリファレンス

### package.json 設定

```json
{
  "type": "module",
  "engines": {
    "node": ">=18.0.0"
  },
  "scripts": {
    "start": "node src/index.js",
    "dev": "node --watch src/index.js",
    "test": "node --test",
    "lint": "eslint src/"
  }
}
```

### 推奨ツール

| ツール | 用途 |
|-------|------|
| `pino` | ロギング |
| `dotenv` | 環境変数管理 |
| `zod` | バリデーション |
| `jest` / `vitest` | テスト |
| `nodemon` / `--watch` | 開発時の自動再起動 |