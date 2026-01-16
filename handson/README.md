# Claude Code ハンズオン

Claude Codeを初めて触る方向けのハンズオン教材です。

## 前提条件

- Java 17以上
- Gradle 8.12以上（または Gradle Wrapper を使用）
- Claude Codeがインストール済み（`claude` コマンドが使える状態）

## セットアップ

```bash
# リポジトリをクローン
git clone <this-repo>
cd handson

# 作業用ブランチを作成（自分の名前-handson）
git checkout -b <your-name>-handson

# Spring Bootアプリをビルド
cd spring-boot-app
gradle wrapper --gradle-version 8.12  # 初回のみ
./gradlew build
```

## コース一覧

| コース | 内容 | 目安 |
|--------|------|------|
| [コース1](./course-1/README.md) | 基本操作編 | 15分 |
| [コース2](./course-2/README.md) | subagent活用編 | 30分 |
| [コース3](./course-3/README.md) | skills実践編 | 20分 |
| [コース4](./course-4/README.md) | 総合演習 | 30分 |

## 学習の流れ

```
コース1 → コース2 → コース3 → コース4
```

順番に進めることで、段階的にClaude Codeの使い方を習得できます。

### コース1: 基本操作編
- Claude Codeの起動
- 会話でコードを理解する
- ファイル指定（@ファイル名）
- /コマンドの使い方

### コース2: subagent活用編
- explorerでコードベース調査
- debuggerでバグ修正
- testerでテスト追加

### コース3: skills実践編
- /fix-bug でバグ修正
- /refactor でコード改善
- /commit でコミット

### コース4: 総合演習
- planner → architect → implementer → tester → reviewer の完全フロー
- 新機能（優先度フィールド）の追加

## サンプルアプリについて

`spring-boot-app/` には、ハンズオン用のシンプルなTODOアプリが含まれています。

- Spring Boot 3.5
- H2インメモリDB
- REST API（CRUD）

詳細は [spring-boot-app/README.md](./spring-boot-app/README.md) を参照してください。

## 注意事項

- 演習用にバグが意図的に仕込まれています
- 各コースは前のコースの完了を前提としています

## トラブルシューティング

### Gradleビルドが失敗する

```bash
# Javaバージョンを確認
java -version  # 17以上が必要

# Gradle Wrapperを使用している場合は再生成
gradle wrapper --gradle-version 8.12
```

### Claude Codeが起動しない

```bash
# インストール確認
claude --version
```
