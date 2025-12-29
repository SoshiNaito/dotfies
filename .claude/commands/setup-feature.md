新機能実装ワークフローをセットアップします。

手順:
1. mainから機能ブランチを作成
2. feature-plannerサブエージェントで実装計画を作成
3. ユーザーの承認を待つ
4. feature-implementerサブエージェントでTDDにより実装
5. code-reviewerサブエージェントでレビュー
6. プルリクエストを作成

機能名: $ARGUMENTS

実行:
```bash
git checkout -b feature/$ARGUMENTS
```

それでは、feature-plannerサブエージェントを使用して要件を分析し、詳細な実装計画を作成してください。