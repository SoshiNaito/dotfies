# HTML チェック項目

HTML およびWebアクセシビリティに関するコードレビュー観点。

---

## 文書構造

- [ ] 正しい DOCTYPE 宣言 (`<!DOCTYPE html>`)
- [ ] `<html>` に `lang` 属性がある
- [ ] `<head>` に適切な `<meta>` タグがある
- [ ] `<title>` が設定されている
- [ ] 文字エンコーディングが指定されている (`<meta charset="UTF-8">`)
- [ ] viewport が設定されている

```html
<!DOCTYPE html>
<html lang="ja">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="description" content="ページの説明">
  <title>ページタイトル</title>
</head>
```

---

## セマンティックHTML

### 構造要素
- [ ] 適切なセマンティック要素を使用（`<header>`, `<nav>`, `<main>`, `<article>`, `<section>`, `<aside>`, `<footer>`）
- [ ] `<main>` は1ページに1つ
- [ ] `<div>` / `<span>` の乱用を避けている

```html
<!-- 推奨: セマンティック -->
<article>
  <header>
    <h2>記事タイトル</h2>
    <time datetime="2024-01-15">2024年1月15日</time>
  </header>
  <p>本文...</p>
</article>

<!-- 非推奨: div だらけ -->
<div class="article">
  <div class="article-header">
    <div class="article-title">記事タイトル</div>
  </div>
</div>
```

### 見出し
- [ ] 見出しの階層が正しい（`<h1>` → `<h2>` → `<h3>`、スキップしない）
- [ ] `<h1>` は1ページに1つ（推奨）
- [ ] 見出しがスタイル目的で使われていない

### リスト・テーブル
- [ ] リストには `<ul>`, `<ol>`, `<dl>` を使用
- [ ] テーブルは表形式データにのみ使用（レイアウト目的で使わない）
- [ ] テーブルに `<caption>`, `<thead>`, `<tbody>` を適切に使用
- [ ] テーブルヘッダーに `<th>` と `scope` 属性

```html
<table>
  <caption>売上データ</caption>
  <thead>
    <tr>
      <th scope="col">商品名</th>
      <th scope="col">数量</th>
      <th scope="col">金額</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>商品A</td>
      <td>10</td>
      <td>1,000円</td>
    </tr>
  </tbody>
</table>
```

---

## フォーム

- [ ] `<form>` に適切な `action` と `method`
- [ ] すべての入力要素に `<label>` が関連付けられている
- [ ] `<fieldset>` と `<legend>` でグループ化
- [ ] 適切な `type` 属性（`email`, `tel`, `number`, `date` など）
- [ ] `required`, `pattern` などのバリデーション属性
- [ ] `autocomplete` 属性の設定
- [ ] `placeholder` はラベルの代わりに使わない

```html
<form action="/contact" method="POST">
  <fieldset>
    <legend>連絡先情報</legend>
    
    <label for="email">メールアドレス</label>
    <input 
      type="email" 
      id="email" 
      name="email" 
      required
      autocomplete="email"
    >
    
    <label for="phone">電話番号</label>
    <input 
      type="tel" 
      id="phone" 
      name="phone"
      pattern="[0-9]{2,4}-[0-9]{2,4}-[0-9]{3,4}"
      autocomplete="tel"
    >
  </fieldset>
  
  <button type="submit">送信</button>
</form>
```

---

## 画像・メディア

- [ ] すべての `<img>` に `alt` 属性がある
- [ ] 装飾画像は `alt=""` （空）
- [ ] `width` / `height` 属性でレイアウトシフト防止
- [ ] 遅延読み込み（`loading="lazy"`）の適用
- [ ] レスポンシブ画像（`srcset`, `<picture>`）の活用

```html
<!-- コンテンツ画像 -->
<img 
  src="product.jpg" 
  alt="赤いTシャツ、フロントにロゴ入り"
  width="400"
  height="300"
  loading="lazy"
>

<!-- 装飾画像 -->
<img src="divider.png" alt="" role="presentation">

<!-- レスポンシブ画像 -->
<picture>
  <source media="(min-width: 800px)" srcset="large.jpg">
  <source media="(min-width: 400px)" srcset="medium.jpg">
  <img src="small.jpg" alt="説明">
</picture>
```

---

## リンク・ナビゲーション

- [ ] リンクテキストが意味を持つ（「ここをクリック」を避ける）
- [ ] 外部リンクに `rel="noopener noreferrer"`（`target="_blank"` 使用時）
- [ ] スキップリンクの設置
- [ ] 現在のページを示す `aria-current="page"`

```html
<!-- 推奨 -->
<a href="/products">製品一覧を見る</a>
<a href="https://example.com" target="_blank" rel="noopener noreferrer">
  外部サイト（新しいタブで開きます）
</a>

<!-- スキップリンク -->
<a href="#main-content" class="skip-link">メインコンテンツへスキップ</a>

<!-- 非推奨 -->
<a href="/products">ここをクリック</a>
```

---

## アクセシビリティ（a11y）

### 基本
- [ ] すべての機能がキーボードで操作可能
- [ ] フォーカス表示が視認できる
- [ ] 色だけに依存しない情報伝達
- [ ] 十分なターゲットサイズ（44x44px以上推奨）

### ARIA

#### 基本原則
- [ ] ネイティブHTML要素を優先（ARIAは最後の手段）
- [ ] `role` の正しい使用
- [ ] 状態の更新（`aria-expanded`, `aria-selected` など）

```html
<!-- 非推奨: ARIAで実装 -->
<div role="button" tabindex="0" onclick="submit()">送信</div>

<!-- 推奨: ネイティブ要素 -->
<button type="submit">送信</button>
```

#### よく使うARIA属性
- [ ] `aria-label` / `aria-labelledby`: ラベル付け
- [ ] `aria-describedby`: 追加説明
- [ ] `aria-hidden`: 支援技術から隠す
- [ ] `aria-live`: 動的更新の通知
- [ ] `aria-expanded`: 展開状態
- [ ] `aria-current`: 現在の項目

```html
<!-- アコーディオン -->
<button 
  aria-expanded="false" 
  aria-controls="panel1"
>
  セクション1
</button>
<div id="panel1" hidden>
  パネルの内容
</div>

<!-- ライブリージョン -->
<div aria-live="polite" aria-atomic="true">
  <!-- 動的に更新される内容 -->
</div>
```

---

## パフォーマンス

- [ ] CSS は `<head>` 内で読み込み
- [ ] JS は `</body>` 直前または `defer` / `async` 属性付き
- [ ] 不要な DOM 要素を減らしている
- [ ] リソースのプリロード/プリフェッチの活用

```html
<head>
  <link rel="preload" href="critical.css" as="style">
  <link rel="preconnect" href="https://api.example.com">
</head>
<body>
  <!-- コンテンツ -->
  <script src="app.js" defer></script>
</body>
```

---

## セキュリティ

- [ ] `target="_blank"` に `rel="noopener noreferrer"`
- [ ] フォームの CSRF 対策
- [ ] 機密情報を HTML に含めない
- [ ] Content Security Policy (CSP) の検討

---

## クイックリファレンス

### バリデーションツール
- W3C Markup Validation Service
- HTMLHint
- axe DevTools（アクセシビリティ）

### アクセシビリティチェックツール

| ツール | 用途 |
|-------|------|
| axe DevTools | 自動a11yテスト |
| WAVE | Webアクセシビリティ評価 |
| Lighthouse | パフォーマンス・a11y監査 |
| NVDA / VoiceOver | スクリーンリーダーテスト |