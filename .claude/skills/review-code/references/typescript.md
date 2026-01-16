# TypeScript チェック項目

TypeScript 固有のコードレビュー観点。

※ JavaScript の基本的なチェック項目は `javascript.md` を参照。

---

## 型定義

### 基本
- [ ] `any` の使用を避けている
- [ ] `unknown` を適切に使用している
- [ ] Union型 / Intersection型の適切な使用
- [ ] Generics の活用
- [ ] 型推論を活かしている（過剰な型注釈を避ける）

```typescript
// 推奨
function processValue(value: unknown): string {
  if (typeof value === 'string') {
    return value.toUpperCase();
  }
  return String(value);
}

// 非推奨
function processValue(value: any): string {
  return value.toUpperCase(); // 実行時エラーの可能性
}
```

### 型ガード
- [ ] 型ガードの適切な実装
- [ ] `typeof`, `instanceof` の活用
- [ ] カスタム型ガード関数の定義

```typescript
// カスタム型ガード
function isUser(value: unknown): value is User {
  return (
    typeof value === 'object' &&
    value !== null &&
    'id' in value &&
    'name' in value
  );
}
```

---

## 型の設計

### interface vs type
- [ ] `interface` vs `type` の一貫した使い分け
- [ ] オブジェクト型には `interface` を優先（拡張性）
- [ ] Union型、Intersection型、プリミティブ型には `type` を使用

### プロパティ
- [ ] オプショナルプロパティ (`?`) の適切な使用
- [ ] `readonly` の活用
- [ ] インデックスシグネチャの適切な使用

```typescript
interface User {
  readonly id: string;
  name: string;
  email: string;
  profile?: UserProfile;
}
```

### Utility Types
- [ ] Utility Types の活用（`Partial`, `Pick`, `Omit`, `Record`, `Required` など）
- [ ] 型のエクスポート

```typescript
type CreateUserInput = Omit<User, 'id'>;
type UpdateUserInput = Partial<Pick<User, 'name' | 'email'>>;
type UserMap = Record<string, User>;
```

---

## 型安全性

### 避けるべきパターン
- [ ] Non-null assertion (`!`) の乱用を避ける
- [ ] 型アサーション (`as`) の乱用を避ける
- [ ] `// @ts-ignore` の使用を避ける

```typescript
// 非推奨
const name = user!.name;
const data = response as UserData;

// 推奨
if (user) {
  const name = user.name;
}
```

### Null/Undefined ハンドリング
- [ ] `strictNullChecks` 有効時の適切な null チェック
- [ ] Optional chaining (`?.`) の活用
- [ ] Nullish coalescing (`??`) の活用

---

## Discriminated Unions

- [ ] Discriminated Unions の活用（状態管理、結果型など）
- [ ] 網羅性チェック（exhaustive check）

```typescript
type Result<T> = 
  | { success: true; data: T }
  | { success: false; error: string };

function handleResult(result: Result<User>) {
  if (result.success) {
    console.log(result.data.name);
  } else {
    console.error(result.error);
  }
}

// 網羅性チェック
type Status = 'pending' | 'approved' | 'rejected';

function getStatusLabel(status: Status): string {
  switch (status) {
    case 'pending':
      return '保留中';
    case 'approved':
      return '承認済み';
    case 'rejected':
      return '却下';
    default:
      const _exhaustive: never = status;
      return _exhaustive;
  }
}
```

---

## 関数の型

- [ ] 関数の引数と戻り値の型定義
- [ ] オーバーロードの適切な使用
- [ ] コールバック関数の型定義

```typescript
// 関数型の定義
type Formatter<T> = (value: T) => string;

// ジェネリック関数
function first<T>(array: T[]): T | undefined {
  return array[0];
}

// オーバーロード
function parse(value: string): number;
function parse(value: string, radix: number): number;
function parse(value: string, radix?: number): number {
  return parseInt(value, radix ?? 10);
}
```

---

## Enum と定数

- [ ] `const enum` の検討（バンドルサイズ削減）
- [ ] 文字列リテラル型 / Union型 での代替検討
- [ ] `as const` の活用

```typescript
// 文字列リテラル型（推奨）
type Status = 'pending' | 'approved' | 'rejected';

// as const
const COLORS = {
  red: '#ff0000',
  green: '#00ff00',
  blue: '#0000ff',
} as const;

type Color = typeof COLORS[keyof typeof COLORS];
```

---

## モジュールと型のエクスポート

- [ ] 型のエクスポート（`export type`）
- [ ] 再エクスポートの整理
- [ ] 型定義ファイル (`.d.ts`) の適切な管理

```typescript
// 型のみエクスポート
export type { User, UserProfile };

// 値と型を同時にエクスポート
export { createUser, type CreateUserInput };
```

---

## クイックリファレンス

### tsconfig 厳格モード推奨設定

```json
{
  "compilerOptions": {
    "strict": true,
    "noImplicitAny": true,
    "strictNullChecks": true,
    "noImplicitReturns": true,
    "noUncheckedIndexedAccess": true,
    "noImplicitOverride": true
  }
}
```

### よく使う Utility Types

| 型 | 説明 |
|---|------|
| `Partial<T>` | すべてのプロパティをオプショナルに |
| `Required<T>` | すべてのプロパティを必須に |
| `Readonly<T>` | すべてのプロパティを読み取り専用に |
| `Pick<T, K>` | 指定したプロパティのみ抽出 |
| `Omit<T, K>` | 指定したプロパティを除外 |
| `Record<K, V>` | キーと値の型を指定したオブジェクト |
| `NonNullable<T>` | null と undefined を除外 |
| `ReturnType<T>` | 関数の戻り値の型を取得 |
| `Parameters<T>` | 関数の引数の型をタプルで取得 |