# Lesson 02: 新機能開発

## 概要
このレッスンでは、Claude Codeのsubagent機能を活用して、新機能を効率的に開発する方法を学びます。

**所要時間**: 約60分

## 学習目標
- `/setup-feature`コマンドの使用
- feature-planner agentによる計画立案
- feature-implementer agentによるTDD実装
- `/create-pr`コマンドによるPR作成

---

## 課題: 優先度機能の追加

Taskエンティティに「優先度（priority）」フィールドを追加します。

**要件**:
- 優先度は LOW, MEDIUM, HIGH の3段階
- タスク作成時に優先度を指定可能（デフォルトはMEDIUM）
- 優先度でのフィルタリングが可能

---

## Step 1: 機能開発の準備

### 1.1 新しいブランチを作成

```
feature/task-priority というブランチを作成して
```

### 1.2 /setup-feature コマンドの実行

```
/setup-feature task-priority
```

このコマンドを実行すると:
1. feature-planner agentが起動
2. 既存コードを分析
3. 実装計画を作成

---

## Step 2: 計画の確認

### 2.1 feature-planner の出力を確認

feature-plannerは以下のような計画を提案します:

```
## 実装計画: task-priority

### 変更対象ファイル
1. Task.java - Priorityフィールド追加
2. TaskRepository.java - 優先度検索メソッド追加
3. TaskService.java - フィルタリングロジック追加
4. TaskController.java - クエリパラメータ対応

### 新規作成ファイル
1. Priority.java - Enum定義

### テストファイル
1. TaskServiceTest.java - 優先度関連テスト追加
```

### 2.2 計画の調整

必要に応じて計画を調整できます:

```
デフォルト優先度はMEDIUMではなくLOWにして
```

### 2.3 計画の承認

計画に問題がなければ承認します:

```
この計画で進めて
```

---

## Step 3: TDD実装

### 3.1 feature-implementer の起動

計画を承認すると、feature-implementer agentが自動的に起動し、TDDで実装を進めます。

### 3.2 RED Phase (テスト作成)

まずテストが作成されます:

```java
// TaskServiceTest.java に追加されるテスト例

@Test
void createTask_WithPriority_ShouldSetPriority() {
    Task task = new Task();
    task.setTitle("High Priority Task");
    task.setPriority(Priority.HIGH);

    when(taskRepository.save(any(Task.class))).thenReturn(task);

    Task result = taskService.createTask(task);

    assertEquals(Priority.HIGH, result.getPriority());
}

@Test
void createTask_WithoutPriority_ShouldDefaultToLow() {
    Task task = new Task();
    task.setTitle("Default Priority Task");

    when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
        Task savedTask = invocation.getArgument(0);
        savedTask.setId(1L);
        return savedTask;
    });

    Task result = taskService.createTask(task);

    assertEquals(Priority.LOW, result.getPriority());
}
```

この時点でテストを実行すると失敗します（RED）。

### 3.3 GREEN Phase (実装)

テストをパスさせるための最小限の実装が行われます:

```java
// Priority.java (新規作成)
public enum Priority {
    LOW, MEDIUM, HIGH
}
```

```java
// Task.java (フィールド追加)
@Enumerated(EnumType.STRING)
private Priority priority = Priority.LOW;
```

### 3.4 REFACTOR Phase

必要に応じてリファクタリングが行われます。

---

## Step 4: 実装の確認

### 4.1 変更内容の確認

```
今回の変更内容をまとめて
```

### 4.2 テストの実行

```
テストを実行して
```

全てのテストがパスすることを確認します。

### 4.3 動作確認

```bash
# アプリケーション起動
mvn spring-boot:run

# 優先度付きタスク作成
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"Urgent Task","priority":"HIGH"}'

# 優先度でフィルタリング
curl "http://localhost:8080/api/tasks?priority=HIGH"
```

---

## Step 5: コードレビューとPR作成

### 5.1 セルフレビュー

```
今回の変更内容をレビューして
```

code-reviewer agentが変更を分析し、フィードバックを提供します。

### 5.2 指摘事項の修正

レビューで指摘があれば修正します:

```
指摘事項を修正して
```

### 5.3 PRの作成

```
/create-pr
```

または:

```
この変更のPRを作成して
```

Claude Codeが:
1. 変更内容を分析
2. PR説明文を生成
3. GitHubにPRを作成

---

## 実践課題

以下の追加機能を同じ方法で実装してみましょう:

### 課題1: 期限機能

```
/setup-feature task-deadline
```

**要件**:
- Taskに期限（dueDate）フィールドを追加
- 期限切れタスクのフィルタリング機能

### 課題2: タグ機能

```
/setup-feature task-tags
```

**要件**:
- Taskに複数のタグを付けられる
- タグでのフィルタリング機能

---

## Subagent活用のポイント

### feature-planner

| 得意なこと | 注意点 |
|----------|-------|
| 既存コードの分析 | 複雑な要件は分割して依頼 |
| 変更影響範囲の特定 | 曖昧な要件は事前に明確化 |
| 段階的な計画立案 | 計画は必ず確認してから承認 |

### feature-implementer

| 得意なこと | 注意点 |
|----------|-------|
| TDDでの実装 | テストが通らない場合は確認 |
| 既存コードスタイルの踏襲 | 大きな変更は段階的に |
| エッジケースの考慮 | 生成コードは必ずレビュー |

---

## ワークフロー図

```
/setup-feature
     ↓
feature-planner (計画作成)
     ↓
計画レビュー・承認
     ↓
feature-implementer (TDD実装)
     ↓
  RED → GREEN → REFACTOR
     ↓
テスト・動作確認
     ↓
/create-pr
```

---

## チェックポイント

このレッスンを完了したら、以下ができるようになっているはずです:

- [ ] `/setup-feature`で機能開発を開始できる
- [ ] feature-plannerの計画を確認・調整できる
- [ ] feature-implementerのTDD実装を理解できる
- [ ] `/create-pr`でPRを作成できる

---

## 次のステップ

新機能開発をマスターしたら、[Lesson 03: バグ修正](./03-bug-fix-tdd.md)に進みましょう。ここではTDDワークフローを使ったバグ修正を学びます。
