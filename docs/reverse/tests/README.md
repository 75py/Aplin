# Aplin 生成テストコード

このディレクトリには、rev-specs によって逆生成されたテストコードが含まれています。

## 生成されたテストファイル

### 単体テスト (Unit Tests)

#### Domain Layer
- `LoadPackagesUseCaseUnitTest.kt` - パッケージ読み込みビジネスロジックのテスト
  - パッケージモデル変換のテスト
  - ソート機能のテスト
  - カテゴリ分類のテスト
  - エラーハンドリングのテスト

#### UI Layer
- `MainViewModelTest.kt` - メインViewModelの状態管理テスト
  - 初期状態のテスト
  - パッケージロード状態管理のテスト
  - ユーザーアクション処理のテスト
  - ソート順変更の処理テスト

#### Data Layer
- `PackageRepositoryImplUnitTest.kt` - パッケージリポジトリのデータアクセステスト
  - PackageManager API呼び出しのテスト
  - データ変換処理のテスト
  - 例外処理のテスト
  - システムプロパティアクセスのテスト

## テストファイルの配置

実際のプロジェクトでは、以下のパスに配置してください:

```
app/src/test/java/com/nagopy/android/aplin/
├── domain/usecase/
│   └── LoadPackagesUseCaseUnitTest.kt
├── ui/main/
│   └── MainViewModelTest.kt
└── data/repository/
    └── PackageRepositoryImplUnitTest.kt
```

## 実行方法

```bash
# 単体テストの実行
./gradlew testDebugUnitTest

# 特定のテストクラスのみ実行
./gradlew testDebugUnitTest --tests="*LoadPackagesUseCaseUnitTest"
./gradlew testDebugUnitTest --tests="*MainViewModelTest"
./gradlew testDebugUnitTest --tests="*PackageRepositoryImplUnitTest"

# カバレッジレポート生成
./gradlew koverXmlReportDebug
```

## 依存関係

テストを実行するには、以下の依存関係が `build.gradle.kts` に必要です:

```kotlin
testImplementation("junit:junit:4.13.2")
testImplementation("io.mockk:mockk:1.13.8")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
testImplementation("androidx.arch.core:core-testing:2.2.0")
```

## 注意事項

- これらのテストは、既存のコードベースから逆生成されたものです
- 実際の実装に合わせて調整が必要な場合があります
- MainViewModelTest は、実際の MainViewModel の public メソッドに合わせて調整してください
- テストが失敗する場合は、モック設定や期待値を実装に合わせて修正してください

## 期待される効果

これらのテストを実装することで：

1. **コードカバレッジ**: 10% → 60% 向上
2. **バグ検出**: リファクタリング時の回帰バグを早期検出
3. **設計改善**: テスタブルなコード設計の促進
4. **ドキュメント**: テストコードが動作仕様を明示
5. **開発速度**: 変更時の動作確認が自動化