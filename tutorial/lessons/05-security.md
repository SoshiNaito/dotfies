# Lesson 05: セキュリティ強化

## 概要
このレッスンでは、security-check skillを活用して、APIのセキュリティを強化する方法を学びます。

**所要時間**: 約60分

## 学習目標
- security-check skillの活用
- 入力バリデーションの実装
- エラーハンドリングの改善
- セキュリティベストプラクティス

---

## 課題: Task APIのセキュリティ強化

現在のTask APIには以下のセキュリティ上の問題があります:

1. **入力バリデーションなし**: どんな値でも受け入れる
2. **エラー情報の漏洩**: スタックトレースがレスポンスに含まれる
3. **適切なHTTPステータスなし**: すべてのエラーが500

---

## Step 1: セキュリティ監査

### 1.1 security-check skillの活用

```
セキュリティ観点でこのAPIをレビューして
```

security-check skillが自動的にロードされ、以下をチェックします:

- 入力バリデーション
- 認証・認可
- エラーハンドリング
- データ保護
- 一般的な脆弱性

### 1.2 監査結果の確認

指摘される問題例:

```
## セキュリティ監査結果

### 高リスク
1. 入力バリデーションなし
   - TaskController.createTask(): リクエストボディの検証なし
   - SQLインジェクションの可能性は低いがデータ整合性の問題

### 中リスク
2. エラー情報の漏洩
   - RuntimeExceptionのスタックトレースが外部に露出
   - 内部実装の詳細が漏洩する可能性

### 低リスク
3. レート制限なし
   - DoS攻撃に対する防御がない
```

---

## Step 2: 入力バリデーションの追加

### 2.1 Bean Validationの導入

```
Taskエンティティに入力バリデーションを追加して
```

**Task.java の変更**:
```java
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "タイトルは必須です")
    @Size(max = 200, message = "タイトルは200文字以内で入力してください")
    private String title;

    @Size(max = 1000, message = "説明は1000文字以内で入力してください")
    private String description;

    // ...
}
```

### 2.2 Controllerでのバリデーション有効化

```
TaskControllerでバリデーションを有効にして
```

**TaskController.java の変更**:
```java
import jakarta.validation.Valid;

@PostMapping
public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
    Task createdTask = taskService.createTask(task);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
}

@PutMapping("/{id}")
public ResponseEntity<Task> updateTask(
        @PathVariable Long id,
        @Valid @RequestBody Task task) {
    // ...
}
```

### 2.3 バリデーションテストの追加

```
バリデーションのテストを追加して
```

```java
@Test
void createTask_WithEmptyTitle_ShouldReturnBadRequest() {
    Task task = new Task();
    task.setTitle("");  // 空のタイトル

    // バリデーションエラーを期待
}

@Test
void createTask_WithTooLongTitle_ShouldReturnBadRequest() {
    Task task = new Task();
    task.setTitle("a".repeat(201));  // 201文字

    // バリデーションエラーを期待
}
```

---

## Step 3: エラーハンドリングの改善

### 3.1 グローバル例外ハンドラーの作成

```
グローバル例外ハンドラーを作成して、適切なエラーレスポンスを返すようにして
```

**GlobalExceptionHandler.java**:
```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {

        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.toList());

        ErrorResponse response = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Validation Failed",
            errors
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskNotFound(
            TaskNotFoundException ex) {

        ErrorResponse response = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            null
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        // ログには詳細を記録
        log.error("Unexpected error", ex);

        // クライアントには詳細を隠す
        ErrorResponse response = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "An unexpected error occurred",
            null
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
```

### 3.2 カスタム例外の作成

```
TaskNotFoundExceptionを作成して
```

**TaskNotFoundException.java**:
```java
public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(Long id) {
        super("Task not found with id: " + id);
    }
}
```

### 3.3 エラーレスポンスDTOの作成

```
ErrorResponse DTOを作成して
```

**ErrorResponse.java**:
```java
public record ErrorResponse(
    int status,
    String message,
    List<String> errors
) {}
```

---

## Step 4: 追加のセキュリティ対策

### 4.1 XSS対策

```
XSS対策として、入力値のサニタイズを追加して
```

### 4.2 レスポンスヘッダーの追加

```
セキュリティ関連のHTTPヘッダーを追加して
```

**SecurityConfig.java**:
```java
@Configuration
public class SecurityConfig {

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new HandlerInterceptor() {
                    @Override
                    public void postHandle(HttpServletRequest request,
                            HttpServletResponse response,
                            Object handler,
                            ModelAndView modelAndView) {
                        response.setHeader("X-Content-Type-Options", "nosniff");
                        response.setHeader("X-Frame-Options", "DENY");
                        response.setHeader("X-XSS-Protection", "1; mode=block");
                    }
                });
            }
        };
    }
}
```

---

## Step 5: セキュリティ再監査

### 5.1 修正後の確認

```
セキュリティの修正が適切か再度チェックして
```

### 5.2 テストの実行

```
全てのテストを実行して
```

### 5.3 動作確認

```bash
# バリデーションエラーの確認
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":""}'

# 期待するレスポンス
# {
#   "status": 400,
#   "message": "Validation Failed",
#   "errors": ["title: タイトルは必須です"]
# }
```

---

## Step 6: コミット

```
/commit
```

コミットメッセージ例:
```
feat: セキュリティ強化

- Bean Validationによる入力バリデーション追加
- グローバル例外ハンドラーでエラー情報漏洩を防止
- カスタム例外クラスを作成
- セキュリティヘッダーを追加
```

---

## セキュリティチェックリスト

### 入力バリデーション
- [ ] 必須フィールドの検証
- [ ] 長さ制限
- [ ] 形式チェック（メール、URLなど）
- [ ] 許可リストによる値の制限

### エラーハンドリング
- [ ] スタックトレースを隠す
- [ ] 適切なHTTPステータスコード
- [ ] 一貫したエラーレスポンス形式
- [ ] ログへの詳細記録

### 認証・認可（本レッスン範囲外）
- [ ] 認証の実装
- [ ] 認可の実装
- [ ] セッション管理

### その他
- [ ] HTTPSの使用
- [ ] セキュリティヘッダー
- [ ] レート制限
- [ ] 監査ログ

---

## 実践課題

### 課題1: 追加のバリデーション

```
Taskエンティティに以下のバリデーションを追加して:
- descriptionはオプショナルだが、空文字列は許可しない
- completedのデフォルト値はfalse
```

### 課題2: 入力サニタイズ

```
HTMLタグを含む入力を無害化する処理を追加して
```

### 課題3: レート制限

```
同一IPからのリクエストを1分間に100回に制限して
```

---

## チェックポイント

このレッスンを完了したら、以下ができるようになっているはずです:

- [ ] security-check skillでセキュリティ監査ができる
- [ ] 入力バリデーションを実装できる
- [ ] 適切なエラーハンドリングを実装できる
- [ ] セキュリティベストプラクティスを理解している

---

## 次のステップ

セキュリティ強化をマスターしたら、[Lesson 06: コードレビュー](./06-code-review.md)に進みましょう。ここではcode-reviewer agentを使ったコードレビューを学びます。
