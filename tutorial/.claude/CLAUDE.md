# Task API Tutorial Project

## 概要
Claude Codeの機能を学ぶためのハンズオンチュートリアル用プロジェクトです。

## 技術スタック
- **Java**: 17
- **Framework**: Spring Boot 3.5
- **Database**: H2 (インメモリ)
- **Build**: Gradle

## プロジェクト構造

```
tutorial/
├── base-app/task-api/      # Spring Boot アプリケーション
│   ├── build.gradle        # Gradle設定
│   ├── settings.gradle     # Gradleプロジェクト設定
│   ├── src/main/java/      # メインソース
│   └── src/test/java/      # テストソース
└── lessons/                 # チュートリアルレッスン
    ├── 00-setup.md
    ├── 01-basic-operations.md
    ├── 02-feature-development.md
    ├── 03-bug-fix-tdd.md
    ├── 04-refactoring.md
    ├── 05-security.md
    ├── 06-code-review.md
    └── 07-custom-commands.md
```

## よく使うコマンド

```bash
# ビルド
./gradlew build

# テスト実行
./gradlew test

# アプリケーション起動
./gradlew bootRun

# 特定のテストクラス実行
./gradlew test --tests TaskServiceTest

# クリーンビルド
./gradlew clean build
```

## API エンドポイント

| Method | Path | 説明 |
|--------|------|------|
| GET | /api/tasks | 全タスク取得 |
| GET | /api/tasks/{id} | タスク取得 |
| POST | /api/tasks | タスク作成 |
| PUT | /api/tasks/{id} | タスク更新 |
| DELETE | /api/tasks/{id} | タスク削除 |

## 意図的な問題点（レッスン用）

このプロジェクトには学習目的で意図的に以下の問題が含まれています:

1. **Lesson 03 用**: `TaskService.updateTask()` - 完了済みタスクが更新できないバグ
2. **Lesson 04 用**: `TaskService.searchTasks()` - 肥大化したメソッド
3. **Lesson 05 用**: バリデーションなし、エラーハンドリング不足

## コーディング規約

### レイヤー構造
```
Controller → Service → Repository
```

### 命名規則
- コントローラー: `XxxController.java`
- サービス: `XxxService.java`
- リポジトリ: `XxxRepository.java`
- エンティティ: `Xxx.java`

### テスト
- 単体テスト: `XxxTest.java` (Mockito使用)
- 統合テスト: `XxxIntegrationTest.java`

## チュートリアル進行のヒント

1. **Lesson 01-03** は順番に進めてください
2. **Lesson 04-06** は独立して実施可能
3. **Lesson 07** は最後に実施することを推奨

各レッスンでは:
1. まず課題を読む
2. Claude Codeに指示を出す
3. 結果を確認
4. 応用課題に挑戦
