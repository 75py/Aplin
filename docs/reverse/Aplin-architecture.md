# Aplin アーキテクチャ設計（逆生成）

## 分析日時
2025-08-02T09:15:00Z

## システム概要

### 実装されたアーキテクチャ
- **パターン**: Clean Architecture (Data → Domain → UI)
- **フレームワーク**: Android Jetpack Compose
- **構成**: 3層アーキテクチャ (Data, Domain, UI) + DI Container

### 技術スタック

#### フロントエンド
- **フレームワーク**: Jetpack Compose 1.x
- **状態管理**: StateFlow + Compose State
- **UI ライブラリ**: Material Design 3
- **スタイリング**: Compose Theme System
- **ナビゲーション**: Jetpack Navigation Compose

#### バックエンド（Android App Layer）
- **フレームワーク**: Android SDK 34 (API Level)
- **認証方式**: N/A (ローカルアプリ)
- **データアクセス**: Android PackageManager API
- **バリデーション**: Kotlin Type System + Custom Validation

#### データベース
- **DBMS**: N/A (システムパッケージ情報はPackageManagerから取得)
- **永続化**: DataStore Preferences (設定データ)
- **キャッシュ**: メモリ内キャッシュ（StateFlow）
- **接続プール**: N/A

#### インフラ・ツール
- **ビルドツール**: Gradle 8.14.2 + Android Gradle Plugin 8.10.0
- **テストフレームワーク**: JUnit4 + MockK + AndroidX Test
- **コード品質**: ktlint + Kover (テストカバレッジ)
- **依存性注入**: Koin
- **ログ出力**: Logcat Library

## レイヤー構成

### 発見されたレイヤー
```
app/src/main/java/com/nagopy/android/aplin/
├── AplinApplication.kt          # Application Entry Point
├── AppModule.kt                 # Koin DI Configuration
├── DispatcherModule.kt          # Coroutine Dispatcher Configuration
├── data/                        # Data Layer
│   └── repository/
│       ├── DevicePolicyRepository.kt
│       ├── DevicePolicyRepositoryImpl.kt
│       ├── PackageRepository.kt
│       ├── PackageRepositoryImpl.kt
│       └── RepositoryModule.kt
├── domain/                      # Domain Layer
│   ├── model/
│   │   ├── PackageModel.kt
│   │   └── PackagesModel.kt
│   └── usecase/
│       ├── CategorizePackageUseCase.kt
│       ├── LoadPackagesUseCase.kt
│       └── UseCaseModule.kt
└── ui/                          # UI Layer
    ├── UiModule.kt
    ├── ads/                     # 広告機能
    │   ├── AdsStatus.kt
    │   ├── AdsViewModel.kt
    │   └── compose/
    │       └── AdBanner.kt
    ├── main/                    # メイン画面
    │   ├── AppCategory.kt
    │   ├── MainActivity.kt
    │   ├── MainUiState.kt
    │   ├── MainViewModel.kt
    │   ├── Screen.kt
    │   ├── SearchWidgetState.kt
    │   └── compose/
    │       ├── HorizontalAppList.kt
    │       ├── Loading.kt
    │       ├── MainScreen.kt
    │       ├── RootScreen.kt
    │       └── VerticalAppList.kt
    ├── prefs/                   # 設定画面
    │   ├── DisplayItem.kt
    │   ├── PreferenceScreen.kt
    │   ├── SortOrder.kt
    │   └── UserDataStore.kt
    └── theme/                   # テーマ定義
        ├── Color.kt
        ├── Shape.kt
        ├── Theme.kt
        └── Type.kt
```

### レイヤー責務分析
- **Data Layer**: Android システムAPI（PackageManager, DevicePolicyManager）との通信、データ変換
- **Domain Layer**: ビジネスロジック（パッケージ分類、読み込み処理）、データモデル定義
- **UI Layer**: Jetpack Compose UI、状態管理、ユーザーインタラクション

## デザインパターン

### 発見されたパターン
- **Dependency Injection**: Koin DI による依存性注入（全層で実装）
- **Repository Pattern**: PackageRepository, DevicePolicyRepository で実装
- **Use Case Pattern**: LoadPackagesUseCase, CategorizePackageUseCase で実装
- **MVVM Pattern**: ViewModel + StateFlow による状態管理
- **Strategy Pattern**: SortOrder による並び順戦略パターン

## 非機能要件の実装状況

### セキュリティ
- **認証**: N/A（ローカルアプリケーション）
- **認可**: Android Permission System (QUERY_ALL_PACKAGES)
- **プライバシー**: GDPR対応 UMP SDK による同意管理
- **データ保護**: 個人情報の取得・保存なし

### パフォーマンス
- **非同期処理**: Kotlin Coroutines による並行処理
- **メモリ最適化**: LazyColumn による仮想化リスト
- **読み込み最適化**: async/await による並行データ取得
- **画像最適化**: Android標準 Drawable キャッシュ

### 運用・監視
- **ログ出力**: Logcat Library による構造化ログ
- **エラートラッキング**: try-catch + ログ出力
- **メトリクス収集**: Google Analytics無効化（プライバシー重視）
- **クラッシュレポート**: 未実装

## アーキテクチャ詳細分析

### Clean Architecture実装

```
┌─────────────────────────────────────────────────┐
│                    UI Layer                     │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐│
│  │ MainActivity│ │ Compose UI  │ │ ViewModels  ││
│  └─────────────┘ └─────────────┘ └─────────────┘│
└─────────────────────┬───────────────────────────┘
                      │ StateFlow/Events
┌─────────────────────┴───────────────────────────┐
│                 Domain Layer                    │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐│
│  │ Use Cases   │ │ Models      │ │ Interfaces  ││
│  └─────────────┘ └─────────────┘ └─────────────┘│
└─────────────────────┬───────────────────────────┘
                      │ Repository Interfaces
┌─────────────────────┴───────────────────────────┐
│                  Data Layer                     │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐│
│  │Repositories │ │Android APIs │ │ DataStore   ││
│  └─────────────┘ └─────────────┘ └─────────────┘│
└─────────────────────────────────────────────────┘
```

### 依存性注入（Koin）構成

```kotlin
// Module構成
startKoin {
    modules(
        appModule,          // Application Context
        dispatcherModule,   // Coroutine Dispatchers  
        uiModule,          // ViewModels
        useCaseModule,     // Use Cases
        repositoryModule   // Repositories
    )
}
```

### 状態管理パターン

```kotlin
// StateFlow + Compose State
class MainViewModel : ViewModel() {
    private val _viewModelState = MutableStateFlow(MainUiState())
    val viewModelState = _viewModelState.stateIn(...)
    
    // UI Events → State Updates
    fun updatePackages() {
        viewModelScope.launch {
            val result = loadPackagesUseCase.execute()
            _viewModelState.update { ... }
        }
    }
}
```

## 特殊実装・制約事項

### Android固有の実装
- **QUERY_ALL_PACKAGES権限**: Android 11+ での全パッケージアクセス
- **PackageManager API**: インストール済みアプリケーション情報取得
- **DevicePolicyManager**: デバイス管理者・企業向け機能チェック
- **Intent System**: アプリ詳細設定画面への遷移

### GDPR/プライバシー対応
- **UMP SDK**: Google User Messaging Platform 統合
- **TCF v2準拠**: IAB Transparency & Consent Framework
- **広告表示制御**: パーソナライズ/非パーソナライズ/非表示

### パフォーマンス考慮事項
- **大量データ処理**: 数千個のアプリ情報を効率的に処理
- **UI応答性**: メインスレッドブロック回避
- **メモリ使用量**: アプリアイコン読み込み時の最適化

## 推奨改善点

### アーキテクチャ
- **モジュール分割**: マルチモジュール構成への移行検討
- **キャッシュ層**: Repository層でのデータキャッシュ実装
- **エラーハンドリング**: 統一的なError型の導入

### テスト
- **UI自動テスト**: Compose UIテストの充実
- **統合テスト**: Repository ↔ UseCase 連携テスト
- **E2Eテスト**: メインユーザーフローの自動テスト

### 運用
- **ログ管理**: 本番環境でのログレベル制御
- **クラッシュ対応**: Firebase Crashlytics等の導入検討
- **パフォーマンス監視**: メモリ使用量・起動時間の監視