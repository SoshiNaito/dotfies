---
name: error-handling
description: 堅牢なエラーハンドリングパターンとベストプラクティス。例外処理やエラー境界を実装する際に参照。
---

# エラーハンドリングガイド

## 基本原則

1. **早期失敗**: 問題を早期に検出して報告
2. **適切な粒度**: 具体的すぎず、抽象的すぎない
3. **回復可能性**: 回復可能なエラーと致命的エラーを区別
4. **ユーザーフレンドリー**: 技術的詳細を隠し、意味のあるメッセージを

## カスタムエラークラス

```typescript
// ベースエラー
export class AppError extends Error {
  constructor(
    message: string,
    public code: string,
    public statusCode: number = 500,
    public isOperational: boolean = true
  ) {
    super(message)
    this.name = this.constructor.name
    Error.captureStackTrace(this, this.constructor)
  }
}

// 具体的なエラー
export class ValidationError extends AppError {
  constructor(message: string, public fields?: Record<string, string>) {
    super(message, 'VALIDATION_ERROR', 400)
  }
}

export class AuthenticationError extends AppError {
  constructor(message: string = '認証が必要です') {
    super(message, 'AUTHENTICATION_ERROR', 401)
  }
}

export class AuthorizationError extends AppError {
  constructor(message: string = '権限がありません') {
    super(message, 'AUTHORIZATION_ERROR', 403)
  }
}

export class NotFoundError extends AppError {
  constructor(resource: string) {
    super(`${resource}が見つかりません`, 'NOT_FOUND', 404)
  }
}

export class ConflictError extends AppError {
  constructor(message: string) {
    super(message, 'CONFLICT', 409)
  }
}
```

## Express エラーハンドリング

### ミドルウェア

```typescript
// asyncハンドラーラッパー
export const asyncHandler = (fn: RequestHandler): RequestHandler => {
  return (req, res, next) => {
    Promise.resolve(fn(req, res, next)).catch(next)
  }
}

// グローバルエラーハンドラー
export const errorHandler: ErrorRequestHandler = (err, req, res, next) => {
  // ログ出力
  logger.error({
    message: err.message,
    stack: err.stack,
    path: req.path,
    method: req.method,
  })

  // 本番環境ではスタックトレースを隠す
  if (err instanceof AppError) {
    return res.status(err.statusCode).json({
      error: {
        code: err.code,
        message: err.message,
        ...(err instanceof ValidationError && { fields: err.fields }),
      },
    })
  }

  // 予期しないエラー
  res.status(500).json({
    error: {
      code: 'INTERNAL_ERROR',
      message: process.env.NODE_ENV === 'production'
        ? 'サーバーエラーが発生しました'
        : err.message,
    },
  })
}
```

### 使用例

```typescript
router.get('/users/:id', asyncHandler(async (req, res) => {
  const user = await userService.findById(req.params.id)
  if (!user) {
    throw new NotFoundError('ユーザー')
  }
  res.json(user)
}))
```

## React エラー境界

```tsx
interface Props {
  children: React.ReactNode
  fallback?: React.ReactNode
}

interface State {
  hasError: boolean
  error?: Error
}

export class ErrorBoundary extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props)
    this.state = { hasError: false }
  }

  static getDerivedStateFromError(error: Error): State {
    return { hasError: true, error }
  }

  componentDidCatch(error: Error, errorInfo: React.ErrorInfo) {
    // エラーレポートサービスに送信
    errorReportingService.log({ error, errorInfo })
  }

  render() {
    if (this.state.hasError) {
      return this.props.fallback || <ErrorFallback error={this.state.error} />
    }
    return this.props.children
  }
}
```

## 非同期エラーパターン

### Result型パターン

```typescript
type Result<T, E = Error> =
  | { success: true; data: T }
  | { success: false; error: E }

async function fetchUser(id: string): Promise<Result<User>> {
  try {
    const user = await db.users.findById(id)
    if (!user) {
      return { success: false, error: new NotFoundError('ユーザー') }
    }
    return { success: true, data: user }
  } catch (error) {
    return { success: false, error: error as Error }
  }
}

// 使用
const result = await fetchUser('123')
if (result.success) {
  console.log(result.data)
} else {
  console.error(result.error)
}
```

## チェックリスト

- [ ] カスタムエラークラスを定義している
- [ ] 適切なHTTPステータスコードを返している
- [ ] エラーログを記録している
- [ ] 本番環境で詳細を隠している
- [ ] ユーザーフレンドリーなメッセージを表示
- [ ] React ErrorBoundaryを実装している
- [ ] 非同期エラーを適切にキャッチしている
