// ==============================================
// Aplin 型定義集約ファイル（逆生成）
// 分析日時: 2025-08-02T09:35:00Z
// ==============================================
// 
// このファイルは Aplin アプリケーションで使用されている
// 全ての型定義を集約したものです。Clean Architecture の
// 各レイヤーで定義されている型を整理・統合しています。
//
// ==============================================

package com.nagopy.android.aplin.reverse

import android.graphics.drawable.Drawable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

// ==============================================
// Domain Layer - エンティティ型定義
// ==============================================

/**
 * パッケージ情報を表すドメインモデル
 * Android PackageInfo から変換された、アプリケーション独自のモデル
 */
data class PackageModel(
    val packageName: String,      // パッケージ名（例: "com.example.app"）
    val label: String,           // 表示名（例: "マイアプリ"）
    val icon: Drawable,          // アプリアイコン
    val isEnabled: Boolean,      // 有効状態（true: 有効, false: 無効）
    val firstInstallTime: Long,  // 初回インストール時刻（Unix timestamp）
    val lastUpdateTime: Long,    // 最終更新時刻（Unix timestamp）
    val versionName: String?     // バージョン名（例: "1.0.0"）
)

/**
 * カテゴリ別に分類されたパッケージコレクション
 * LoadPackagesUseCase の実行結果として返される
 */
data class PackagesModel(
    val disableablePackages: List<PackageModel>,  // 無効化可能アプリ（システムアプリで無効化可能）
    val disabledPackages: List<PackageModel>,     // 無効化済みアプリ
    val userPackages: List<PackageModel>,         // ユーザーアプリ（Google Play等からインストール）
    val allPackages: List<PackageModel>           // 全アプリ（システム・ユーザー問わず）
)

// ==============================================
// Repository Layer - データアクセス型定義
// ==============================================

/**
 * パッケージ情報取得リポジトリインターフェース
 * Android PackageManager API をラップ
 */
interface PackageRepository {
    /**
     * インストール済み全パッケージ取得
     * @return Android PackageInfo のリスト
     */
    suspend fun loadAll(): List<android.content.pm.PackageInfo>

    /**
     * ホームランチャーアプリのパッケージ名一覧取得
     * @return ホームアプリのパッケージ名セット
     */
    suspend fun loadHomePackageNames(): Set<String>

    /**
     * 現在デフォルト設定されているホームアプリ取得
     * @return デフォルトホームアプリのパッケージ名、未設定時はnull
     */ 
    suspend fun loadCurrentDefaultHomePackageName(): String?

    /**
     * アプリケーション表示名取得
     * @param applicationInfo Android ApplicationInfo
     * @return アプリケーション表示名
     */
    fun loadLabel(applicationInfo: android.content.pm.ApplicationInfo): String

    /**
     * アプリケーションアイコン取得
     * @param applicationInfo Android ApplicationInfo
     * @return アプリケーションアイコン
     */
    fun loadIcon(applicationInfo: android.content.pm.ApplicationInfo): Drawable

    // システムパッケージ情報（プロパティ）
    val systemPackage: android.content.pm.PackageInfo?
    val permissionControllerPackageName: String?
    val servicesSystemSharedLibraryPackageName: String?
    val sharedSystemSharedLibraryPackageName: String?
    val printSpoolerPackageName: String?
    val deviceProvisioningPackage: String?
}

/**
 * デバイスポリシー管理リポジトリインターフェース
 * Android DevicePolicyManager API をラップ
 */
interface DevicePolicyRepository {
    /**
     * アクティブなデバイス管理者チェック
     * @param packageName チェック対象パッケージ名
     * @return デバイス管理者の場合true
     */
    suspend fun isActiveAdmin(packageName: String): Boolean

    /**
     * デバイスオーナーアプリチェック
     * @param packageName チェック対象パッケージ名
     * @return デバイスオーナーの場合true
     */
    suspend fun isDeviceOwnerApp(packageName: String): Boolean

    /**
     * プロファイルオーナーアプリチェック
     * @param packageName チェック対象パッケージ名
     * @return プロファイルオーナーの場合true
     */
    suspend fun isProfileOwnerApp(packageName: String): Boolean
}

// ==============================================
// Use Case Layer - ビジネスロジック型定義
// ==============================================

/**
 * パッケージ読み込みユースケース
 * Repository からデータを取得し、ビジネスロジックを適用
 */
interface LoadPackagesUseCase {
    /**
     * パッケージ情報読み込み実行
     * @return カテゴリ別分類済みパッケージモデル
     */
    suspend fun execute(): PackagesModel
}

/**
 * パッケージ分類ユースケース
 * パッケージの種別・状態による分類ロジック
 */
interface CategorizePackageUseCase {
    /**
     * システムアプリ（プリインストールアプリ）判定
     * @param packageInfo Android PackageInfo
     * @return システムアプリの場合true
     */
    fun isBundled(packageInfo: android.content.pm.PackageInfo): Boolean

    /**
     * 無効化可能アプリ判定
     * @param packageInfo 判定対象パッケージ
     * @param homePackages ホームアプリ一覧
     * @param currentDefaultHomePackageName 現在のデフォルトホーム
     * @return 無効化可能な場合true
     */
    fun isDisableable(
        packageInfo: android.content.pm.PackageInfo,
        homePackages: Set<String>,
        currentDefaultHomePackageName: String?
    ): Boolean
}

// ==============================================
// UI Layer - 画面状態・イベント型定義
// ==============================================

/**
 * メイン画面のUI状態
 * StateFlow で管理される画面全体の状態
 */
data class MainUiState(
    val isLoading: Boolean = false,                              // 読み込み中フラグ
    val packagesModel: PackagesModel? = null,                    // パッケージデータ
    val sortOrder: SortOrder = SortOrder.DEFAULT,                // ソート順
    val searchWidgetState: SearchWidgetState = SearchWidgetState.CLOSED, // 検索Widget状態
    val searchText: String = ""                                  // 検索テキスト
)

/**
 * 検索Widgetの状態
 * 検索バーの開閉状態を管理
 */
enum class SearchWidgetState {
    OPENED,   // 検索バー展開中
    CLOSED    // 検索バー閉じた状態
}

/**
 * アプリカテゴリ列挙
 * 画面上でのカテゴリ表示用
 */
enum class AppCategory(val displayName: String) {
    DISABLEABLE("無効化できるアプリ"),
    DISABLED("無効化されたアプリ"),
    USER("ユーザーアプリ"),
    ALL("すべてのアプリ")
}

/**
 * 画面遷移定義
 * Jetpack Navigation Compose用
 */
sealed class Screen {
    object Main : Screen()
    object Settings : Screen()
}

// ==============================================
// ViewModel - 画面制御型定義
// ==============================================

/**
 * メイン画面ViewModel インターフェース
 * UI イベントハンドリングと状態管理
 */
interface MainViewModel {
    // 状態
    val viewModelState: StateFlow<MainUiState>
    val launcherLargeIconSize: Int

    // アクション
    fun updatePackages()
    fun startDetailSettingsActivity(activity: android.app.Activity, pkg: String)
    fun searchByWeb(activity: android.app.Activity, packageModel: PackageModel)
    fun startOssLicensesActivity(activity: android.app.Activity)
    fun sharePackages(activity: android.app.Activity, packages: List<PackageModel>)
    fun updateSearchWidgetState(newValue: SearchWidgetState)
    fun updateSearchTextState(newValue: String)
}

// ==============================================
// 設定・プリファレンス型定義
// ==============================================

/**
 * ソート順設定
 * ユーザーが選択可能なソート方式
 */
enum class SortOrder(val displayName: String) {
    LABEL("アプリ名"),
    PACKAGE_NAME("パッケージ名"),
    FIRST_INSTALL_TIME("インストール日時"),
    LAST_UPDATE_TIME("更新日時");

    companion object {
        val DEFAULT = LABEL
        const val KEY = "sort_order"
    }

    /**
     * PackagesModel をソート
     * @param packagesModel ソート対象
     * @return ソート済み PackagesModel
     */
    fun sort(packagesModel: PackagesModel): PackagesModel
}

/**
 * 表示項目設定
 * アプリ一覧で表示する追加情報
 */
enum class DisplayItem(val displayName: String) {
    INSTALL_TIME("インストール日時"),
    UPDATE_TIME("更新日時"),
    VERSION("バージョン");

    companion object {
        const val KEY = "display_items"
    }
}

/**
 * ユーザーデータストア
 * DataStore Preferences によるユーザー設定管理
 */
interface UserDataStore {
    /**
     * 表示項目設定のFlow
     * @return 選択された表示項目のリスト
     */
    val displayItems: Flow<List<DisplayItem>>

    /**
     * ソート順設定のFlow
     * @return 選択されたソート順
     */
    val sortOrder: Flow<SortOrder>
}

// ==============================================
// 広告・GDPR型定義
// ==============================================

/**
 * 広告表示状態
 * GDPR同意状況に基づく広告表示制御
 */
sealed class AdsStatus {
    object NotInitialized : AdsStatus()      // 未初期化
    object Personalized : AdsStatus()        // パーソナライズ広告表示可能
    object NonPersonalized : AdsStatus()     // 非パーソナライズ広告のみ表示可能
    object Denied : AdsStatus()              // 広告表示拒否
    object Error : AdsStatus()               // エラー状態
}

/**
 * 広告ViewModel インターフェース
 * UMP SDK による GDPR 対応とAdMob統合
 */
interface AdsViewModel {
    // 状態
    val adsState: StateFlow<AdsStatus>
    val isGDPRState: StateFlow<Boolean>

    // アクション
    fun init(activity: android.app.Activity)
    fun loadForm(activity: android.app.Activity, force: Boolean = false)
    fun updateAds(state: AdsStatus, adView: com.google.android.gms.ads.AdView)
}

// ==============================================
// テーマ・UI 型定義
// ==============================================

/**
 * アプリテーマ設定
 * Material Design 3 対応
 */
object AplinTheme {
    // カラーテーマは実装で定義
    // 動的カラー対応（Android 12+）
}

/**
 * Compose UI プレビュー用データ
 * プレビューで使用するサンプルデータ
 */
object PreviewData {
    /**
     * サンプルPackageModel生成
     * @param packageName パッケージ名
     * @param label 表示名
     * @return サンプル PackageModel
     */
    fun samplePackageModel(
        packageName: String = "com.example.sample",
        label: String = "Sample App"
    ): PackageModel

    /**
     * サンプルPackagesModel生成
     * @return テスト用 PackagesModel
     */
    fun samplePackagesModel(): PackagesModel
}

// ==============================================
// Koin DI - 依存性注入型定義
// ==============================================

/**
 * Koin モジュール定義インターフェース
 * 各レイヤーでの依存性注入設定
 */
interface DIModule {
    /**
     * Koin Module
     * 実装クラスでorg.koin.dsl.module を返す
     */
    val module: org.koin.core.module.Module
}

/**
 * アプリケーションモジュール
 * Application Context 等の提供
 */
object AppModule : DIModule {
    override val module: org.koin.core.module.Module
        get() = TODO("Implementation in AppModule.kt")
}

/**
 * ディスパッチャーモジュール
 * Coroutine Dispatcher の提供
 */
object DispatcherModule : DIModule {
    override val module: org.koin.core.module.Module
        get() = TODO("Implementation in DispatcherModule.kt")
}

/**
 * リポジトリモジュール
 * Repository 実装の提供
 */
object RepositoryModule : DIModule {
    override val module: org.koin.core.module.Module
        get() = TODO("Implementation in RepositoryModule.kt")
}

/**
 * ユースケースモジュール
 * UseCase 実装の提供
 */
object UseCaseModule : DIModule {
    override val module: org.koin.core.module.Module
        get() = TODO("Implementation in UseCaseModule.kt")
}

/**
 * UIモジュール
 * ViewModel 等の提供
 */
object UiModule : DIModule {
    override val module: org.koin.core.module.Module
        get() = TODO("Implementation in UiModule.kt")
}

// ==============================================
// エラーハンドリング型定義
// ==============================================

/**
 * アプリケーション独自例外基底クラス
 * 各レイヤーでの例外ハンドリング統一
 */
sealed class AplinException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    
    /**
     * パッケージ読み込みエラー
     * PackageManager API呼び出し失敗時
     */
    class PackageLoadException(message: String, cause: Throwable? = null) : AplinException(message, cause)
    
    /**
     * 権限不足エラー
     * QUERY_ALL_PACKAGES等の権限不足時
     */
    class PermissionDeniedException(message: String, cause: Throwable? = null) : AplinException(message, cause)
    
    /**
     * デバイスポリシーエラー
     * DevicePolicyManager API呼び出し失敗時
     */
    class DevicePolicyException(message: String, cause: Throwable? = null) : AplinException(message, cause)
    
    /**
     * 設定保存エラー
     * DataStore書き込み失敗時
     */
    class PreferenceSaveException(message: String, cause: Throwable? = null) : AplinException(message, cause)
}

/**
 * 結果型定義
 * 成功・失敗を型安全に表現
 */
sealed class Result<out T> {
    /**
     * 成功結果
     * @param data 成功時のデータ
     */
    data class Success<T>(val data: T) : Result<T>()
    
    /**
     * エラー結果
     * @param exception 発生した例外
     */
    data class Error(val exception: AplinException) : Result<Nothing>()
    
    /**
     * 読み込み中状態
     */
    object Loading : Result<Nothing>()
}

// ==============================================
// テスト用型定義
// ==============================================

/**
 * テスト用データファクトリ
 * MockK や JUnit テストで使用
 */
object TestDataFactory {
    /**
     * テスト用PackageInfo生成
     */
    fun createMockPackageInfo(
        packageName: String = "com.test.app",
        isSystemApp: Boolean = false,
        isEnabled: Boolean = true
    ): android.content.pm.PackageInfo

    /**
     * テスト用PackageModel生成
     */
    fun createPackageModel(
        packageName: String = "com.test.app",
        label: String = "Test App"
    ): PackageModel

    /**
     * テスト用PackagesModel生成
     */
    fun createPackagesModel(
        disableableCount: Int = 5,
        disabledCount: Int = 2,
        userCount: Int = 10
    ): PackagesModel
}

// ==============================================
// 拡張関数型定義
// ==============================================

/**
 * PackageInfo 拡張関数
 * Kotlin らしい便利メソッド追加
 */
fun android.content.pm.PackageInfo.isSystemApp(): Boolean =
    (this.applicationInfo?.flags ?: 0) and android.content.pm.ApplicationInfo.FLAG_SYSTEM != 0

fun android.content.pm.PackageInfo.isEnabled(): Boolean =
    this.applicationInfo?.enabled ?: false

/**
 * Flow 拡張関数
 * StateFlow との相互変換等
 */
fun <T> Flow<T>.stateIn(
    scope: kotlinx.coroutines.CoroutineScope,
    started: kotlinx.coroutines.flow.SharingStarted,
    initialValue: T
): StateFlow<T> = kotlinx.coroutines.flow.stateIn(scope, started, initialValue)

// ==============================================
// 定数定義
// ==============================================

/**
 * アプリケーション定数
 */
object AplinConstants {
    // DataStore ファイル名
    const val DATASTORE_NAME = "settings"
    
    // ログタグ
    const val LOG_TAG = "Aplin"
    
    // パフォーマンス設定
    const val CACHE_VALIDITY_MS = 30_000L  // 30秒
    const val MAX_CONCURRENT_OPERATIONS = 10
    
    // UI設定
    const val SEARCH_DEBOUNCE_MS = 300L
    const val LOADING_DELAY_MS = 200L
    
    // 広告設定
    const val GOOGLE_VENDOR_ID = 755
    
    // システムパッケージ
    val SYSTEM_PROTECTED_PACKAGES = setOf(
        "android",
        "com.android.systemui", 
        "com.android.settings",
        "com.android.permissioncontroller"
    )
}

// ==============================================
// バージョン情報
// ==============================================

/**
 * 型定義ファイルバージョン情報
 */
object InterfaceVersion {
    const val VERSION = "1.0.0"
    const val GENERATED_DATE = "2025-08-02T09:35:00Z"
    const val SOURCE_APP_VERSION = "5.5.1"
    const val MIN_SDK_VERSION = 26
    const val TARGET_SDK_VERSION = 34
    const val COMPILE_SDK_VERSION = 34
}

// ==============================================
// 型エイリアス（簡略化）
// ==============================================

// Android 標準型の簡略化
typealias AndroidPackageInfo = android.content.pm.PackageInfo
typealias AndroidApplicationInfo = android.content.pm.ApplicationInfo
typealias AndroidActivity = android.app.Activity
typealias AndroidDrawable = android.graphics.drawable.Drawable

// Coroutines 型の簡略化
typealias CoroutineScope = kotlinx.coroutines.CoroutineScope
typealias CoroutineDispatcher = kotlinx.coroutines.CoroutineDispatcher

// 関数型の簡略化
typealias PackageFilter = (PackageModel) -> Boolean
typealias PackageComparator = Comparator<PackageModel>
typealias StateUpdateListener<T> = (T) -> Unit