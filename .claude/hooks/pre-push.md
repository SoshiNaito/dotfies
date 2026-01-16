# Pre-Push Hook

プッシュ前に自動実行される包括的チェック。

## 実行内容
1. 全テストの実行
2. セキュリティチェック
3. ドキュメントの整合性確認

## 失敗時の挙動
- エラーがあればプッシュを中断
- 問題の詳細を報告
- 必要に応じてsubagent（debugger, tester）への委譲を提案

## 呼び出すskills
- `run-tdd`
- `check-security`