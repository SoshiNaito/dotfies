# テストパターン集

## 基本パターン

### 1. 単純な値のテスト
```typescript
describe('計算機能', () => {
  it('2つの数値を足し算する', () => {
    const result = add(2, 3)
    expect(result).toBe(5)
  })
})
```

### 2. 非同期処理のテスト
```typescript
describe('API呼び出し', () => {
  it('ユーザーデータを取得する', async () => {
    const user = await fetchUser(1)
    expect(user.id).toBe(1)
    expect(user.name).toBeDefined()
  })
})
```

### 3. エラーハンドリングのテスト
```typescript
describe('バリデーション', () => {
  it('無効なメールアドレスでエラーをスローする', () => {
    expect(() => validateEmail('invalid')).toThrow('無効なメールアドレス')
  })
})
```

## モックパターン

### 1. 依存関係のモック
```typescript
describe('UserService', () => {
  it('ユーザーを作成する', async () => {
    const mockRepository = {
      save: jest.fn().mockResolvedValue({ id: 1, email: 'test@example.com' })
    }
    
    const service = new UserService(mockRepository)
    const user = await service.createUser({ email: 'test@example.com' })
    
    expect(mockRepository.save).toHaveBeenCalledTimes(1)
    expect(user.id).toBe(1)
  })
})
```

### 2. 外部API呼び出しのモック
```typescript
jest.mock('axios')

describe('外部API', () => {
  it('天気情報を取得する', async () => {
    axios.get.mockResolvedValue({ data: { temp: 25 } })
    
    const weather = await getWeather('Tokyo')
    expect(weather.temp).toBe(25)
  })
})
```

## エッジケースパターン

### 1. 空配列・null・undefined
```typescript
describe('配列処理', () => {
  it('空配列を処理する', () => {
    expect(processArray([])).toEqual([])
  })
  
  it('nullを処理する', () => {
    expect(processArray(null)).toEqual([])
  })
  
  it('undefinedを処理する', () => {
    expect(processArray(undefined)).toEqual([])
  })
})
```

### 2. 境界値テスト
```typescript
describe('年齢バリデーション', () => {
  it('最小値(0)を受け入れる', () => {
    expect(validateAge(0)).toBe(true)
  })
  
  it('最大値(150)を受け入れる', () => {
    expect(validateAge(150)).toBe(true)
  })
  
  it('負の値を拒否する', () => {
    expect(validateAge(-1)).toBe(false)
  })
  
  it('150を超える値を拒否する', () => {
    expect(validateAge(151)).toBe(false)
  })
})
```

## 統合テストパターン

### 1. データベーステスト
```typescript
describe('User Repository統合テスト', () => {
  beforeEach(async () => {
    await setupTestDatabase()
  })
  
  afterEach(async () => {
    await cleanupTestDatabase()
  })
  
  it('ユーザーを保存して取得する', async () => {
    const user = await repository.save({ email: 'test@example.com' })
    const found = await repository.findById(user.id)
    
    expect(found.email).toBe('test@example.com')
  })
})
```

### 2. APIエンドポイントテスト
```typescript
describe('POST /api/users', () => {
  it('新しいユーザーを作成する', async () => {
    const response = await request(app)
      .post('/api/users')
      .send({ email: 'test@example.com', password: 'password123' })
      .expect(201)
    
    expect(response.body.user.email).toBe('test@example.com')
    expect(response.body.user.password).toBeUndefined() // パスワードは返さない
  })
})
```