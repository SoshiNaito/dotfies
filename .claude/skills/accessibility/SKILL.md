---
name: accessibility
description: アクセシビリティ (a11y) チェックリストとベストプラクティス。UIコンポーネント実装時に参照。
---

# アクセシビリティ (a11y) ガイド

## 基本原則 (WCAG 2.1)

1. **知覚可能**: 情報とUIを認識できる方法で提示
2. **操作可能**: UIとナビゲーションを操作可能に
3. **理解可能**: 情報とUIの操作が理解可能
4. **堅牢**: 支援技術を含む多様なユーザーエージェントで解釈可能

## セマンティックHTML

```html
<!-- ❌ 悪い例: divの乱用 -->
<div class="header">
  <div class="nav">
    <div class="nav-item">ホーム</div>
  </div>
</div>

<!-- ✅ 良い例: セマンティック要素 -->
<header>
  <nav aria-label="メインナビゲーション">
    <a href="/">ホーム</a>
  </nav>
</header>
```

### 重要なHTML要素

| 要素 | 用途 |
|------|------|
| `<header>` | ページ/セクションのヘッダー |
| `<nav>` | ナビゲーション |
| `<main>` | メインコンテンツ |
| `<article>` | 独立したコンテンツ |
| `<section>` | テーマ別セクション |
| `<aside>` | 補足コンテンツ |
| `<footer>` | フッター |

## フォームアクセシビリティ

```tsx
// ✅ 良い例: ラベル付きフォーム
<form>
  <div>
    <label htmlFor="email">メールアドレス</label>
    <input
      type="email"
      id="email"
      name="email"
      aria-describedby="email-hint email-error"
      aria-invalid={hasError}
      required
    />
    <p id="email-hint">例: user@example.com</p>
    {hasError && (
      <p id="email-error" role="alert">
        有効なメールアドレスを入力してください
      </p>
    )}
  </div>

  <button type="submit">送信</button>
</form>
```

## キーボードナビゲーション

```tsx
// カスタムコンポーネントのキーボード対応
const Dropdown = () => {
  const [isOpen, setIsOpen] = useState(false)

  const handleKeyDown = (e: KeyboardEvent) => {
    switch (e.key) {
      case 'Enter':
      case ' ':
        setIsOpen(!isOpen)
        break
      case 'Escape':
        setIsOpen(false)
        break
      case 'ArrowDown':
        // 次の項目にフォーカス
        break
      case 'ArrowUp':
        // 前の項目にフォーカス
        break
    }
  }

  return (
    <div
      role="listbox"
      tabIndex={0}
      onKeyDown={handleKeyDown}
      aria-expanded={isOpen}
      aria-haspopup="listbox"
    >
      {/* コンテンツ */}
    </div>
  )
}
```

### フォーカス管理

```tsx
// モーダルのフォーカストラップ
const Modal = ({ isOpen, onClose, children }) => {
  const modalRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    if (isOpen) {
      // モーダルにフォーカスを移動
      modalRef.current?.focus()
    }
  }, [isOpen])

  return (
    <div
      ref={modalRef}
      role="dialog"
      aria-modal="true"
      aria-labelledby="modal-title"
      tabIndex={-1}
    >
      <h2 id="modal-title">タイトル</h2>
      {children}
      <button onClick={onClose}>閉じる</button>
    </div>
  )
}
```

## ARIA属性

### よく使うARIA

```tsx
// ライブリージョン（動的更新を通知）
<div aria-live="polite" aria-atomic="true">
  {notification}
</div>

// ローディング状態
<button aria-busy={isLoading} disabled={isLoading}>
  {isLoading ? '処理中...' : '送信'}
</button>

// 展開/折りたたみ
<button
  aria-expanded={isExpanded}
  aria-controls="panel-content"
>
  詳細を見る
</button>
<div id="panel-content" hidden={!isExpanded}>
  {content}
</div>

// タブ
<div role="tablist">
  <button role="tab" aria-selected={activeTab === 0} aria-controls="panel-0">
    タブ1
  </button>
</div>
<div role="tabpanel" id="panel-0" aria-labelledby="tab-0">
  {content}
</div>
```

## 色とコントラスト

```css
/*
 * コントラスト比:
 * - 通常テキスト: 4.5:1以上
 * - 大きなテキスト: 3:1以上
 * - UI要素: 3:1以上
 */

/* ❌ 悪い例: 低コントラスト */
.text {
  color: #999;
  background: #fff;
}

/* ✅ 良い例: 十分なコントラスト */
.text {
  color: #595959;
  background: #fff;
}

/* 色だけに依存しない */
.error {
  color: #d32f2f;
  border-left: 4px solid #d32f2f; /* 色以外の視覚的手がかり */
}

.error::before {
  content: "⚠ "; /* アイコンも追加 */
}
```

## 画像とメディア

```tsx
// 意味のある画像
<img src="chart.png" alt="2023年の売上推移グラフ。1月から12月まで右肩上がり" />

// 装飾的な画像
<img src="decorative.png" alt="" role="presentation" />

// 動画
<video controls>
  <source src="video.mp4" type="video/mp4" />
  <track kind="captions" src="captions.vtt" srclang="ja" label="日本語字幕" />
</video>
```

## チェックリスト

### 構造
- [ ] セマンティックHTMLを使用している
- [ ] 見出しが論理的な階層になっている (h1→h2→h3)
- [ ] ランドマーク（header, main, nav, footer）を使用している

### フォーム
- [ ] 全てのinputにlabelが関連付けられている
- [ ] エラーメッセージが明確で関連付けられている
- [ ] 必須フィールドが明示されている

### キーボード
- [ ] 全ての機能がキーボードで操作可能
- [ ] フォーカスが見える
- [ ] フォーカス順序が論理的

### ビジュアル
- [ ] コントラスト比が十分（4.5:1以上）
- [ ] 色だけで情報を伝えていない
- [ ] テキストが200%まで拡大可能

### 画像・メディア
- [ ] 画像に適切なaltテキストがある
- [ ] 動画に字幕がある

## テストツール

```bash
# axe-core (自動テスト)
npm install @axe-core/react

# eslint-plugin-jsx-a11y
npm install eslint-plugin-jsx-a11y

# Lighthouse
npx lighthouse https://example.com --only-categories=accessibility
```

```tsx
// axe-coreの使用
import { axe, toHaveNoViolations } from 'jest-axe'

test('アクセシビリティ違反がないこと', async () => {
  const { container } = render(<MyComponent />)
  const results = await axe(container)
  expect(results).toHaveNoViolations()
})
```
