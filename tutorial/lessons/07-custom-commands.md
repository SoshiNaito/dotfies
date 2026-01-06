# Lesson 07: カスタムコマンド・スキル作成

## 概要
このレッスンでは、プロジェクト固有のカスタムコマンドとスキルを作成する方法を学びます。

**所要時間**: 約45分

## 学習目標
- カスタムコマンドの作成
- カスタムスキルの作成
- プロジェクト固有の自動化
- チームでの共有方法

---

## カスタムコマンドとは

カスタムコマンドは、よく使う操作を簡単に実行できるようにするショートカットです。

例:
- `/run-tests` - テストを実行
- `/deploy-staging` - ステージング環境にデプロイ
- `/db-migrate` - データベースマイグレーション実行

---

## Step 1: カスタムコマンドの作成

### 1.1 コマンドファイルの作成

`/run-tests`コマンドを作成します:

```
.claude/commands/run-tests.md を作成して、
Mavenでテストを実行するコマンドにして
```

**.claude/commands/run-tests.md**:
```markdown
---
description: Mavenでテストを実行する
---

# Run Tests

以下の手順でテストを実行してください:

1. プロジェクトルートディレクトリで実行
2. `mvn test` コマンドを使用
3. テスト結果のサマリーを報告

## オプション

- 特定のテストクラスを指定された場合: `mvn test -Dtest=クラス名`
- 特定のメソッドを指定された場合: `mvn test -Dtest=クラス名#メソッド名`

## 出力形式

```
## テスト結果

- 実行: X件
- 成功: X件
- 失敗: X件
- スキップ: X件

### 失敗したテスト（あれば）
- テスト名: 失敗理由
```
```

### 1.2 コマンドの実行

```
/run-tests
```

### 1.3 引数付きコマンド

```
/run-tests TaskServiceTest
```

---

## Step 2: より高度なコマンド

### 2.1 デプロイコマンド

```
.claude/commands/deploy-local.md を作成して
```

**.claude/commands/deploy-local.md**:
```markdown
---
description: ローカル環境でアプリケーションを起動
---

# Deploy Local

ローカル環境でSpring Bootアプリケーションを起動します。

## 手順

1. 既存のプロセスがあれば停止
2. クリーンビルド実行
3. アプリケーション起動
4. ヘルスチェック

## コマンド

```bash
# ビルド
mvn clean package -DskipTests

# 起動（バックグラウンド）
java -jar target/*.jar &

# ヘルスチェック（起動完了まで待機）
until curl -s http://localhost:8080/api/tasks > /dev/null; do
  sleep 1
done
echo "Application started successfully"
```
```

### 2.2 DB操作コマンド

**.claude/commands/db-console.md**:
```markdown
---
description: H2データベースコンソールを開く
---

# DB Console

H2データベースコンソールへのアクセス方法を案内します。

## 手順

1. アプリケーションが起動していることを確認
2. ブラウザで http://localhost:8080/h2-console を開く
3. 接続情報を入力:
   - JDBC URL: jdbc:h2:mem:taskdb
   - User: sa
   - Password: (空)
```

---

## Step 3: カスタムスキルの作成

### 3.1 スキルとコマンドの違い

| | コマンド | スキル |
|--|---------|-------|
| 起動方法 | ユーザーが明示的に実行 | 文脈に応じて自動ロード |
| 用途 | 特定のタスク実行 | ガイドライン・知識の提供 |
| 例 | /run-tests, /deploy | api-design, security-check |

### 3.2 Spring Boot固有のスキル

```
.claude/skills/spring-boot.md を作成して
```

**.claude/skills/spring-boot.md**:
```markdown
---
description: Spring Boot開発時のベストプラクティスを提供
triggers:
  - "@Controller"
  - "@Service"
  - "@Repository"
  - "spring-boot"
---

# Spring Boot Best Practices

## レイヤー構造

```
Controller → Service → Repository
   (API)     (ロジック)   (データ)
```

## アノテーションガイド

### Controller層
- `@RestController`: RESTful API用
- `@RequestMapping`: ベースパス指定
- `@GetMapping`, `@PostMapping`, etc.

### Service層
- `@Service`: ビジネスロジック
- `@Transactional`: トランザクション管理

### Repository層
- `@Repository`: データアクセス
- `extends JpaRepository<Entity, ID>`

## 命名規則

- Controller: `XxxController.java`
- Service: `XxxService.java`
- Repository: `XxxRepository.java`
- Entity: `Xxx.java`

## レスポンス形式

- 成功: 適切なHTTPステータス + データ
- エラー: 適切なHTTPステータス + エラーメッセージ

```java
// 推奨パターン
@GetMapping("/{id}")
public ResponseEntity<Task> getById(@PathVariable Long id) {
    return taskService.findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
}
```

## テストパターン

- 単体テスト: Mockitoでモック
- 統合テスト: @SpringBootTest
- スライステスト: @WebMvcTest, @DataJpaTest
```

### 3.3 API設計スキル

**.claude/skills/rest-api-design.md**:
```markdown
---
description: REST API設計のガイドライン
triggers:
  - "API"
  - "endpoint"
  - "@RequestMapping"
---

# REST API Design Guidelines

## URL設計

- リソースは名詞（複数形）: `/tasks`, `/users`
- 階層関係: `/users/{id}/tasks`
- アクションは避ける: ❌ `/getTasks` → ✅ `/tasks`

## HTTPメソッド

| メソッド | 用途 | べき等性 |
|---------|------|---------|
| GET | 取得 | Yes |
| POST | 作成 | No |
| PUT | 更新（全体） | Yes |
| PATCH | 更新（部分） | Yes |
| DELETE | 削除 | Yes |

## ステータスコード

| コード | 用途 |
|--------|------|
| 200 | 成功（データあり） |
| 201 | 作成成功 |
| 204 | 成功（データなし） |
| 400 | リクエスト不正 |
| 401 | 認証必要 |
| 403 | 権限なし |
| 404 | リソースなし |
| 500 | サーバーエラー |

## ページネーション

```
GET /tasks?page=0&size=20&sort=createdAt,desc
```

レスポンス:
```json
{
  "content": [...],
  "page": 0,
  "size": 20,
  "totalElements": 100,
  "totalPages": 5
}
```
```

---

## Step 4: 設定の共有

### 4.1 チームでの共有

```
.claude/配下のファイルをGitで共有することで、
チーム全員が同じコマンド・スキルを使えます
```

### 4.2 プロジェクト固有のCLAUDE.md

```
.claude/CLAUDE.md を作成して
```

**.claude/CLAUDE.md**:
```markdown
# Task API Project

## プロジェクト概要
タスク管理REST API（Spring Boot）

## 技術スタック
- Java 17
- Spring Boot 3.2
- H2 Database
- Maven

## よく使うコマンド
- /run-tests - テスト実行
- /deploy-local - ローカル起動
- /db-console - DBコンソール案内

## コーディング規約
- コントローラーは薄く（ロジックはServiceに）
- 例外はGlobalExceptionHandlerで処理
- テストはMockitoでモック

## ディレクトリ構造
```
src/main/java/com/example/taskapi/
├── controller/    # REST API
├── service/       # ビジネスロジック
├── repository/    # データアクセス
├── entity/        # エンティティ
└── exception/     # カスタム例外
```
```

---

## 実践課題

### 課題1: ログ確認コマンド

```
/show-logs というコマンドを作成して、
アプリケーションログの最新20行を表示するようにして
```

### 課題2: コード生成コマンド

```
/gen-crud {EntityName} というコマンドを作成して、
指定したエンティティのCRUD一式を生成するようにして
```

### 課題3: ドキュメント生成スキル

```
javadoc を書く時のベストプラクティスを提供するスキルを作成して
```

---

## ファイル構成まとめ

```
.claude/
├── CLAUDE.md                    # プロジェクト固有の説明
├── commands/
│   ├── run-tests.md             # /run-tests
│   ├── deploy-local.md          # /deploy-local
│   └── db-console.md            # /db-console
├── skills/
│   ├── spring-boot.md           # Spring Bootガイド
│   └── rest-api-design.md       # API設計ガイド
└── agents/
    ├── code-reviewer.md         # レビュー観点
    └── feature-planner.md       # 計画作成ガイド
```

---

## チェックポイント

このレッスンを完了したら、以下ができるようになっているはずです:

- [ ] カスタムコマンドを作成できる
- [ ] カスタムスキルを作成できる
- [ ] プロジェクト固有のCLAUDE.mdを作成できる
- [ ] チームで設定を共有できる

---

## チュートリアル完了

おめでとうございます！全7レッスンを完了しました。

### 学んだこと

1. **Lesson 01**: 基本操作、コード理解、/commit
2. **Lesson 02**: /setup-feature、feature-planner/implementer
3. **Lesson 03**: TDDワークフロー、バグ修正
4. **Lesson 04**: リファクタリング、code-reviewer
5. **Lesson 05**: security-check、セキュリティ強化
6. **Lesson 06**: PRレビュー、レビューコメント
7. **Lesson 07**: カスタムコマンド・スキル作成

### 次のステップ

- 実際のプロジェクトでClaude Codeを活用
- チーム固有のコマンド・スキルを拡充
- より複雑な自動化ワークフローの構築

### 参考リソース

- [Claude Code 公式ドキュメント](https://docs.anthropic.com/claude-code)
- [dotfilesリポジトリの.claude/sample/](../.claude/sample/)
