# シナリオ4: リファクタリングワークフロー

肥大化した `OrderService` クラスをリファクタリングする例です。

## 前提条件

- 対象ファイルが存在
- テストが十分に書かれている

## ステップ

### Step 1: リファクタリングを開始

```
あなた: /refactor src/services/orderService.ts
```

**Claudeの動作:**
```
🔍 対象ファイルを分析中...

ファイル: src/services/orderService.ts
行数: 450行
関数数: 12
```

### Step 2: 問題点の分析

**Claudeの出力:**
```
📊 コード分析結果

📁 src/services/orderService.ts

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🚨 検出された問題

1. **巨大クラス (God Class)**
   - 行数: 450行 (推奨: 200行以下)
   - 責任: 注文管理、在庫管理、通知、レポート
   - → 単一責任の原則に違反

2. **長いメソッド**
   - `createOrder()`: 120行
   - `processPayment()`: 85行
   - → 関数抽出が必要

3. **重複コード**
   - バリデーションロジックが3箇所で重複
   - エラーハンドリングパターンが5箇所で重複

4. **マジックナンバー**
   - `if (items.length > 50)` (L:78)
   - `discount * 0.1` (L:145)
   - → 定数化が必要

5. **深いネスト**
   - `processPayment()` 内で5段階のネスト
   - → 早期リターンでフラット化

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📋 リファクタリング計画

**Phase 1: 定数の抽出**
- マジックナンバーを定数に置き換え
- 影響範囲: 小、リスク: 低

**Phase 2: メソッドの抽出**
- 長いメソッドを小さな関数に分割
- 影響範囲: 中、リスク: 低

**Phase 3: クラスの分割**
- InventoryService を抽出
- NotificationService を抽出
- ReportService を抽出
- 影響範囲: 大、リスク: 中

**Phase 4: 重複の削除**
- 共通ロジックをユーティリティに抽出
- 影響範囲: 中、リスク: 低

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

この計画で進めてよろしいですか？
段階的に実行することを推奨します。
```

### Step 3: Phase 1を実行

```
あなた: Phase 1から始めてください
```

**Claudeの動作:**
```
🔄 Phase 1: 定数の抽出

現在のテストを実行...
✅ 全テストパス (45件)

変更開始...
```

**Claudeの出力:**
```
📝 Phase 1 完了: 定数の抽出

変更内容:
---
// Before
if (items.length > 50) { ... }
const discountedPrice = price * 0.1

// After
const MAX_ITEMS_PER_ORDER = 50
const BULK_DISCOUNT_RATE = 0.1

if (items.length > MAX_ITEMS_PER_ORDER) { ... }
const discountedPrice = price * BULK_DISCOUNT_RATE
---

抽出した定数:
- MAX_ITEMS_PER_ORDER = 50
- BULK_DISCOUNT_RATE = 0.1
- MAX_RETRY_ATTEMPTS = 3
- PAYMENT_TIMEOUT_MS = 30000

テスト実行...
✅ 全テストパス (45件)

次のPhaseに進みますか？
```

### Step 4: Phase 2を実行

```
あなた: Phase 2に進んでください
```

**Claudeの動作:**
```
🔄 Phase 2: メソッドの抽出

createOrder() を分析中...
```

**Claudeの出力:**
```
📝 Phase 2: メソッドの抽出

━━━━ createOrder() のリファクタリング ━━━━

Before (120行):
```typescript
async createOrder(data: CreateOrderDto): Promise<Order> {
  // バリデーション (20行)
  // 在庫チェック (25行)
  // 価格計算 (30行)
  // DB保存 (15行)
  // 通知送信 (20行)
  // ログ記録 (10行)
}
```

After (25行):
```typescript
async createOrder(data: CreateOrderDto): Promise<Order> {
  this.validateOrderData(data)
  await this.checkInventory(data.items)

  const pricing = this.calculatePricing(data.items, data.couponCode)
  const order = await this.saveOrder(data, pricing)

  await this.sendOrderNotification(order)
  this.logOrderCreation(order)

  return order
}

private validateOrderData(data: CreateOrderDto): void { ... }
private async checkInventory(items: OrderItem[]): Promise<void> { ... }
private calculatePricing(items: OrderItem[], coupon?: string): Pricing { ... }
private async saveOrder(data: CreateOrderDto, pricing: Pricing): Promise<Order> { ... }
private async sendOrderNotification(order: Order): Promise<void> { ... }
private logOrderCreation(order: Order): void { ... }
```

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

同様に processPayment() もリファクタリング完了

テスト実行...
✅ 全テストパス (45件)

変更サマリー:
- createOrder(): 120行 → 25行 + 6つのプライベートメソッド
- processPayment(): 85行 → 20行 + 4つのプライベートメソッド

次のPhaseに進みますか？
```

### Step 5: Phase 3を実行

```
あなた: Phase 3に進んでください
```

**Claudeの出力:**
```
🔄 Phase 3: クラスの分割

⚠️ 注意: このフェーズは影響範囲が大きいです

変更内容:
1. InventoryService を新規作成
2. NotificationService を新規作成
3. ReportService を新規作成
4. OrderService を依存性注入に変更

影響を受けるファイル:
- src/services/orderService.ts
- src/services/inventoryService.ts (新規)
- src/services/notificationService.ts (新規)
- src/services/reportService.ts (新規)
- src/api/orders.ts (DI設定変更)
- src/tests/orderService.test.ts

続行しますか？ [Y/n]
```

```
あなた: はい
```

**Claudeの動作:**
```
📁 新規ファイル作成: src/services/inventoryService.ts

export class InventoryService {
  async checkStock(items: OrderItem[]): Promise<StockResult> { ... }
  async reserveStock(orderId: string, items: OrderItem[]): Promise<void> { ... }
  async releaseStock(orderId: string): Promise<void> { ... }
}
```

```
📁 新規ファイル作成: src/services/notificationService.ts

export class NotificationService {
  async sendOrderConfirmation(order: Order): Promise<void> { ... }
  async sendShippingNotification(order: Order): Promise<void> { ... }
  async sendPaymentReceipt(order: Order): Promise<void> { ... }
}
```

```
📁 OrderService の更新

export class OrderService {
  constructor(
    private readonly inventoryService: InventoryService,
    private readonly notificationService: NotificationService,
    private readonly orderRepository: OrderRepository
  ) {}

  async createOrder(data: CreateOrderDto): Promise<Order> {
    this.validateOrderData(data)
    await this.inventoryService.checkStock(data.items)

    const pricing = this.calculatePricing(data.items, data.couponCode)
    const order = await this.saveOrder(data, pricing)

    await this.inventoryService.reserveStock(order.id, data.items)
    await this.notificationService.sendOrderConfirmation(order)

    return order
  }
}
```

**Claudeの出力:**
```
📝 Phase 3 完了: クラスの分割

作成したファイル:
├── src/services/
│   ├── orderService.ts      (450行 → 150行)
│   ├── inventoryService.ts  (新規: 80行)
│   ├── notificationService.ts (新規: 60行)
│   └── reportService.ts     (新規: 70行)

テスト実行...
✅ 全テストパス (52件) ← 7件追加

各クラスの責任:
- OrderService: 注文の作成・管理のみ
- InventoryService: 在庫管理
- NotificationService: 通知送信
- ReportService: レポート生成
```

### Step 6: 最終確認

```
あなた: 全体のテストを実行して最終確認して
```

**Claudeの出力:**
```
🧪 最終テスト実行

npm test

  OrderService
    ✓ 注文を作成できる
    ✓ バリデーションエラーを返す
    ✓ 在庫不足でエラーを返す
    ...

  InventoryService
    ✓ 在庫をチェックできる
    ✓ 在庫を予約できる
    ...

  NotificationService
    ✓ 注文確認メールを送信できる
    ...

Test Suites: 15 passed, 15 total
Tests:       52 passed, 52 total
Time:        6.234s

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📊 リファクタリング結果

Before:
- ファイル数: 1
- 総行数: 450行
- クラス数: 1
- 責任: 4つ (違反)

After:
- ファイル数: 4
- 総行数: 360行 (20%削減)
- クラス数: 4
- 責任: 各1つ (SOLID準拠)

改善点:
✅ 単一責任の原則に準拠
✅ テスト容易性が向上
✅ コードの再利用性が向上
✅ 保守性が向上
```

### Step 7: コミット

```
あなた: /commit
```

**Claudeの出力:**
```
📝 提案するコミットメッセージ:
---
refactor(orders): OrderServiceを責任ごとにクラス分割

- InventoryService: 在庫管理ロジックを分離
- NotificationService: 通知ロジックを分離
- ReportService: レポート生成ロジックを分離
- 定数抽出とメソッド抽出を実施
- テストカバレッジを維持（52件パス）

BREAKING CHANGE: OrderServiceのコンストラクタ引数が変更
---

このメッセージでコミットしますか？ [Y/n]
```

## リファクタリングパターン一覧

| パターン | Before | After |
|---------|--------|-------|
| 定数抽出 | `if (x > 100)` | `if (x > MAX_LIMIT)` |
| メソッド抽出 | 100行の関数 | 20行 + 5つの関数 |
| クラス分割 | 1つの巨大クラス | 責任ごとの小さなクラス |
| 重複削除 | 同じコードが3箇所 | 1つのユーティリティ |
| 早期リターン | 深いネスト | フラットな構造 |
