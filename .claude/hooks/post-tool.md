# Post-Tool Hook

ツール実行後に自動実行される後処理。

## 実行内容
1. 作業コンテキストの更新（manage-memory）
2. 変更ファイルの追跡
3. 次のsubagentへの引き継ぎ情報の整理

## 呼び出すskills
- `manage-memory`