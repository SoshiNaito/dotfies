# Lesson 04: リファクタリング

## 概要
このレッスンでは、肥大化したコードをリファクタリングする方法を学びます。code-reviewer agentを活用して、安全にリファクタリングを進めます。

**所要時間**: 約45分

## 学習目標
- コードの問題点を分析できる
- 段階的なリファクタリングの進め方
- code-reviewer agentによるレビュー
- テストによる動作保証

---

## 課題: TaskServiceのリファクタリング

### 問題のあるコード

`TaskService.java`の`searchTasks`メソッドには以下の問題があります:

1. 複数の責務を持っている（検索 + フィルタリング + ソート）
2. switch文が拡張しにくい
3. 条件分岐が複雑

```java
public List<Task> searchTasks(String keyword, Boolean completed, String sortBy) {
    List<Task> tasks;

    if (keyword != null && !keyword.isEmpty()) {
        tasks = taskRepository.findByTitleContaining(keyword);
    } else if (completed != null) {
        tasks = taskRepository.findByCompleted(completed);
    } else {
        tasks = taskRepository.findAll();
    }

    // ソート処理
    if (sortBy != null) {
        switch (sortBy) {
            case "title":
                tasks.sort((a, b) -> a.getTitle().compareTo(b.getTitle()));
                break;
            case "createdAt":
                tasks.sort((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));
                break;
            case "completed":
                tasks.sort((a, b) -> Boolean.compare(a.isCompleted(), b.isCompleted()));
                break;
        }
    }

    return tasks;
}
```

---

## Step 1: 現状分析

### 1.1 問題点の把握

```
TaskServiceのsearchTasksメソッドを分析して、問題点を教えて
```

Claude Codeが以下のような問題を指摘します:

- **単一責任原則違反**: 検索とソートが混在
- **開放閉鎖原則違反**: 新しいソート条件の追加にはコード変更が必要
- **条件分岐の複雑さ**: keyword優先でcompletedが無視される

### 1.2 テストカバレッジの確認

リファクタリング前に、既存のテストを確認:

```
searchTasksメソッドのテストカバレッジを確認して
```

---

## Step 2: リファクタリング計画

### 2.1 リファクタリング方針の策定

```
searchTasksメソッドをリファクタリングする計画を立てて
```

提案される計画例:

1. **ソート処理の分離**: TaskSorterクラスを作成
2. **検索条件の改善**: 複数条件のAND検索対応
3. **Strategyパターンの適用**: ソート戦略を切り替え可能に

### 2.2 計画の確認

```
この計画で進めて。ただし、まずテストを書いてから実装して
```

---

## Step 3: テストの作成

### 3.1 リファクタリング前のテスト追加

既存動作を保証するテストを追加:

```java
@Test
void searchTasks_WithKeywordAndCompleted_ShouldApplyBothFilters() {
    // キーワードとcompletedの両方を指定した場合のテスト
}

@Test
void searchTasks_WithSort_ShouldReturnSortedList() {
    // ソート機能のテスト
}

@Test
void searchTasks_WithInvalidSortBy_ShouldReturnUnsortedList() {
    // 無効なソート条件のテスト
}
```

### 3.2 テストの実行

```
テストを実行して、現状の動作を確認して
```

---

## Step 4: 段階的リファクタリング

### 4.1 Phase 1: ソート処理の抽出

```
まずソート処理を別メソッドに抽出して
```

**Before**:
```java
public List<Task> searchTasks(String keyword, Boolean completed, String sortBy) {
    List<Task> tasks = // 検索処理

    // インラインのソート処理
    if (sortBy != null) {
        switch (sortBy) { ... }
    }
    return tasks;
}
```

**After**:
```java
public List<Task> searchTasks(String keyword, Boolean completed, String sortBy) {
    List<Task> tasks = // 検索処理
    return sortTasks(tasks, sortBy);
}

private List<Task> sortTasks(List<Task> tasks, String sortBy) {
    if (sortBy == null) return tasks;
    // ソート処理
}
```

テスト実行:
```
テストを実行して
```

### 4.2 Phase 2: TaskSorterクラスの作成

```
ソート処理をTaskSorterクラスに移動して
```

```java
@Component
public class TaskSorter {

    public List<Task> sort(List<Task> tasks, String sortBy) {
        if (sortBy == null) return tasks;

        Comparator<Task> comparator = getComparator(sortBy);
        if (comparator != null) {
            return tasks.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
        }
        return tasks;
    }

    private Comparator<Task> getComparator(String sortBy) {
        return switch (sortBy) {
            case "title" -> Comparator.comparing(Task::getTitle);
            case "createdAt" -> Comparator.comparing(Task::getCreatedAt);
            case "completed" -> Comparator.comparing(Task::isCompleted);
            default -> null;
        };
    }
}
```

### 4.3 Phase 3: 検索条件の改善

```
検索条件を改善して、keywordとcompletedの両方を同時に適用できるようにして
```

---

## Step 5: コードレビュー

### 5.1 code-reviewer agentによるレビュー

```
リファクタリング結果をレビューして
```

code-reviewer agentが以下をチェックします:

- 既存機能の維持
- コード品質の向上
- テストカバレッジ
- パフォーマンスへの影響

### 5.2 レビュー結果への対応

指摘があれば修正:

```
指摘事項を修正して
```

---

## Step 6: 最終確認とコミット

### 6.1 全テストの実行

```
全てのテストを実行して
```

### 6.2 変更のコミット

```
/commit
```

コミットメッセージ例:
```
refactor: TaskServiceのsearchTasksメソッドをリファクタリング

- TaskSorterクラスを作成してソート処理を分離
- 複数条件の同時適用を可能に
- switch式を使用して可読性を向上
```

---

## リファクタリングパターン集

### Extract Method（メソッド抽出）
```
〜の処理を別メソッドに抽出して
```

### Extract Class（クラス抽出）
```
〜の責務を別クラスに分離して
```

### Replace Conditional with Polymorphism
```
switch文をポリモーフィズムを使って置き換えて
```

### Introduce Parameter Object
```
複数のパラメータをオブジェクトにまとめて
```

---

## 実践課題

### 課題1: TaskControllerのリファクタリング

```
TaskControllerのgetAllTasksメソッドを分析して、
リファクタリングすべき点があれば実施して
```

### 課題2: 例外処理の改善

```
RuntimeExceptionを適切なカスタム例外に置き換えて
```

### 課題3: Builderパターンの導入

```
Taskエンティティの生成にBuilderパターンを導入して
```

---

## リファクタリングのベストプラクティス

### 安全なリファクタリングの手順

1. **テストの確認**: 十分なテストカバレッジを確保
2. **小さなステップ**: 一度に一つの変更
3. **頻繁なテスト**: 各ステップ後にテスト実行
4. **コミット**: 安定した状態で細かくコミット

### やってはいけないこと

| NG | 理由 |
|----|------|
| テストなしでリファクタ | 動作保証ができない |
| 大きな変更を一度に | 問題の特定が困難 |
| 機能追加と同時に | 変更理由が不明確 |
| レビューなしでマージ | 品質低下 |

---

## チェックポイント

このレッスンを完了したら、以下ができるようになっているはずです:

- [ ] コードの問題点を分析できる
- [ ] 段階的にリファクタリングを進められる
- [ ] テストで動作を保証しながら変更できる
- [ ] code-reviewer agentを活用できる

---

## 次のステップ

リファクタリングをマスターしたら、[Lesson 05: セキュリティ](./05-security.md)に進みましょう。ここではセキュリティ強化について学びます。
