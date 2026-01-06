# Spring Boot × Claude Code ハンズオンチュートリアル

Spring BootのTask管理APIを題材に、Claude Codeの機能を段階的に学ぶハンズオンチュートリアルです。

## 対象者

- Claude Codeを使い始めたい開発者
- Spring Bootの基本を理解している方
- AIペアプログラミングに興味がある方

## 前提条件

- Java 17以上
- Git
- Claude Code CLI

※ Gradleはプロジェクトに含まれるWrapper（gradlew）を使用します

## チュートリアル構成

| Lesson | 内容 | 学ぶこと | 目安時間 |
|--------|------|----------|----------|
| [00](lessons/00-setup.md) | 環境準備 | インストール、セットアップ | 15分 |
| [01](lessons/01-basic-operations.md) | 基本操作 | コード理解、ファイル操作、/commit | 30分 |
| [02](lessons/02-feature-development.md) | 新機能追加 | /setup-feature, subagent活用 | 60分 |
| [03](lessons/03-bug-fix-tdd.md) | バグ修正 | /fix-bug, TDDワークフロー | 45分 |
| [04](lessons/04-refactoring.md) | リファクタリング | /refactor, code-reviewer | 45分 |
| [05](lessons/05-security.md) | セキュリティ | security-check skill | 60分 |
| [06](lessons/06-code-review.md) | コードレビュー | /review-pr, api-design skill | 30分 |
| [07](lessons/07-custom-commands.md) | カスタマイズ | コマンド・スキル作成 | 45分 |

**合計: 約5.5時間**

## ベースアプリについて

`base-app/task-api/` にはシンプルなTask管理REST APIが含まれています。

### エンドポイント

| Method | Path | 説明 |
|--------|------|------|
| GET | /api/tasks | 全タスク取得 |
| GET | /api/tasks/{id} | タスク取得 |
| POST | /api/tasks | タスク作成 |
| PUT | /api/tasks/{id} | タスク更新 |
| DELETE | /api/tasks/{id} | タスク削除 |

### 技術スタック

- Java 17
- Spring Boot 3.5
- Spring Data JPA
- H2 Database（インメモリ）
- Gradle

## 始め方

```bash
# 1. リポジトリをクローン
git clone <repository-url>
cd tutorial

# 2. ベースアプリを起動
cd base-app/task-api
./gradlew bootRun

# 3. 動作確認
curl http://localhost:8080/api/tasks

# 4. Lesson 00から開始
# lessons/00-setup.md を開いて進めてください
```

## 学習のコツ

1. **手を動かす**: 読むだけでなく、実際にコマンドを打ってみましょう
2. **実験する**: 「こう聞いたらどうなる？」を試してみましょう
3. **振り返る**: 各レッスン後に学んだことをまとめましょう

## 困ったときは

- Claude Codeのヘルプ: `/help`
- このリポジトリのdotfiles設定を参照: `../.claude/`

---

**Happy Coding with Claude! 🤖**
