#!/bin/bash

# Claude Code設定をグローバル設定にインストールするスクリプト
# - テキストファイル: マーカー付き追記（重複防止）
# - JSONファイル: マージ
# - ディレクトリ: コピー

DOTFILES_DIR="$(cd "$(dirname "$0")" && pwd)"
DOTFILES_CLAUDE="$DOTFILES_DIR/.claude"
TARGET_DIR="$HOME/.claude"
MARKER_START="# === DOTFILES CONFIG START ==="
MARKER_END="# === DOTFILES CONFIG END ==="

# ~/.claude ディレクトリがなければ作成
mkdir -p "$TARGET_DIR"

echo "=== Claude Code設定をインストール ==="
echo ""

# --------------------------------------------
# テキストファイルをマーカー付きで追記する関数
# --------------------------------------------
append_with_marker() {
    local src="$1"
    local dest="$2"
    local name="$3"

    # ターゲットファイルが存在しない場合は作成
    if [ ! -f "$dest" ]; then
        touch "$dest"
    fi

    # 既存のマーカー間の内容を抽出して比較
    if grep -q "$MARKER_START" "$dest" 2>/dev/null; then
        # マーカー間の内容を抽出（Source/Updated行を除く）
        local existing_content=$(sed -n "/$MARKER_START/,/$MARKER_END/p" "$dest" | grep -v "^# ===" | grep -v "^# Source:" | grep -v "^# Updated:" | sed '/^$/d')
        local new_content=$(cat "$src" | sed '/^$/d')

        if [ "$existing_content" = "$new_content" ]; then
            echo "  スキップ: 内容が同じです"
            return
        fi

        # 内容が異なる場合は既存のdotfiles部分を削除
        echo "  既存のdotfiles設定を更新中..."
        if [[ "$OSTYPE" == "darwin"* ]]; then
            sed -i '' "/$MARKER_START/,/$MARKER_END/d" "$dest"
        else
            sed -i "/$MARKER_START/,/$MARKER_END/d" "$dest"
        fi
    fi

    # 末尾の空行を整理
    if [ -s "$dest" ]; then
        echo "" >> "$dest"
    fi

    # dotfilesの内容をマーカー付きで追記
    {
        echo "$MARKER_START"
        echo "# Source: $src"
        echo "# Updated: $(date '+%Y-%m-%d %H:%M:%S')"
        echo ""
        cat "$src"
        echo ""
        echo "$MARKER_END"
    } >> "$dest"

    echo "  完了: $dest"
}

# --------------------------------------------
# JSONファイルをマージする関数
# --------------------------------------------
merge_json() {
    local src="$1"
    local dest="$2"
    local name="$3"

    if [ ! -f "$dest" ]; then
        # 存在しない場合はコピー
        cp "$src" "$dest"
        echo "  コピー: $dest"
    else
        # 存在する場合はマージ（jqが必要）
        if command -v jq &> /dev/null; then
            # マージ結果と現在の内容を比較
            local merged=$(jq -s '.[0] * .[1]' "$dest" "$src")
            local current=$(cat "$dest")

            # 正規化して比較（整形して比較）
            local merged_normalized=$(echo "$merged" | jq -S '.')
            local current_normalized=$(echo "$current" | jq -S '.')

            if [ "$merged_normalized" = "$current_normalized" ]; then
                echo "  スキップ: 内容が同じです"
                return
            fi

            local tmp=$(mktemp)
            echo "$merged" > "$tmp" && mv "$tmp" "$dest"
            echo "  マージ: $dest"
        else
            echo "  警告: jqがインストールされていません。$name をスキップします。"
            echo "        brew install jq でインストール後、再実行してください。"
        fi
    fi
}

# --------------------------------------------
# ディレクトリをコピーする関数
# （同名ファイルがある場合はスキップして警告）
# --------------------------------------------
CONFLICTS=()

copy_directory() {
    local src="$1"
    local dest="$2"
    local name="$3"

    if [ -d "$src" ]; then
        # ディレクトリがなければ作成
        mkdir -p "$dest"

        # ファイルを1つずつチェックしてコピー
        for file in "$src"/*; do
            local filename=$(basename "$file")
            local dest_file="$dest/$filename"

            if [ -e "$dest_file" ]; then
                # 同名ファイルが存在する場合はスキップ
                CONFLICTS+=("$dest_file")
            else
                # 存在しない場合のみコピー
                cp -r "$file" "$dest_file"
            fi
        done
        echo "  完了: $dest"
    fi
}

# --------------------------------------------
# インストール実行
# --------------------------------------------

# 1. CLAUDE.md (マーカー付き追記)
echo "[1/8] CLAUDE.md"
append_with_marker "$DOTFILES_CLAUDE/CLAUDE.md" "$TARGET_DIR/CLAUDE.md" "CLAUDE.md"

# 2. ignore (マーカー付き追記)
echo "[2/8] ignore"
if [ -f "$DOTFILES_CLAUDE/ignore" ]; then
    append_with_marker "$DOTFILES_CLAUDE/ignore" "$TARGET_DIR/ignore" "ignore"
else
    echo "  スキップ: ソースファイルなし"
fi

# 3. settings.json (マージ)
echo "[3/8] settings.json"
if [ -f "$DOTFILES_CLAUDE/settings.json" ]; then
    merge_json "$DOTFILES_CLAUDE/settings.json" "$TARGET_DIR/settings.json" "settings.json"
else
    echo "  スキップ: ソースファイルなし"
fi

# 4. mcp.json (マージ)
echo "[4/8] mcp.json"
if [ -f "$DOTFILES_CLAUDE/mcp.json" ]; then
    merge_json "$DOTFILES_CLAUDE/mcp.json" "$TARGET_DIR/mcp.json" "mcp.json"
else
    echo "  スキップ: ソースファイルなし"
fi

# 5. commands/ (コピー)
echo "[5/8] commands/"
copy_directory "$DOTFILES_CLAUDE/commands" "$TARGET_DIR/commands" "commands"

# 6. skills/ (コピー)
echo "[6/8] skills/"
copy_directory "$DOTFILES_CLAUDE/skills" "$TARGET_DIR/skills" "skills"

# 7. agents/ (コピー)
echo "[7/8] agents/"
copy_directory "$DOTFILES_CLAUDE/agents" "$TARGET_DIR/agents" "agents"

# 8. hooks/ (コピー)
echo "[8/8] hooks/"
copy_directory "$DOTFILES_CLAUDE/hooks" "$TARGET_DIR/hooks" "hooks"

echo ""
echo "=== インストール完了 ==="
echo ""
echo "インストールされた内容:"
echo "  - CLAUDE.md     (コーディング原則、Git ワークフロー等)"
echo "  - ignore        (無視パターン)"
echo "  - settings.json (設定)"
echo "  - mcp.json      (MCP設定)"
echo "  - commands/     (カスタムコマンド)"
echo "  - skills/       (スキル)"
echo "  - agents/       (エージェント)"
echo "  - hooks/        (フック)"

# 競合があれば警告を表示
if [ ${#CONFLICTS[@]} -gt 0 ]; then
    echo ""
    echo "=== 警告 ==="
    echo "以下のファイルはすでに存在しています。手動で統合してください:"
    echo ""
    for conflict in "${CONFLICTS[@]}"; do
        echo "  - $conflict"
    done
    echo ""
    echo "dotfilesのソース: $DOTFILES_CLAUDE"
fi
