# Aplin API仕様書（逆生成）

## 分析日時
2025-08-02T09:25:00Z

## API概要

AplinはAndroidアプリケーションとして、直接的なWebAPIを提供するのではなく、Android SDKのシステムAPIをラップした内部APIアーキテクチャを採用しています。本仕様書では、アプリケーション内部のAPI設計とAndroidシステムAPIとの連携を文書化します。

## 内部API アーキテクチャ

### ベースアーキテクチャ
- **パターン**: Repository Pattern + Use Case Pattern
- **データフロー**: UI → ViewModel → UseCase → Repository → Android System API
- **非同期処理**: Kotlin Coroutines (suspend functions)
- **エラーハンドリング**: try-catch + Logging

## Repository API仕様

### PackageRepository インターフェース

#### suspend fun loadAll(): List<PackageInfo>
**説明**: インストール済み全パッケージ情報を取得

**パラメータ**: なし

**戻り値**:
```kotlin
List<PackageInfo> // Android標準のPackageInfo
```

**実装詳細**:
```kotlin
// PackageManagerからQUERY_ALL_PACKAGES権限で取得
packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
```

**例外**:
- `SecurityException`: QUERY_ALL_PACKAGES権限不足
- `RuntimeException`: システムAPI呼び出し失敗

---

#### suspend fun loadHomePackageNames(): Set<String>
**説明**: ホームランチャーアプリのパッケージ名一覧を取得

**パラメータ**: なし

**戻り値**:
```kotlin
Set<String> // ホームアプリのパッケージ名セット
```

**実装詳細**:
```kotlin
val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
    .map { it.activityInfo.packageName }
    .toSet()
```

---

#### suspend fun loadCurrentDefaultHomePackageName(): String?
**説明**: 現在デフォルトに設定されているホームアプリを取得

**戻り値**:
```kotlin
String? // デフォルトホームアプリのパッケージ名、未設定時はnull
```

---

#### fun loadLabel(applicationInfo: ApplicationInfo): String
**説明**: アプリケーションの表示名を取得

**パラメータ**:
```kotlin
applicationInfo: ApplicationInfo // Android標準のApplicationInfo
```

**戻り値**:
```kotlin
String // アプリケーション表示名
```

---

#### fun loadIcon(applicationInfo: ApplicationInfo): Drawable
**説明**: アプリケーションアイコンを取得

**パラメータ**:
```kotlin
applicationInfo: ApplicationInfo
```

**戻り値**:
```kotlin
Drawable // アプリケーションアイコン
```

### DevicePolicyRepository インターフェース

#### suspend fun isActiveAdmin(packageName: String): Boolean
**説明**: 指定パッケージがアクティブなデバイス管理者かチェック

**パラメータ**:
```kotlin
packageName: String // チェック対象のパッケージ名
```

**戻り値**:
```kotlin
Boolean // デバイス管理者の場合true
```

---

#### suspend fun isDeviceOwnerApp(packageName: String): Boolean
**説明**: 指定パッケージがデバイスオーナーアプリかチェック

**パラメータ**:
```kotlin
packageName: String
```

**戻り値**:
```kotlin
Boolean // デバイスオーナーの場合true
```

---

#### suspend fun isProfileOwnerApp(packageName: String): Boolean
**説明**: 指定パッケージがプロファイルオーナーアプリかチェック

**パラメータ**:
```kotlin
packageName: String
```

**戻り値**:
```kotlin
Boolean // プロファイルオーナーの場合true
```

## Use Case API仕様

### LoadPackagesUseCase

#### suspend fun execute(): PackagesModel
**説明**: パッケージ情報を読み込み、カテゴリ別に分類した統合モデルを返す

**パラメータ**: なし

**戻り値**:
```kotlin
data class PackagesModel(
    val disableablePackages: List<PackageModel>,  // 無効化可能アプリ
    val disabledPackages: List<PackageModel>,     // 無効化済みアプリ
    val userPackages: List<PackageModel>,         // ユーザーアプリ
    val allPackages: List<PackageModel>           // 全アプリ
)
```

**内部処理フロー**:
1. PackageRepository.loadAll() を並行実行
2. PackageRepository.loadHomePackageNames() を並行実行
3. PackageRepository.loadCurrentDefaultHomePackageName() を並行実行
4. CategorizePackageUseCase でカテゴリ分類
5. ソート処理実行
6. PackagesModel 構築して返却

**例外**:
- `SecurityException`: 権限不足
- `CancellationException`: 処理キャンセル

### CategorizePackageUseCase

#### fun isBundled(packageInfo: PackageInfo): Boolean
**説明**: システムアプリ（プリインストールアプリ）かどうか判定

**パラメータ**:
```kotlin
packageInfo: PackageInfo
```

**戻り値**:
```kotlin
Boolean // システムアプリの場合true
```

**判定ロジック**:
```kotlin
(packageInfo.applicationInfo?.flags ?: 0) and ApplicationInfo.FLAG_SYSTEM != 0
```

---

#### fun isDisableable(
    packageInfo: PackageInfo,
    homePackages: Set<String>,
    currentDefaultHomePackageName: String?
): Boolean
**説明**: アプリが無効化可能かどうか判定

**パラメータ**:
```kotlin
packageInfo: PackageInfo                    // 判定対象パッケージ
homePackages: Set<String>                   // ホームアプリ一覧
currentDefaultHomePackageName: String?      // 現在のデフォルトホーム
```

**戻り値**:
```kotlin
Boolean // 無効化可能な場合true
```

**判定条件**:
1. システムアプリであること
2. 有効状態であること
3. ホームランチャーでないこと
4. デバイス管理者アプリでないこと
5. システム保護アプリでないこと

## ViewModel API仕様

### MainViewModel

#### val viewModelState: StateFlow<MainUiState>
**説明**: UI状態を表すStateFlow

**型定義**:
```kotlin
data class MainUiState(
    val isLoading: Boolean = false,
    val packagesModel: PackagesModel? = null,
    val sortOrder: SortOrder = SortOrder.DEFAULT,
    val searchWidgetState: SearchWidgetState = SearchWidgetState.CLOSED,
    val searchText: String = ""
)
```

---

#### fun updatePackages()
**説明**: パッケージ情報を更新（再読み込み）

**副作用**:
- LoadPackagesUseCase.execute() 実行
- viewModelState更新（isLoading → packagesModel更新）

---

#### fun startDetailSettingsActivity(activity: Activity, pkg: String)
**説明**: アプリ詳細設定画面を起動

**パラメータ**:
```kotlin
activity: Activity  // 起動元Activity
pkg: String        // 対象パッケージ名
```

**動作**:
```kotlin
val intent = Intent(
    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
    Uri.parse("package:$packageName")
)
activity.startActivity(intent)
```

---

#### fun searchByWeb(activity: Activity, packageModel: PackageModel)
**説明**: ウェブ検索でアプリを検索

**パラメータ**:
```kotlin
activity: Activity          // 起動元Activity  
packageModel: PackageModel  // 検索対象アプリ
```

**動作**:
1. ACTION_WEB_SEARCH Intentを試行
2. 失敗時はGoogle検索URLでACTION_VIEW Intent
3. 両方失敗時はログ出力のみ

---

#### fun sharePackages(activity: Activity, packages: List<PackageModel>)
**説明**: パッケージ名一覧を他アプリへ共有

**パラメータ**:
```kotlin
activity: Activity              // 共有元Activity
packages: List<PackageModel>   // 共有対象パッケージ一覧
```

**共有形式**:
```
text/plain
パッケージ名1
パッケージ名2
...
```

### AdsViewModel

#### val adsState: StateFlow<AdsStatus>
**説明**: 広告表示状態を管理

**状態定義**:
```kotlin
sealed class AdsStatus {
    object NotInitialized : AdsStatus()    // 未初期化
    object Personalized : AdsStatus()      // パーソナライズ広告OK
    object NonPersonalized : AdsStatus()   // 非パーソナライズ広告のみ
    object Denied : AdsStatus()            // 広告拒否
    object Error : AdsStatus()             // エラー状態
}
```

---

#### fun init(activity: Activity)
**説明**: UMP SDKを初期化し、GDPR同意状態をチェック

**処理フロー**:
1. ConsentRequestParameters構築
2. ConsentInformation.requestConsentInfoUpdate()
3. 必要に応じて同意フォーム表示
4. adsState更新

---

#### fun updateAds(state: AdsStatus, adView: AdView)
**説明**: 広告表示状態に応じてAdViewを更新

**パラメータ**:
```kotlin
state: AdsStatus  // 現在の広告状態
adView: AdView   // 更新対象のAdView
```

**動作**:
- Personalized: 通常の広告リクエスト
- NonPersonalized: npa=1パラメータ付き非パーソナライズ広告
- その他: 広告読み込み無し

## Data Model API仕様

### PackageModel
```kotlin
data class PackageModel(
    val packageName: String,      // パッケージ名
    val label: String,           // 表示名
    val icon: Drawable,          // アイコン
    val isEnabled: Boolean,      // 有効状態
    val firstInstallTime: Long,  // 初回インストール時刻(Unix timestamp)
    val lastUpdateTime: Long,    // 最終更新時刻(Unix timestamp)  
    val versionName: String?     // バージョン名
)
```

### PackagesModel
```kotlin
data class PackagesModel(
    val disableablePackages: List<PackageModel>,
    val disabledPackages: List<PackageModel>,
    val userPackages: List<PackageModel>,
    val allPackages: List<PackageModel>
)
```

### SortOrder
```kotlin
enum class SortOrder(val displayName: String) {
    LABEL("アプリ名"),
    PACKAGE_NAME("パッケージ名"), 
    FIRST_INSTALL_TIME("インストール日時"),
    LAST_UPDATE_TIME("更新日時");
    
    fun sort(packagesModel: PackagesModel): PackagesModel
}
```

## Android システムAPI 連携仕様

### 使用権限
```xml
<uses-permission android:name="android.permission.QUERY_ALL_PACKAGES" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### 主要API呼び出し

#### PackageManager API
```kotlin
// 全パッケージ取得
packageManager.getInstalledPackages(PackageManager.GET_META_DATA)

// ホームアプリ取得
val homeIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
packageManager.queryIntentActivities(homeIntent, PackageManager.MATCH_DEFAULT_ONLY)

// アプリ詳細取得
packageManager.getApplicationInfo(packageName, 0)
```

#### DevicePolicyManager API
```kotlin
devicePolicyManager.isDeviceOwnerApp(packageName)
devicePolicyManager.isProfileOwnerApp(packageName)
devicePolicyManager.getActiveAdmins()?.any { it.packageName == packageName }
```

#### Intent API
```kotlin
// アプリ詳細設定画面
Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))

// ウェブ検索
Intent(Intent.ACTION_WEB_SEARCH).putExtra(SearchManager.QUERY, query)
Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=$query"))
```

## エラー仕様

### 共通エラー形式
```kotlin
// 例外ハンドリング
try {
    // API呼び出し
} catch (e: SecurityException) {
    logcat { "Permission denied: $e" }
    // UI状態維持、ユーザーに影響最小限
} catch (e: Exception) {
    logcat { "Unexpected error: $e" }
    // エラー状態設定
}
```

### エラーコード（ログレベル）

| ログレベル | 用途 | 例 |
|------------|------|-----|
| VERBOSE | 詳細デバッグ情報 | UMP状態変更、フロー追跡 |
| DEBUG | 開発時デバッグ | 関数呼び出し、状態変更 |
| INFO | 情報提供 | アプリ起動、データ読み込み完了 |
| WARN | 警告 | 非推奨API使用、軽微な問題 |
| ERROR | エラー | 権限不足、Activity起動失敗 |

## パフォーマンス仕様

### 応答時間目標
- パッケージ読み込み: 5秒以内（1000アプリの場合）
- UI更新: 60fps維持
- 設定変更反映: 100ms以内

### 並行処理仕様
```kotlin
// 複数Repository操作の並行実行
coroutineScope {
    val loadAllAsync = async { packageRepository.loadAll() }
    val loadHomeAsync = async { packageRepository.loadHomePackageNames() }
    val loadDefaultHomeAsync = async { packageRepository.loadCurrentDefaultHomePackageName() }
    
    // 結果待ち合わせ
    val all = loadAllAsync.await()
    val home = loadHomeAsync.await()  
    val defaultHome = loadDefaultHomeAsync.await()
}
```

## セキュリティ仕様

### 権限チェック
- QUERY_ALL_PACKAGES: Android 11+で必須
- 実行時権限チェックは不要（インストール時付与）

### プライバシー
- 個人情報の収集・保存なし
- GDPR対応: UMP SDK による同意管理
- Google Analytics無効化

### データ保護
- ユーザー設定のみローカル保存（DataStore）
- 外部送信データなし（広告除く）

## 拡張性設計

### インターフェース指向設計
```kotlin
interface PackageRepository {
    // 実装はPackageRepositoryImplで提供
    // テスト時はMockRepository使用可能
}
```

### モジュール分離
- data: Repository実装
- domain: ビジネスロジック
- ui: 画面・ViewModel

### 設定拡張
```kotlin
enum class DisplayItem(val displayName: String) {
    INSTALL_TIME("インストール日時"),
    UPDATE_TIME("更新日時"),
    VERSION("バージョン");
    // 新項目追加時は enum に追加するだけ
}
```

この内部API設計により、Aplinは Android システムの複雑性を適切に抽象化し、保守性と拡張性を両立したアーキテクチャを実現しています。