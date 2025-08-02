# データベース設計（逆生成）

## スキーマ概要

### データベース管理システム
SQLite/DataStore

### テーブル一覧
- mainuistate
- packagemodel
- packagesmodel

## テーブル詳細

### mainuistate テーブル

**カラム**:
- `isLoading`: Boolean - Property of data class
- `packagesModel`: PackagesModel? = null - Property of data class
- `sortOrder`: SortOrder = SortOrder.DEFAULT - Property of data class
- `searchWidgetState`: SearchWidgetState = SearchWidgetState.CLOSED - Property of data class
- `searchText`: String = "" - Property of data class
### packagemodel テーブル

**カラム**:
- `packageName`: String - Property of data class
- `label`: String - Property of data class
- `icon`: Drawable - Property of data class
- `isEnabled`: Boolean - Property of data class
- `firstInstallTime`: Long - Property of data class
- `lastUpdateTime`: Long - Property of data class
- `versionName`: String? - Property of data class
### packagesmodel テーブル

**カラム**:
- `disableablePackages`: List<PackageModel> - Property of data class
- `disabledPackages`: List<PackageModel> - Property of data class
- `userPackages`: List<PackageModel> - Property of data class
- `allPackages`: List<PackageModel> - Property of data class

## データアクセスパターン

### 実装パターン
- Repository パターンによるデータアクセス抽象化
- UseCase による業務ロジック分離
- Flow による非同期データストリーム

### パフォーマンス考慮事項
- DataStore による設定データの効率的な管理
- SharedPreferences からの移行
- 非同期処理による UI ブロック回避
