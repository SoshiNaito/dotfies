# Java / Spring Boot チェック項目

Java および Spring Boot プロジェクト固有のコードレビュー観点。

---

## Java言語

### 命名規則
- [ ] クラス名: PascalCase
- [ ] メソッド名/変数名: camelCase
- [ ] 定数: UPPER_SNAKE_CASE
- [ ] パッケージ名: すべて小文字

### モダンJava（Java 8+）
- [ ] Stream APIの適切な使用
- [ ] `Optional` の適切な使用（フィールドには使わない）
- [ ] ラムダ式の適切な使用
- [ ] メソッド参照の活用
- [ ] `var` の適切な使用（型が明確な場合のみ）

### イミュータビリティ
- [ ] 可能な限りフィールドを `final` にしている
- [ ] 不変オブジェクトを優先している
- [ ] コレクションを返す際に防御的コピーまたは不変コレクションを使用

### equals/hashCode/toString
- [ ] `equals()` と `hashCode()` が一貫している
- [ ] `toString()` が有用な情報を返す
- [ ] Lombokの `@Data`, `@EqualsAndHashCode` を適切に使用

### 例外処理
- [ ] 汎用的な `Exception` のキャッチを避けている
- [ ] 例外を握りつぶしていない（空のcatchブロック）
- [ ] 適切な例外の再スローまたはラップ
- [ ] カスタム例外が適切に定義されている
- [ ] try-with-resourcesでリソース解放

### パフォーマンス
- [ ] 文字列結合に `StringBuilder` を使用（ループ内）
- [ ] 適切なデータ構造を使用（List vs Set vs Map）
- [ ] 不要なオブジェクト生成がない
- [ ] ストリームAPIの適切な使用（並列処理の判断）

---

## Spring Boot

### レイヤーアーキテクチャ

#### Controller層
- [ ] `@RestController` / `@Controller` の適切な使用
- [ ] HTTPメソッドに適したマッピング（`@GetMapping`, `@PostMapping` など）
- [ ] 適切なHTTPステータスコードを返している
- [ ] リクエスト/レスポンスDTOを使用（エンティティを直接公開しない）
- [ ] `@Valid` / `@Validated` でリクエストバリデーション

#### Service層
- [ ] `@Service` アノテーションの使用
- [ ] ビジネスロジックがService層に集約されている
- [ ] Controller層やRepository層のロジックが混在していない
- [ ] トランザクション境界が適切

#### Repository層
- [ ] `@Repository` アノテーションの使用（Spring Data JPA使用時は不要）
- [ ] カスタムクエリが適切に定義されている
- [ ] N+1問題の回避（`@EntityGraph`, `JOIN FETCH` など）

### 依存性注入（DI）

- [ ] コンストラクタインジェクションを使用（`@Autowired` フィールドインジェクションを避ける）
- [ ] `final` フィールドと組み合わせて使用
- [ ] 循環依存がない
- [ ] 適切なスコープ（`@Scope`）を使用

```java
// 推奨: コンストラクタインジェクション
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
}

// 非推奨: フィールドインジェクション
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
}
```

### トランザクション管理

- [ ] `@Transactional` の適切な配置（Service層）
- [ ] 読み取り専用操作には `@Transactional(readOnly = true)`
- [ ] 適切な伝播レベル（`propagation`）の指定
- [ ] 適切なロールバック設定
- [ ] トランザクション内で外部API呼び出しを避けている

```java
@Transactional(readOnly = true)
public User findById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));
}

@Transactional
public User save(UserCreateRequest request) {
    // 更新処理
}
```

### JPA / Hibernate

#### エンティティ設計
- [ ] `@Entity` クラスに適切なアノテーション
- [ ] 主キー生成戦略が適切（`@GeneratedValue`）
- [ ] 適切なフェッチ戦略（LAZY vs EAGER）
- [ ] 関連マッピングが適切（`@OneToMany`, `@ManyToOne` など）
- [ ] カスケード設定が適切

#### パフォーマンス
- [ ] N+1問題の回避
- [ ] 必要なフィールドのみ取得（プロジェクション、DTO）
- [ ] バッチサイズの設定（`@BatchSize`）
- [ ] 適切なインデックスの存在

```java
// N+1問題の回避例
@Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.id = :id")
Optional<User> findByIdWithRoles(@Param("id") Long id);

// プロジェクション例
@Query("SELECT new com.example.dto.UserSummary(u.id, u.name) FROM User u")
List<UserSummary> findAllSummaries();
```

### バリデーション

- [ ] Bean Validation（`@NotNull`, `@Size`, `@Email` など）の使用
- [ ] カスタムバリデーターの適切な実装
- [ ] バリデーションエラーの適切なハンドリング
- [ ] グループバリデーションの活用（必要に応じて）

```java
public class UserCreateRequest {
    @NotBlank(message = "名前は必須です")
    @Size(max = 100, message = "名前は100文字以内で入力してください")
    private String name;

    @NotBlank(message = "メールアドレスは必須です")
    @Email(message = "有効なメールアドレスを入力してください")
    private String email;

    @NotNull(message = "年齢は必須です")
    @Min(value = 0, message = "年齢は0以上で入力してください")
    private Integer age;
}
```

### 例外ハンドリング

- [ ] `@ControllerAdvice` / `@RestControllerAdvice` で統一的なエラーハンドリング
- [ ] `@ExceptionHandler` で適切な例外処理
- [ ] 一貫したエラーレスポンス形式
- [ ] 適切なHTTPステータスコード

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(UserNotFoundException ex) {
        return new ErrorResponse("USER_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationError(MethodArgumentNotValidException ex) {
        // バリデーションエラーの処理
    }
}
```

### セキュリティ（Spring Security）

- [ ] 適切な認証設定
- [ ] エンドポイントごとの認可設定（`@PreAuthorize`, `@Secured`）
- [ ] CSRF対策（REST APIでは無効化を検討）
- [ ] CORS設定
- [ ] パスワードのハッシュ化（`PasswordEncoder`）
- [ ] セキュリティヘッダーの設定

```java
@PreAuthorize("hasRole('ADMIN')")
@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    userService.delete(id);
    return ResponseEntity.noContent().build();
}
```

### 設定管理

- [ ] `application.yml` / `application.properties` の適切な使用
- [ ] 環境別設定の分離（プロファイル）
- [ ] `@ConfigurationProperties` での型安全な設定
- [ ] 機密情報の外部化（環境変数、Secrets Manager など）

```java
@Configuration
@ConfigurationProperties(prefix = "app.mail")
@Validated
public class MailProperties {
    @NotBlank
    private String host;

    @Min(1) @Max(65535)
    private int port;

    // getter/setter
}
```

### ログ

- [ ] SLF4Jの使用（`@Slf4j` Lombokアノテーション推奨）
- [ ] 適切なログレベルの使用（ERROR, WARN, INFO, DEBUG, TRACE）
- [ ] 機密情報をログに出力していない
- [ ] 例外スタックトレースの適切なログ出力
- [ ] パフォーマンスを考慮したログ（`log.debug("msg: {}", value)`）

```java
@Slf4j
@Service
public class UserService {

    public User findById(Long id) {
        log.debug("Finding user by id: {}", id);
        return userRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("User not found: {}", id);
                return new UserNotFoundException(id);
            });
    }
}
```

### テスト

#### テストの種類と使い分け
- [ ] `@SpringBootTest` - 統合テスト（必要な場合のみ）
- [ ] `@WebMvcTest` - Controller層のテスト
- [ ] `@DataJpaTest` - Repository層のテスト
- [ ] `@MockBean` / `@SpyBean` の適切な使用
- [ ] 単体テストはSpringコンテキストなしで実行

#### テスト品質
- [ ] 適切なテストカバレッジ
- [ ] エッジケースのテスト
- [ ] テストデータの適切な準備（`@BeforeEach`, TestContainers など）
- [ ] テストの独立性（他のテストに依存しない）

```java
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void shouldReturnUser() throws Exception {
        given(userService.findById(1L))
            .willReturn(new User(1L, "test@example.com"));

        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("test@example.com"));
    }
}
```

---

## クイックリファレンス

### よく使うLombokアノテーション

| アノテーション | 用途 |
|--------------|------|
| `@Data` | getter/setter/equals/hashCode/toString |
| `@Getter` / `@Setter` | getter/setter のみ |
| `@NoArgsConstructor` | 引数なしコンストラクタ |
| `@AllArgsConstructor` | 全引数コンストラクタ |
| `@RequiredArgsConstructor` | finalフィールドのコンストラクタ（DI用） |
| `@Builder` | Builderパターン |
| `@Slf4j` | ロガーフィールド |

### Spring Boot テストアノテーション

| アノテーション | 用途 | ロードするBean |
|--------------|------|--------------|
| `@SpringBootTest` | 統合テスト | 全Bean |
| `@WebMvcTest` | Controllerテスト | Web層のみ |
| `@DataJpaTest` | Repositoryテスト | JPA関連のみ |
| `@JsonTest` | JSON変換テスト | Jackson関連のみ |