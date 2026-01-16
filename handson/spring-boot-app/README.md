# TODO API

Claude Codeハンズオン用のシンプルなTODOアプリです。

## 技術スタック

- Java 17
- Spring Boot 3.5
- Spring Data JPA
- H2 Database（インメモリ）

## セットアップ

初回のみ Gradle Wrapper を生成してください。

```bash
gradle wrapper --gradle-version 8.12
```

## 起動方法

```bash
./gradlew bootRun
```

http://localhost:8080 でアプリが起動します。

## API エンドポイント

| メソッド | パス | 説明 |
|---------|------|------|
| GET | /api/todos | TODO一覧取得 |
| GET | /api/todos/{id} | TODO取得 |
| POST | /api/todos | TODO作成 |
| PUT | /api/todos/{id} | TODO更新 |
| DELETE | /api/todos/{id} | TODO削除 |

## 使用例

### TODO作成

```bash
curl -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -d '{"title": "牛乳を買う"}'
```

### TODO一覧取得

```bash
curl http://localhost:8080/api/todos
```

### TODO更新（完了にする）

```bash
curl -X PUT http://localhost:8080/api/todos/1 \
  -H "Content-Type: application/json" \
  -d '{"title": "牛乳を買う", "completed": true}'
```

### TODO削除

```bash
curl -X DELETE http://localhost:8080/api/todos/1
```

## テスト実行

```bash
./gradlew test
```

## H2 Console

開発中はH2 Consoleでデータベースを確認できます。

- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:tododb`
- User: `sa`
- Password: （空）
