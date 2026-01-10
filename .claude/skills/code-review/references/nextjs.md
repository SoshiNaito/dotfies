# Next.js チェック項目

Next.js 固有のコードレビュー観点。

※ JavaScript / TypeScript の基本的なチェック項目は `javascript.md`, `typescript.md` を参照。

---

## App Router（Next.js 13+）

### ファイル構造
- [ ] `app/` ディレクトリを使用
- [ ] 適切なファイル規約（`page.tsx`, `layout.tsx`, `loading.tsx`, `error.tsx`, `not-found.tsx`）
- [ ] ルートグループ `(folder)` の適切な使用
- [ ] プライベートフォルダ `_folder` の活用

```
app/
├── layout.tsx          # ルートレイアウト
├── page.tsx            # ホームページ
├── loading.tsx         # ローディングUI
├── error.tsx           # エラーUI
├── not-found.tsx       # 404ページ
├── (marketing)/        # ルートグループ
│   ├── about/
│   └── contact/
├── dashboard/
│   ├── layout.tsx      # ネストレイアウト
│   └── page.tsx
└── api/
    └── users/
        └── route.ts    # Route Handler
```

### Server Components vs Client Components
- [ ] デフォルトは Server Component を使用
- [ ] `'use client'` は必要な場合のみ使用
- [ ] Client Component は末端（リーフ）に配置
- [ ] Server Component 内で Client Component をインポート可能

```tsx
// Server Component（デフォルト）
async function ProductList() {
  const products = await fetchProducts(); // サーバーで実行
  return (
    <ul>
      {products.map(p => (
        <ProductCard key={p.id} product={p} />
      ))}
    </ul>
  );
}

// Client Component
'use client';

import { useState } from 'react';

function AddToCartButton({ productId }: { productId: string }) {
  const [isLoading, setIsLoading] = useState(false);
  // インタラクティブな処理
}
```

### Client Component が必要なケース
- [ ] `useState`, `useEffect` などの React Hooks
- [ ] イベントハンドラ（`onClick`, `onChange` など）
- [ ] ブラウザAPI（`window`, `document`）
- [ ] クラスコンポーネント

---

## データフェッチング

### Server Components でのフェッチ
- [ ] `async/await` を直接使用
- [ ] `fetch` のキャッシュ設定を適切に指定
- [ ] 並列フェッチの活用

```tsx
// キャッシュ設定
const data = await fetch('https://api.example.com/data', {
  cache: 'force-cache',    // デフォルト: 静的
  // cache: 'no-store',    // 動的: 毎回フェッチ
  // next: { revalidate: 3600 }, // ISR: 1時間ごと
});

// 並列フェッチ
async function Page() {
  const [users, posts] = await Promise.all([
    fetchUsers(),
    fetchPosts(),
  ]);
  return <div>...</div>;
}
```

### 動的レンダリング
- [ ] `cookies()`, `headers()` 使用時は動的になる
- [ ] `searchParams` 使用時は動的になる
- [ ] 意図しない動的レンダリングを避ける

---

## Server Actions

- [ ] `'use server'` ディレクティブの使用
- [ ] フォーム送信での活用
- [ ] バリデーションの実装
- [ ] エラーハンドリング
- [ ] 適切な revalidation

```tsx
// actions.ts
'use server';

import { revalidatePath } from 'next/cache';
import { redirect } from 'next/navigation';

export async function createPost(formData: FormData) {
  const title = formData.get('title') as string;
  
  // バリデーション
  if (!title || title.length < 3) {
    return { error: 'タイトルは3文字以上必要です' };
  }
  
  // DB保存
  await db.post.create({ data: { title } });
  
  // キャッシュ更新
  revalidatePath('/posts');
  redirect('/posts');
}
```

```tsx
// form.tsx
'use client';

import { createPost } from './actions';

function CreatePostForm() {
  return (
    <form action={createPost}>
      <input name="title" required />
      <button type="submit">作成</button>
    </form>
  );
}
```

---

## Route Handlers（API Routes）

- [ ] 適切な HTTP メソッドの実装
- [ ] レスポンスの型定義
- [ ] エラーハンドリング
- [ ] 認証・認可チェック

```tsx
// app/api/users/route.ts
import { NextRequest, NextResponse } from 'next/server';

export async function GET(request: NextRequest) {
  const searchParams = request.nextUrl.searchParams;
  const query = searchParams.get('query');
  
  const users = await fetchUsers(query);
  
  return NextResponse.json(users);
}

export async function POST(request: NextRequest) {
  try {
    const body = await request.json();
    const user = await createUser(body);
    return NextResponse.json(user, { status: 201 });
  } catch (error) {
    return NextResponse.json(
      { error: 'Failed to create user' },
      { status: 500 }
    );
  }
}
```

---

## レンダリング戦略

### 静的 vs 動的
- [ ] 可能な限り静的レンダリングを優先
- [ ] `generateStaticParams` での静的パス生成
- [ ] 適切な revalidate 設定

```tsx
// 静的生成
export async function generateStaticParams() {
  const posts = await fetchPosts();
  return posts.map((post) => ({
    slug: post.slug,
  }));
}

// ISR
export const revalidate = 3600; // 1時間
```

### Streaming と Suspense
- [ ] `loading.tsx` でのローディングUI
- [ ] `<Suspense>` での部分的ローディング
- [ ] 重いコンポーネントの遅延読み込み

```tsx
import { Suspense } from 'react';

export default function Page() {
  return (
    <div>
      <h1>Dashboard</h1>
      <Suspense fallback={<Loading />}>
        <SlowComponent />
      </Suspense>
    </div>
  );
}
```

---

## メタデータ・SEO

- [ ] `metadata` オブジェクトまたは `generateMetadata` の使用
- [ ] 適切な `title`, `description`
- [ ] OGP 設定
- [ ] `robots.txt`, `sitemap.xml` の生成

```tsx
// 静的メタデータ
export const metadata: Metadata = {
  title: 'ページタイトル',
  description: 'ページの説明',
  openGraph: {
    title: 'OGタイトル',
    description: 'OG説明',
    images: ['/og-image.png'],
  },
};

// 動的メタデータ
export async function generateMetadata({ params }): Promise<Metadata> {
  const post = await fetchPost(params.slug);
  return {
    title: post.title,
    description: post.excerpt,
  };
}
```

---

## 画像最適化

- [ ] `next/image` の `Image` コンポーネントを使用
- [ ] `width`, `height` または `fill` の指定
- [ ] 適切な `priority` 設定（LCP画像）
- [ ] `sizes` 属性でレスポンシブ対応

```tsx
import Image from 'next/image';

// 固定サイズ
<Image
  src="/hero.jpg"
  alt="説明"
  width={800}
  height={600}
  priority // LCP画像
/>

// レスポンシブ
<Image
  src="/hero.jpg"
  alt="説明"
  fill
  sizes="(max-width: 768px) 100vw, 50vw"
  style={{ objectFit: 'cover' }}
/>
```

---

## パフォーマンス

- [ ] 不要な `'use client'` を避ける
- [ ] 動的インポート (`next/dynamic`) の活用
- [ ] バンドルサイズの監視
- [ ] 適切なキャッシュ戦略

```tsx
import dynamic from 'next/dynamic';

// クライアントサイドのみで読み込み
const Chart = dynamic(() => import('@/components/Chart'), {
  ssr: false,
  loading: () => <p>Loading...</p>,
});
```

---

## ミドルウェア

- [ ] `middleware.ts` の適切な配置（ルート）
- [ ] マッチャー設定
- [ ] 認証・認可チェック
- [ ] リダイレクト・リライト

```tsx
// middleware.ts
import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

export function middleware(request: NextRequest) {
  const token = request.cookies.get('token');
  
  if (!token && request.nextUrl.pathname.startsWith('/dashboard')) {
    return NextResponse.redirect(new URL('/login', request.url));
  }
  
  return NextResponse.next();
}

export const config = {
  matcher: ['/dashboard/:path*', '/api/:path*'],
};
```

---

## 環境変数

- [ ] `NEXT_PUBLIC_` プレフィックスの理解
- [ ] サーバーのみの環境変数を公開しない
- [ ] `.env.local` の `.gitignore` 登録

```
# .env.local
DATABASE_URL=...           # サーバーのみ
NEXT_PUBLIC_API_URL=...    # クライアントでも使用可
```

---

## クイックリファレンス

### ファイル規約

| ファイル | 用途 |
|---------|------|
| `page.tsx` | ページコンポーネント |
| `layout.tsx` | 共有レイアウト |
| `loading.tsx` | ローディングUI |
| `error.tsx` | エラーUI |
| `not-found.tsx` | 404ページ |
| `route.ts` | API Route Handler |
| `template.tsx` | 再マウントするレイアウト |
| `default.tsx` | Parallel Routes のデフォルト |

### キャッシュ設定

| 設定 | 説明 |
|-----|------|
| `cache: 'force-cache'` | 静的（デフォルト） |
| `cache: 'no-store'` | 動的、毎回フェッチ |
| `next: { revalidate: N }` | N秒ごとに再検証（ISR） |
| `next: { tags: ['tag'] }` | タグベースの再検証 |