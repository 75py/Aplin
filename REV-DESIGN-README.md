# Rev-Design Tool

逆設計ツール - 既存のコードベースから技術設計文書を自動生成

## 概要

`rev-design` は既存のコードベースを分析し、アーキテクチャ、データフロー、API仕様、データベーススキーマ、TypeScript/Kotlinインターフェースを抽出して設計書として文書化するツールです。

## 機能

- **アーキテクチャ分析**: Clean Architecture、MVC、MVVMなどのパターンを自動検出
- **データフロー抽出**: ユーザーインタラクション、API呼び出し、状態管理の流れを図式化
- **API仕様生成**: エンドポイント、リクエスト/レスポンス構造を自動抽出
- **データベーススキーマ逆生成**: テーブル定義、リレーションシップを分析
- **型定義整理**: エンティティ型、API型、コンポーネントPropsを抽出
- **コンポーネント設計分析**: UIコンポーネント階層と状態管理を分析

## 対応プロジェクト

- ✅ Android (Kotlin/Java + Jetpack Compose)
- ✅ Spring Boot
- ✅ React/Vue/Angular
- ✅ Node.js/Express
- ✅ Python (FastAPI/Django)
- ✅ TypeScript プロジェクト

## インストール

```bash
# リポジトリをクローン
git clone https://github.com/75py/Aplin.git
cd Aplin

# 実行権限を付与
chmod +x rev-design.sh rev-design.py
```

### 必要な環境

- Python 3.7+
- Bash (Linux/macOS/WSL)

## 使用方法

### 基本的な使用方法

```bash
# 現在のディレクトリを分析
./rev-design.sh .

# 特定のプロジェクトを分析
./rev-design.sh /path/to/your/project

# Pythonスクリプトを直接実行
python3 rev-design.py /path/to/project
```

### オプション

```bash
# 特定の設計書のみ生成
./rev-design.sh --target architecture .
./rev-design.sh --target api .
./rev-design.sh --target database .

# 出力形式指定
./rev-design.sh --format markdown .

# ヘルプ表示
./rev-design.sh --help
```

## 生成されるファイル

分析実行後、`docs/reverse/` ディレクトリに以下のファイルが生成されます：

### アーキテクチャ設計書
- **ファイル**: `{プロジェクト名}-architecture.md`
- **内容**: アーキテクチャパターン、技術スタック、レイヤー構成、デザインパターン

### データフロー図
- **ファイル**: `{プロジェクト名}-dataflow.md`
- **内容**: ユーザーインタラクションフロー、状態管理フロー、エラーハンドリング（Mermaid図含む）

### API仕様書
- **ファイル**: `{プロジェクト名}-api-specs.md`
- **内容**: エンドポイント一覧、リクエスト/レスポンス構造、認証方式

### データベース設計
- **ファイル**: `{プロジェクト名}-database.md`
- **内容**: テーブル定義、カラム仕様、リレーションシップ、制約条件

### 型定義集約
- **ファイル**: `{プロジェクト名}-interfaces.kt` (または `.ts`)
- **内容**: エンティティ型、API型、コンポーネントProps型、状態管理型

## 実例: Aplinプロジェクト分析

```bash
$ ./rev-design.sh .

🔍 Rev-Design Analysis Tool
==========================
Project Path: .
Target: all
Format: markdown

🚀 Starting reverse engineering analysis...
Starting reverse engineering analysis for Aplin...
Analyzing architecture...
Analyzing data flow...
Analyzing API specifications...
Analyzing database schema...
Analyzing interfaces and type definitions...
Analyzing component design...
Generating design documents...

=== Reverse Design Analysis Complete ===
Project: Aplin
Architecture: Clean Architecture
Framework: Android
Generated documents in: docs/reverse/
- Aplin-architecture.md
- Aplin-dataflow.md
- Aplin-api-specs.md
- Aplin-database.md
- Aplin-interfaces.kt

✅ Analysis complete! Generated documents:
   📄 Aplin-architecture.md
   📄 Aplin-dataflow.md
   📄 Aplin-api-specs.md
   📄 Aplin-database.md
   📄 Aplin-interfaces.kt
```

## 分析結果例

### アーキテクチャ分析結果
- **パターン**: Clean Architecture
- **フレームワーク**: Android
- **技術スタック**: 
  - UI Framework: Jetpack Compose
  - DI Framework: Koin
  - Storage: DataStore
  - Testing: JUnit + MockK

### 検出されたデザインパターン
- Repository Pattern
- Use Case Pattern
- Observer Pattern (Flow)
- Singleton Pattern

## カスタマイズ

### 分析ロジックの拡張

`rev-design.py` を編集して、特定のフレームワークや言語への対応を追加できます：

```python
def detect_framework(self, build_file: Path, root_build_file: Path) -> str:
    # 新しいフレームワーク検出ロジックを追加
    content = build_content + root_content
    
    if "your_framework" in content:
        return "Your Framework"
    # ... 既存のロジック
```

### 出力フォーマットの変更

各 `generate_*_doc()` メソッドを編集して、出力フォーマットをカスタマイズできます。

## トラブルシューティング

### よくある問題

1. **Python3が見つからない**
   ```bash
   # Ubuntu/Debian
   sudo apt install python3
   
   # macOS (Homebrew)
   brew install python3
   ```

2. **権限エラー**
   ```bash
   chmod +x rev-design.sh rev-design.py
   ```

3. **プロジェクトが検出されない**
   - プロジェクトルートディレクトリで実行しているか確認
   - 対応するビルドファイル（build.gradle.kts, package.json等）が存在するか確認

## 貢献

プルリクエストや課題報告を歓迎します。新しいフレームワークへの対応、分析精度の向上、出力フォーマットの改善などを検討しています。

## ライセンス

Apache 2.0 License - 詳細は [LICENSE](LICENSE) ファイルを参照してください。

## 更新履歴

- **v1.0.0** (2025-08-02): 初期リリース
  - Android (Kotlin) プロジェクト対応
  - Clean Architecture検出
  - Jetpack Compose コンポーネント分析
  - Mermaid図生成機能

## 関連ドキュメント

- [発見タスク一覧](docs/reverse/Aplin-discovered-tasks.md) - 既存の分析結果例
- [アーキテクチャ設計](docs/reverse/Aplin-architecture.md) - 生成されたアーキテクチャ文書例
- [データフロー図](docs/reverse/Aplin-dataflow.md) - 生成されたデータフロー文書例