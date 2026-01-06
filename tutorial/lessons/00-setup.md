# Lesson 00: 環境準備

## 概要
このレッスンでは、チュートリアルを進めるための環境を準備します。

## 前提条件

### 必要なソフトウェア

| ソフトウェア | バージョン | 確認コマンド |
|------------|----------|-------------|
| Java | 17以上 | `java -version` |
| Git | 任意 | `git --version` |
| Claude Code | 最新 | `claude --version` |

※ Gradleはプロジェクトに含まれるGradle Wrapperを使用するため、別途インストール不要です。

### Java 17のインストール

#### macOS (Homebrew)
```bash
brew install openjdk@17
```

#### Ubuntu/Debian
```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

#### Windows (winget)
```powershell
winget install Microsoft.OpenJDK.17
```

### Claude Codeのインストール

```bash
npm install -g @anthropic-ai/claude-code
```

または

```bash
brew install claude-code
```

## プロジェクトのセットアップ

### 1. リポジトリのクローン

```bash
git clone https://github.com/SoshiNaito/dotfies.git
cd dotfies/tutorial/base-app/task-api
```

### 2. 依存関係のインストール

```bash
./gradlew build
```

### 3. アプリケーションの起動確認

```bash
./gradlew bootRun
```

別のターミナルでAPIが動作していることを確認:

```bash
curl http://localhost:8080/api/tasks
```

空の配列 `[]` が返ってくれば成功です。

### 4. テストの実行

```bash
./gradlew test
```

全てのテストがパスすることを確認してください。

## Claude Codeの初期設定

### 1. Claude Codeの起動

task-apiディレクトリで:

```bash
claude
```

### 2. 初期確認

Claude Codeが起動したら、以下のコマンドを試してください:

```
/help
```

利用可能なコマンド一覧が表示されます。

### 3. プロジェクトの認識確認

```
このプロジェクトは何のプロジェクトですか？
```

Claude Codeがプロジェクト構造を理解していることを確認します。

## ベースアプリケーションの構造

```
task-api/
├── build.gradle                     # Gradle設定
├── settings.gradle                  # Gradleプロジェクト設定
├── src/
│   ├── main/
│   │   ├── java/com/example/taskapi/
│   │   │   ├── TaskApiApplication.java    # エントリーポイント
│   │   │   ├── entity/
│   │   │   │   └── Task.java              # タスクエンティティ
│   │   │   ├── repository/
│   │   │   │   └── TaskRepository.java    # データアクセス層
│   │   │   ├── service/
│   │   │   │   └── TaskService.java       # ビジネスロジック
│   │   │   └── controller/
│   │   │       └── TaskController.java    # REST API
│   │   └── resources/
│   │       └── application.properties     # 設定ファイル
│   └── test/
│       └── java/com/example/taskapi/
│           ├── TaskApiApplicationTests.java
│           └── service/
│               └── TaskServiceTest.java
```

## APIエンドポイント

| Method | Path | 説明 |
|--------|------|------|
| GET | /api/tasks | 全タスク取得 |
| GET | /api/tasks/{id} | ID指定でタスク取得 |
| POST | /api/tasks | 新規タスク作成 |
| PUT | /api/tasks/{id} | タスク更新 |
| DELETE | /api/tasks/{id} | タスク削除 |

### 動作確認

```bash
# タスク作成
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"My First Task","description":"This is a test"}'

# タスク一覧取得
curl http://localhost:8080/api/tasks

# タスク更新
curl -X PUT http://localhost:8080/api/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{"title":"Updated Task","description":"Updated description","completed":true}'

# タスク削除
curl -X DELETE http://localhost:8080/api/tasks/1
```

## トラブルシューティング

### ポート8080が使用中

```bash
# 使用中のプロセスを確認
lsof -i :8080

# または別のポートで起動
./gradlew bootRun --args='--server.port=8081'
```

### Gradleビルドエラー

```bash
# キャッシュをクリアして再ビルド
./gradlew clean build --refresh-dependencies
```

### Claude Codeが起動しない

```bash
# バージョン確認
claude --version

# 再インストール
npm install -g @anthropic-ai/claude-code@latest
```

## 次のステップ

環境が準備できたら、[Lesson 01: 基本操作](./01-basic-operations.md)に進みましょう。
