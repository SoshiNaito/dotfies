# コース4: 総合演習

これまで学んだことを活かして、新機能を追加します。

## お題: TODOに優先度フィールドを追加

TODOに「優先度（priority）」フィールドを追加します。

- HIGH（高）
- MEDIUM（中）
- LOW（低）

## フェーズ1: 計画を立てる

### 指示

```
TODOに優先度（priority）フィールドを追加したいです。
plannerを使って、実装計画を立ててください。
```

plannerが以下を検討します：
- 変更が必要なファイル
- 作業の順序
- テスト方針

## フェーズ2: 設計

### 指示

```
architectを使って、優先度フィールドの設計を行ってください。
```

architectが以下を決定します：
- Enumの定義場所
- APIの変更点
- デフォルト値

## フェーズ3: 実装

### 指示

```
implementerを使って、設計に従って優先度フィールドを実装してください。
```

implementerが以下を実装します：
- Priorityエンティティ/Enum
- Todoモデルの変更
- Controller/Serviceの変更

## フェーズ4: テスト

### 指示

```
testerを使って、優先度フィールドのテストを追加してください。
```

testerが以下のテストを追加します：
- 優先度付きTODO作成
- 優先度の更新
- デフォルト値の確認

## フェーズ5: レビュー

### 指示

```
reviewerを使って、今回の変更をレビューしてください。
```

reviewerが以下を確認します：
- コード品質
- 設計の妥当性
- テストカバレッジ

## フェーズ6: コミット

### 指示

```
/commit
```

変更をコミットして完了です。

## 完了したら

### 動作確認

```bash
# アプリを起動
mvn spring-boot:run

# 優先度付きTODOを作成
curl -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -d '{"title": "重要なタスク", "priority": "HIGH"}'
```

## まとめ

このコースで学んだこと：
- 完全なsubagentフロー（planner → architect → implementer → tester → reviewer）
- 各subagentの役割と連携方法
- 実践的な機能追加の流れ

## 全コース完了

おめでとうございます！Claude Codeハンズオンの全コースを完了しました。

### 学んだこと

| コース | 内容 |
|--------|------|
| コース1 | 基本操作（会話、ファイル指定、/コマンド） |
| コース2 | subagent活用（explorer, debugger, tester） |
| コース3 | skills実践（fix-bug, refactor, commit） |
| コース4 | 総合演習（完全なフロー） |

### 次のステップ

- dotfilesの設定をカスタマイズ
- 自分のプロジェクトでClaude Codeを活用
- skillsやsubagentを追加してワークフローを改善
