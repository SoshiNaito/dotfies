# Lesson 03: バグ修正（TDD）

## 概要
このレッスンでは、TDD（テスト駆動開発）ワークフローを使用したバグ修正の方法を学びます。

**所要時間**: 約45分

## 学習目標
- tdd-workflow skillの活用
- RED-GREEN-REFACTORサイクルの実践
- 再現テストの作成
- 回帰テストの重要性

---

## 課題: バグの発見と修正

### バグの内容

このTask APIには意図的なバグが仕込まれています:

> **完了済みのタスクを更新しようとするとエラーになる**

正しい動作: 完了済みタスクも更新できるべき（例: 完了を取り消したい場合）

---

## Step 1: バグの再現

### 1.1 バグの確認

まずバグを再現してみましょう:

```bash
# アプリケーション起動
./gradlew bootRun

# タスクを作成
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Task"}'

# タスクを完了にする
curl -X PUT http://localhost:8080/api/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Task","completed":true}'

# 完了済みタスクを更新しようとする（エラーになる）
curl -X PUT http://localhost:8080/api/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{"title":"Updated Task","completed":true}'
```

500エラーが返ってくることを確認します。

### 1.2 バグの報告

Claude Codeにバグを報告します:

```
完了済み（completed=true）のタスクを更新しようとすると
"Cannot update completed task" というエラーが発生するバグがあります。
完了済みタスクも更新できるようにしてください。
```

---

## Step 2: TDDワークフローの開始

### 2.1 tdd-workflow skillの活用

Claude Codeは自動的にtdd-workflow skillをロードし、TDDでバグ修正を進めます。

### 2.2 RED Phase - 失敗するテストを作成

まず、期待する正しい動作を表すテストを作成します:

```java
@Test
void updateTask_WhenCompleted_ShouldAllowUpdate() {
    // Arrange
    Task completedTask = new Task();
    completedTask.setId(1L);
    completedTask.setTitle("Completed Task");
    completedTask.setCompleted(true);  // 完了済み

    Task updatedDetails = new Task();
    updatedDetails.setTitle("Updated Title");
    updatedDetails.setCompleted(true);

    when(taskRepository.findById(1L)).thenReturn(Optional.of(completedTask));
    when(taskRepository.save(any(Task.class))).thenReturn(completedTask);

    // Act & Assert - 例外が発生しないことを確認
    assertDoesNotThrow(() -> taskService.updateTask(1L, updatedDetails));

    // 更新が保存されることを確認
    verify(taskRepository, times(1)).save(any(Task.class));
}
```

このテストを実行すると失敗します（RED）:

```
テストを実行して
```

### 2.3 GREEN Phase - テストをパスさせる

バグの原因を特定し、修正します:

**問題のコード（TaskService.java）**:
```java
public Task updateTask(Long id, Task taskDetails) {
    Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

    // 問題: 完了済みタスクは更新できないという不要な制限
    if (task.isCompleted()) {
        throw new RuntimeException("Cannot update completed task");
    }
    // ...
}
```

**修正後のコード**:
```java
public Task updateTask(Long id, Task taskDetails) {
    Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

    // 不要な制限を削除
    task.setTitle(taskDetails.getTitle());
    task.setDescription(taskDetails.getDescription());
    task.setCompleted(taskDetails.isCompleted());

    return taskRepository.save(task);
}
```

修正後、テストを実行:

```
テストを実行して
```

全てのテストがパスすることを確認（GREEN）。

### 2.4 REFACTOR Phase - 改善

この場合、シンプルな削除なのでリファクタリングは不要ですが、必要に応じて:

```
この修正で他に改善できる点はある？
```

---

## Step 3: 回帰テストの追加

### 3.1 エッジケースのテスト

バグ修正後も他の機能が壊れていないことを確認するテストを追加:

```
updateTaskメソッドの他のエッジケースをテストして
```

追加されるテスト例:

```java
@Test
void updateTask_FromCompletedToIncomplete_ShouldSucceed() {
    // 完了→未完了への変更
}

@Test
void updateTask_TitleOnly_ShouldPreserveOtherFields() {
    // タイトルのみ変更
}
```

### 3.2 全テストの実行

```
全てのテストを実行して結果を教えて
```

---

## Step 4: 変更のコミット

### 4.1 変更内容の確認

```
今回の変更内容をまとめて
```

### 4.2 コミット

```
/commit
```

コミットメッセージの例:
```
fix: 完了済みタスクの更新を許可

- TaskService.updateTask()の不要な制限を削除
- 完了済みタスクも更新可能に変更
- 回帰テストを追加
```

---

## 実践課題

### 課題1: バグを探す

以下の質問をしてバグを探してみましょう:

```
このプロジェクトで他にバグや問題になりそうな箇所はある？
```

### 課題2: 発見したバグを修正

発見したバグをTDDで修正してみましょう:

1. 失敗するテストを作成（RED）
2. 修正を実装（GREEN）
3. リファクタリング（REFACTOR）

### 課題3: Null安全性

```
Taskのtitleがnullの場合の動作をテストして、必要なら修正して
```

---

## TDDのポイント

### なぜテストを先に書くのか？

1. **バグの明確化**: テストで期待動作を明確にする
2. **回帰防止**: 修正後も動作を保証
3. **設計の改善**: テストしやすいコード = 良い設計
4. **ドキュメント**: テストが仕様書になる

### RED-GREEN-REFACTORサイクル

```
    RED                 GREEN               REFACTOR
     │                   │                    │
     ▼                   ▼                    ▼
失敗するテスト  →  最小限の実装  →  コードの改善
   を書く            で通す          （テストは緑）
     │                   │                    │
     └───────────────────┴────────────────────┘
                  繰り返し
```

### よくある間違い

| NG | OK |
|----|-----|
| いきなり実装 | まずテストを書く |
| 大きな修正を一度に | 小さなステップで進む |
| テストをスキップ | 全てのテストを通す |
| リファクタ中に機能追加 | 一度に一つのことだけ |

---

## tdd-workflow skill について

このskillは以下を自動的に行います:

1. バグ報告の分析
2. 再現テストの提案
3. RED-GREEN-REFACTORの案内
4. テスト実行の自動化
5. 回帰テストの提案

---

## チェックポイント

このレッスンを完了したら、以下ができるようになっているはずです:

- [ ] バグを再現するテストを作成できる
- [ ] RED-GREEN-REFACTORサイクルを実践できる
- [ ] 回帰テストの重要性を理解している
- [ ] tdd-workflow skillを活用できる

---

## 次のステップ

バグ修正をマスターしたら、[Lesson 04: リファクタリング](./04-refactoring.md)に進みましょう。ここでは肥大化したコードのリファクタリングを学びます。
