#!/usr/bin/env python3
"""
セキュリティスキャンスクリプト
使用方法: python security_scan.py --path ./src
"""

import os
import re
import sys
import argparse
from pathlib import Path

class SecurityScanner:
    def __init__(self, path):
        self.path = Path(path)
        self.issues = []
    
    def scan(self):
        """セキュリティスキャンを実行"""
        print(f"🔍 セキュリティスキャン開始: {self.path}")
        
        for file_path in self.path.rglob('*.ts'):
            self.scan_file(file_path)
        
        for file_path in self.path.rglob('*.js'):
            self.scan_file(file_path)
        
        self.report()
    
    def scan_file(self, file_path):
        """個別ファイルをスキャン"""
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
                
                # SQLインジェクションチェック
                if re.search(r'`SELECT.*\$\{', content):
                    self.issues.append({
                        'file': str(file_path),
                        'type': 'SQLインジェクション',
                        'severity': '高',
                        'message': 'テンプレートリテラルでのSQL文字列結合が検出されました'
                    })
                
                # ハードコードされたシークレット
                if re.search(r'(password|secret|api_key)\s*=\s*["\'][^"\']+["\']', content, re.IGNORECASE):
                    self.issues.append({
                        'file': str(file_path),
                        'type': 'ハードコードされたシークレット',
                        'severity': '高',
                        'message': 'コード内に機密情報がハードコードされている可能性があります'
                    })
                
                # eval使用
                if 'eval(' in content:
                    self.issues.append({
                        'file': str(file_path),
                        'type': 'eval使用',
                        'severity': '高',
                        'message': 'evalの使用はセキュリティリスクです'
                    })
                
                # console.logでのパスワード出力
                if re.search(r'console\.log.*password', content, re.IGNORECASE):
                    self.issues.append({
                        'file': str(file_path),
                        'type': 'ログへの機密情報出力',
                        'severity': '中',
                        'message': 'パスワードがログに出力されている可能性があります'
                    })
        
        except Exception as e:
            print(f"⚠️  {file_path} の読み込みエラー: {e}")
    
    def report(self):
        """結果レポート"""
        print("\n" + "="*60)
        print("📊 セキュリティスキャン結果")
        print("="*60)
        
        if not self.issues:
            print("✅ セキュリティ上の問題は検出されませんでした")
            return
        
        high_count = sum(1 for i in self.issues if i['severity'] == '高')
        medium_count = sum(1 for i in self.issues if i['severity'] == '中')
        
        print(f"\n🚨 検出された問題: {len(self.issues)}件")
        print(f"   高: {high_count}件")
        print(f"   中: {medium_count}件\n")
        
        for idx, issue in enumerate(self.issues, 1):
            severity_icon = "🔴" if issue['severity'] == '高' else "🟡"
            print(f"{idx}. {severity_icon} [{issue['severity']}] {issue['type']}")
            print(f"   ファイル: {issue['file']}")
            print(f"   詳細: {issue['message']}\n")
        
        if high_count > 0:
            sys.exit(1)

def main():
    parser = argparse.ArgumentParser(description='セキュリティスキャンツール')
    parser.add_argument('--path', required=True, help='スキャンするディレクトリパス')
    args = parser.parse_args()
    
    scanner = SecurityScanner(args.path)
    scanner.scan()

if __name__ == '__main__':
    main()