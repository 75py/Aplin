# Aplin テストケース一覧（逆生成）

## テストケース概要

| ID | テスト名 | カテゴリ | 優先度 | 実装状況 | 推定工数 | 対象ファイル |
|----|----------|----------|--------|----------|----------|-------------|
| TC-001 | PackageModel プロパティ検証 | 単体 | 低 | ✅ | 2h | PackageModel.kt |
| TC-002 | PackagesModel 集約検証 | 単体 | 低 | ✅ | 2h | PackagesModel.kt |
| TC-003 | SortOrder 並び順検証 | 単体 | 低 | ✅ | 1h | SortOrder.kt |
| TC-004 | CategorizePackageUseCase 分類ロジック | 単体 | 中 | ✅ | 4h | CategorizePackageUseCase.kt |
| TC-005 | LoadPackagesUseCase 単体テスト | 単体 | 高 | ❌ | 6h | LoadPackagesUseCase.kt |
| TC-006 | MainViewModel 状態管理 | 単体 | 高 | ❌ | 8h | MainViewModel.kt |
| TC-007 | PackageRepositoryImpl データアクセス | 単体 | 高 | ❌ | 6h | PackageRepositoryImpl.kt |
| TC-008 | UserDataStore 設定管理 | 単体 | 中 | ❌ | 4h | UserDataStore.kt |
| TC-009 | Repository + UseCase 統合 | 統合 | 高 | ❌ | 4h | 複数ファイル |
| TC-010 | LoadPackagesUseCase E2E | 統合 | 中 | ✅ | 6h | LoadPackagesUseCase.kt |
| TC-011 | PackageRepositoryImpl 統合 | 統合 | 中 | 🔸 | 3h | PackageRepositoryImpl.kt |
| TC-012 | DevicePolicyRepositoryImpl 統合 | 統合 | 中 | 🔸 | 3h | DevicePolicyRepositoryImpl.kt |
| TC-013 | MainScreen Compose UI | UI | 高 | ❌ | 5h | MainScreen.kt |
| TC-014 | AppListScreen レンダリング | UI | 中 | ❌ | 4h | AppListScreen.kt |
| TC-015 | SearchWidget 検索機能 | UI | 中 | ❌ | 3h | SearchWidget.kt |
| TC-016 | PackageItem コンポーネント | UI | 中 | ❌ | 3h | PackageItem.kt |
| TC-017 | PreferenceScreen 設定画面 | UI | 低 | ❌ | 3h | PreferenceScreen.kt |
| TC-018 | Theme 適用確認 | UI | 低 | ❌ | 2h | Theme.kt |
| TC-019 | AdsViewModel 広告表示 | 単体 | 低 | ❌ | 3h | AdsViewModel.kt |
| TC-020 | 完全ユーザーフロー | E2E | 高 | ❌ | 8h | 全体 |
| TC-021 | 検索フィルタリングフロー | E2E | 中 | ❌ | 4h | 全体 |
| TC-022 | カテゴリ切替フロー | E2E | 中 | ❌ | 3h | 全体 |
| TC-023 | パッケージアクションフロー | E2E | 中 | ❌ | 4h | 全体 |
| TC-024 | 大量データ処理性能 | パフォーマンス | 中 | ❌ | 4h | LoadPackagesUseCase.kt |
| TC-025 | メモリリーク検出 | パフォーマンス | 中 | ❌ | 3h | 全体 |
| TC-026 | UI応答性能 | パフォーマンス | 低 | ❌ | 3h | MainScreen.kt |
| TC-027 | 無効データ処理 | セキュリティ | 中 | ❌ | 3h | PackageRepositoryImpl.kt |
| TC-028 | 悪意あるパッケージ対策 | セキュリティ | 低 | ❌ | 2h | CategorizePackageUseCase.kt |

**凡例**: ✅完全実装 🔸部分実装 ❌未実装

## 詳細テストケース

### TC-001: PackageModel プロパティ検証テスト ✅

**テスト目的**: PackageModel データクラスの基本プロパティが正しく設定・取得できることを検証

**事前条件**: 
- Mockk ライブラリが利用可能
- テスト用 Drawable モックが作成可能

**テスト手順**:
1. 有効なパラメータで PackageModel インスタンスを作成
2. 各プロパティの値が正しく設定されていることを確認
3. 無効なパラメータ（null）での動作を確認

**期待結果**:
- すべてのプロパティが設定した値と一致
- null 値の適切な処理
- データクラスの equals/hashCode が正常動作

**実装済みファイル**: `app/src/test/java/com/nagopy/android/aplin/domain/model/PackageModelTest.kt`

**実装済みテストケース**:
```kotlin
@Test
fun packageModel_hasCorrectProperties() {
    // 基本プロパティの設定・取得テスト
}

@Test
fun packageModel_withNullVersionName_handlesCorrectly() {
    // null値処理テスト
}
```

**追加推奨テストケース**:
```kotlin
@Test
fun packageModel_equality_worksCorrectly() {
    // 同じ値を持つインスタンスは等価
    val model1 = PackageModel(/*同じパラメータ*/)
    val model2 = PackageModel(/*同じパラメータ*/)
    assertEquals(model1, model2)
    assertEquals(model1.hashCode(), model2.hashCode())
}

@Test
fun packageModel_toString_containsRelevantInfo() {
    // toString()が有用な情報を含む
    val model = PackageModel(/*パラメータ*/)
    val string = model.toString()
    assertTrue(string.contains(model.packageName))
    assertTrue(string.contains(model.label))
}
```

---

### TC-005: LoadPackagesUseCase 単体テスト ❌

**テスト目的**: パッケージ読み込みビジネスロジックの単体テスト

**事前条件**:
- PackageRepository のモックが作成済み
- CategorizePackageUseCase のモックが作成済み
- コルーチンテスト環境が設定済み

**テスト手順**:
1. モック Repository から各種パッケージデータを返すよう設定
2. UseCase の execute() メソッドを実行
3. 返された PackagesModel の内容を検証
4. 各カテゴリ（全て、無効化可能、無効、ユーザー）の分類が正しいことを確認

**期待結果**:
- PackagesModel が正しい構造で返される
- パッケージが適切にカテゴリ分けされる
- ソート順が正しい（ラベル → パッケージ名）
- null の ApplicationInfo を持つパッケージが除外される

**実装すべきファイル**: `app/src/test/java/com/nagopy/android/aplin/domain/usecase/LoadPackagesUseCaseUnitTest.kt`

**テストケース詳細**:

```kotlin
class LoadPackagesUseCaseUnitTest {
    
    @Test
    fun execute_returnsCorrectPackagesModel() = runTest {
        // 基本的な動作確認
        // Given: モックリポジトリとユースケースの設定
        // When: execute()実行
        // Then: 正しいPackagesModelが返される
    }

    @Test
    fun execute_sortsPackagesByLabelAndName() = runTest {
        // ソート動作の確認
        // Given: 異なるラベルを持つパッケージ群
        // When: execute()実行
        // Then: ラベル→パッケージ名順でソートされる
    }

    @Test
    fun execute_handlesEmptyRepository() = runTest {
        // 空データの処理確認
        // Given: 空のリポジトリ
        // When: execute()実行  
        // Then: 空のPackagesModelが返される
    }

    @Test
    fun execute_filtersDisabledPackagesCorrectly() = runTest {
        // 無効パッケージの分類確認
        // Given: 有効・無効パッケージが混在
        // When: execute()実行
        // Then: disabledPackagesに無効パッケージのみ含まれる
    }

    @Test
    fun execute_handlesNullApplicationInfo() = runTest {
        // null ApplicationInfo の処理確認
        // Given: applicationInfo が null のパッケージを含む
        // When: execute()実行
        // Then: null のものは除外される
    }

    @Test
    fun execute_categorizesPackagesCorrectly() = runTest {
        // カテゴリ分けの確認
        // Given: 様々なタイプのパッケージ
        // When: execute()実行
        // Then: 各カテゴリに適切に分類される
    }
}
```

**推定工数**: 6時間
- 環境設定: 1時間
- 基本テストケース実装: 3時間
- エッジケース・例外処理テスト: 2時間

---

### TC-006: MainViewModel 状態管理テスト ❌

**テスト目的**: UI状態管理とユーザーアクションハンドリングの検証

**事前条件**:
- AndroidX Test ライブラリが設定済み
- コルーチンテスト環境が設定済み
- 必要な依存関係のモックが作成済み

**テスト手順**:
1. ViewModel インスタンスを作成
2. 初期状態の確認
3. パッケージ更新操作の実行と状態変化の確認
4. ユーザーアクション（検索、ソート、パッケージ操作）の確認
5. エラーハンドリングの確認

**期待結果**:
- 初期状態が正しく設定される
- パッケージロード時に適切に状態が更新される
- ソート順変更時に再ソートが実行される
- ユーザーアクションが適切に Intent に変換される

**実装すべきファイル**: `app/src/test/java/com/nagopy/android/aplin/ui/main/MainViewModelTest.kt`

**テストケース詳細**:

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun initialState_isCorrect() {
        // Given: ViewModelが作成される
        // When: 初期状態を確認
        // Then: isLoading=false, packagesModel=null
    }

    @Test
    fun updatePackages_loadsAndUpdatesState() = runTest {
        // Given: UseCase がパッケージデータを返すよう設定
        // When: updatePackages()実行
        // Then: 状態が適切に更新される
    }

    @Test
    fun sortOrderChange_triggersResorting() = runTest {
        // Given: パッケージが既にロード済み
        // When: ソート順が変更される
        // Then: パッケージが新しい順序で再ソートされる
    }

    @Test
    fun searchText_filtersPackages() = runTest {
        // Given: パッケージリストが存在
        // When: 検索テキストが入力される
        // Then: フィルタされたリストが表示される
    }

    @Test
    fun uninstallPackage_createsCorrectIntent() {
        // Given: パッケージ名が指定される
        // When: uninstallPackage()実行
        // Then: 正しいアンインストールIntentが作成される
    }

    @Test
    fun sharePackage_createsShareIntent() {
        // Given: PackageModelが指定される
        // When: sharePackage()実行
        // Then: 共有Intentが作成される
    }

    @Test
    fun enableDisablePackage_opensSettings() {
        // Given: パッケージ名が指定される
        // When: enableDisablePackage()実行
        // Then: アプリ詳細設定が開かれる
    }

    @Test
    fun errorHandling_updatesStateCorrectly() = runTest {
        // Given: UseCase が例外をスローするよう設定
        // When: updatePackages()実行
        // Then: エラー状態が適切に設定される
    }
}
```

**推定工数**: 8時間
- ViewModelテスト環境設定: 2時間
- 基本状態管理テスト: 3時間
- ユーザーアクションテスト: 2時間
- 例外処理・エッジケーステスト: 1時間

---

### TC-007: PackageRepositoryImpl データアクセステスト ❌

**テスト目的**: Android PackageManager を使用したデータアクセス層の動作検証

**事前条件**:
- PackageManager のモックが作成済み
- 必要な Android framework クラスのモックが準備済み

**テスト手順**:
1. 各メソッドが PackageManager の適切な API を呼び出すことを確認
2. 戻り値の変換処理が正しく動作することを確認
3. 例外処理が適切に行われることを確認
4. null 値や異常なデータの処理を確認

**期待結果**:
- PackageManager API が正しいパラメータで呼び出される
- データ変換が正確に行われる
- 例外が適切にハンドリングされる
- パフォーマンスが許容範囲内

**実装すべきファイル**: `app/src/test/java/com/nagopy/android/aplin/data/repository/PackageRepositoryImplUnitTest.kt`

**テストケース詳細**:

```kotlin
class PackageRepositoryImplUnitTest {
    
    @Test
    fun loadAll_callsCorrectPackageManagerMethod() {
        // Given: PackageManager モックが設定済み
        // When: loadAll()実行
        // Then: getInstalledPackages(GET_META_DATA)が呼ばれる
    }

    @Test
    fun loadLabel_returnsApplicationLabel() {
        // Given: ApplicationInfo とモック PackageManager
        // When: loadLabel()実行
        // Then: getApplicationLabel()の結果が返される
    }

    @Test
    fun loadIcon_returnsApplicationIcon() {
        // Given: ApplicationInfo とモック PackageManager
        // When: loadIcon()実行
        // Then: getApplicationIcon()の結果が返される
    }

    @Test
    fun loadHomePackageNames_queriesHomeActivities() {
        // Given: HOME Intent に対するResolveInfo群
        // When: loadHomePackageNames()実行
        // Then: queryIntentActivitiesが適切に呼ばれ、パッケージ名セットが返される
    }

    @Test
    fun loadCurrentDefaultHomePackageName_returnsDefault() {
        // Given: デフォルトランチャーのResolveInfo
        // When: loadCurrentDefaultHomePackageName()実行
        // Then: resolveActivity()が呼ばれ、デフォルトパッケージ名が返される
    }

    @Test
    fun loadCurrentDefaultHomePackageName_withNoDefault_returnsNull() {
        // Given: デフォルトランチャーが設定されていない
        // When: loadCurrentDefaultHomePackageName()実行
        // Then: nullが返される
    }

    @Test
    fun systemProperties_returnCorrectValues() {
        // Given: システムプロパティ群
        // When: 各プロパティにアクセス
        // Then: 正しい値が返される
    }

    @Test
    fun exceptionHandling_handlesPackageManagerExceptions() {
        // Given: PackageManager が例外をスロー
        // When: 各メソッド実行
        // Then: 適切に例外処理される
    }
}
```

**推定工数**: 6時間
- モック環境設定: 2時間
- 基本データアクセステスト: 3時間
- 例外処理・エッジケーステスト: 1時間

---

### TC-013: MainScreen Compose UIテスト ❌

**テスト目的**: メイン画面の Compose UI コンポーネントの動作検証

**事前条件**:
- Compose Test Rule が設定済み
- テーマとリソースが利用可能
- テスト用のデータモデルが準備済み

**テスト手順**:
1. 各状態（ローディング、データ表示、エラー）でのレンダリング確認
2. ユーザーインタラクション（タップ、ロングクリック、スワイプ）の動作確認
3. カテゴリ切替の動作確認
4. 検索機能の動作確認
5. アクセシビリティの確認

**期待結果**:
- すべての状態で適切にレンダリングされる
- ユーザーインタラクションが正しく処理される
- 状態変化に応じてUIが更新される
- アクセシビリティ要件を満たす

**実装すべきファイル**: `app/src/androidTest/java/com/nagopy/android/aplin/ui/main/MainScreenTest.kt`

**テストケース詳細**:

```kotlin
@RunWith(AndroidJUnit4::class)
class MainScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun mainScreen_displaysLoadingState() {
        // Given: ローディング状態
        // When: MainScreen レンダリング
        // Then: ローディングインジケーターが表示される
    }

    @Test
    fun mainScreen_displaysPackageList() {
        // Given: パッケージデータ
        // When: MainScreen レンダリング
        // Then: パッケージリストが表示される
    }

    @Test
    fun mainScreen_categorySelection_worksCorrectly() {
        // Given: 複数カテゴリのデータ
        // When: カテゴリタブをタップ
        // Then: 対応するパッケージが表示される
    }

    @Test
    fun packageItem_longClick_showsContextMenu() {
        // Given: パッケージアイテム
        // When: ロングクリック
        // Then: コンテキストメニューが表示される
    }

    @Test
    fun searchWidget_filtersResults() {
        // Given: 検索可能なパッケージリスト
        // When: 検索テキスト入力
        // Then: フィルタされた結果が表示される
    }

    @Test
    fun mainScreen_handlesEmptyState() {
        // Given: 空のパッケージリスト
        // When: MainScreen レンダリング
        // Then: 空状態メッセージが表示される
    }

    @Test
    fun mainScreen_accessibility_labelsCorrect() {
        // Given: アクセシビリティ対応画面
        // When: セマンティクス解析
        // Then: 適切なコンテンツ記述がある
    }

    @Test
    fun packageActions_triggerCorrectCallbacks() {
        // Given: パッケージアクションUI
        // When: 各アクション実行
        // Then: 対応するコールバックが呼ばれる
    }
}
```

**推定工数**: 5時間
- Compose テスト環境設定: 1時間
- 基本レンダリングテスト: 2時間
- インタラクションテスト: 1.5時間
- アクセシビリティテスト: 0.5時間

---

### TC-020: 完全ユーザーフローE2Eテスト ❌

**テスト目的**: アプリ起動から主要機能利用までの完全なユーザージャーニーの検証

**事前条件**:
- 実機またはエミュレータが利用可能
- テスト用アプリがインストール済み
- 必要な権限が付与済み

**テスト手順**:
1. アプリを起動
2. パッケージリストの表示確認
3. カテゴリ切替の動作確認
4. 検索機能の動作確認
5. パッケージ詳細アクセスの確認
6. 設定画面の動作確認
7. アプリ終了まで

**期待結果**:
- すべての主要機能が正常に動作する
- パフォーマンスが許容範囲内
- クラッシュやANRが発生しない
- ユーザビリティが良好

**実装すべきファイル**: `app/src/androidTest/java/com/nagopy/android/aplin/E2EUserFlowTest.kt`

**テストシナリオ**:

```kotlin
@RunWith(AndroidJUnit4::class)
class E2EUserFlowTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun completeUserFlow_launchToPackageAction() {
        // シナリオ: 新規ユーザーが主要機能を一通り使用
        
        // 1. アプリ起動
        composeTestRule.waitForIdle()
        
        // 2. 初期画面確認
        composeTestRule.onNodeWithTag("package_list").assertIsDisplayed()
        
        // 3. カテゴリ切替
        composeTestRule.onNodeWithText("無効化可能").performClick()
        composeTestRule.waitForIdle()
        
        // 4. 検索実行
        composeTestRule.onNodeWithTag("search_button").performClick()
        composeTestRule.onNodeWithTag("search_field").performTextInput("設定")
        composeTestRule.waitForIdle()
        
        // 5. パッケージ選択
        composeTestRule.onAllNodesWithTag("package_item")
            .onFirst()
            .performLongClick()
        
        // 6. コンテキストメニュー操作
        composeTestRule.onNodeWithText("設定").performClick()
        
        // 7. 結果確認
        // システム設定画面が開かれることを確認（外部アプリのため間接的に確認）
    }

    @Test  
    fun searchAndFilterFlow_worksEndToEnd() {
        // シナリオ: 検索機能を詳細に検証
        
        composeTestRule.onNodeWithTag("search_button").performClick()
        composeTestRule.onNodeWithTag("search_field").performTextInput("android")
        composeTestRule.waitForIdle()
        
        // フィルタ結果確認
        composeTestRule.onAllNodesWithTag("package_item").fetchSemanticsNodes()
            .forEach { node ->
                val text = node.config.getOrNull(SemanticsProperties.Text)?.joinToString() ?: ""
                assertTrue(text.contains("android", ignoreCase = true))
            }
    }

    @Test
    fun categoryNavigationFlow_maintainsState() {
        // シナリオ: カテゴリ間の状態維持確認
        
        val categories = listOf("すべて", "無効化可能", "無効", "ユーザー")
        
        categories.forEach { category ->
            composeTestRule.onNodeWithText(category).performClick()
            composeTestRule.waitForIdle()
            
            // カテゴリが選択状態になる
            composeTestRule.onNodeWithText(category).assertIsSelected()
            
            // 対応するコンテンツが表示される
            composeTestRule.onNodeWithTag("package_list").assertIsDisplayed()
        }
    }

    @Test
    fun performanceFlow_respondsWithinTimeLimit() {
        // シナリオ: レスポンス性能の確認
        
        val startTime = System.currentTimeMillis()
        
        // 大量のパッケージが存在する状況での操作
        composeTestRule.onNodeWithText("すべて").performClick()
        composeTestRule.waitForIdle()
        
        val loadTime = System.currentTimeMillis() - startTime
        assertTrue("Package list should load within 3 seconds", loadTime < 3000)
        
        // 検索性能確認
        val searchStartTime = System.currentTimeMillis()
        composeTestRule.onNodeWithTag("search_button").performClick()
        composeTestRule.onNodeWithTag("search_field").performTextInput("test")
        composeTestRule.waitForIdle()
        
        val searchTime = System.currentTimeMillis() - searchStartTime
        assertTrue("Search should respond within 1 second", searchTime < 1000)
    }

    @Test
    fun errorRecoveryFlow_handlesGracefully() {
        // シナリオ: エラー状況からの回復確認
        
        // ネットワーク切断等のエラー状況をシミュレート（可能な範囲で）
        // アプリが適切にエラー表示し、回復可能であることを確認
        
        // 更新操作でのエラー回復
        composeTestRule.onNodeWithTag("refresh_button", useUnmergedTree = true)
            .assertExists()
            .performClick()
        
        composeTestRule.waitForIdle()
        
        // エラー状態でも基本的な UI は維持される
        composeTestRule.onNodeWithTag("main_screen").assertIsDisplayed()
    }
}
```

**推定工数**: 8時間
- E2E テスト環境設定: 2時間
- 基本フローテスト実装: 3時間
- 詳細シナリオテスト: 2時間
- パフォーマンス・エラー処理テスト: 1時間

---

### TC-024: 大量データ処理性能テスト ❌

**テスト目的**: 多数のパッケージが存在する環境での性能検証

**事前条件**:
- パフォーマンス測定環境が準備済み
- 大量のテストデータが作成可能
- メモリ使用量監視ツールが利用可能

**テスト手順**:
1. 1000個以上のパッケージデータを生成
2. LoadPackagesUseCase の実行時間測定
3. メモリ使用量の監視
4. UI レンダリング性能の測定
5. 検索機能の性能測定

**期待結果**:
- パッケージロードが5秒以内に完了
- メモリ使用量が50MB以下
- UI が1秒以内に応答
- 検索結果が0.5秒以内に表示

**実装すべきファイル**: `app/src/test/java/com/nagopy/android/aplin/performance/PerformanceTest.kt`

**テストケース詳細**:

```kotlin
class PerformanceTest {
    
    @Test(timeout = 5000)
    fun loadPackages_withManyPackages_completesWithinTimeLimit() = runTest {
        // Given: 1000個のパッケージデータ
        val manyPackages = (1..1000).map { 
            createMockPackageInfo("com.app$it", "App $it")
        }
        
        setupMocks(manyPackages)

        // When: パッケージロード実行
        val startTime = System.currentTimeMillis()
        val result = loadPackagesUseCase.execute()
        val endTime = System.currentTimeMillis()

        // Then: 制限時間内完了 & 全データ処理完了
        assertTrue("Should complete within 5 seconds", endTime - startTime < 5000)
        assertEquals(1000, result.allPackages.size)
    }

    @Test
    fun packageSorting_withLargeDataset_maintainsPerformance() {
        // 大量データのソート性能テスト
        val packages = (1..10000).map {
            createMockPackageModel("com.app$it", "App ${Random.nextInt()}")
        }

        val startTime = System.currentTimeMillis()
        val sorted = packages.sortedWith(compareBy({ it.label }, { it.packageName }))
        val endTime = System.currentTimeMillis()

        assertTrue("Sorting should complete within 1 second", endTime - startTime < 1000)
        assertTrue("Sort order should be maintained", 
            sorted.zipWithNext().all { it.first.label <= it.second.label })
    }

    @Test
    fun memoryUsage_staysWithinLimits() = runTest {
        // メモリ使用量テスト
        val runtime = Runtime.getRuntime()
        val initialMemory = runtime.totalMemory() - runtime.freeMemory()
        
        // 複数回のロード処理でメモリリークがないことを確認
        repeat(100) {
            loadPackagesUseCase.execute()
            if (it % 10 == 0) {
                System.gc()
                Thread.sleep(100)
            }
        }
        
        System.gc()
        Thread.sleep(500)
        val finalMemory = runtime.totalMemory() - runtime.freeMemory()
        
        val memoryIncrease = finalMemory - initialMemory
        val maxAllowedIncrease = 50 * 1024 * 1024 // 50MB
        
        assertTrue("Memory should not increase significantly", 
            memoryIncrease < maxAllowedIncrease)
    }

    @Test
    fun searchPerformance_respondsQuickly() {
        // 検索機能の性能テスト  
        val largePackageList = (1..5000).map {
            createMockPackageModel("com.package$it", "Package Name $it")
        }
        
        val searchTerm = "Package Name 1"
        
        val startTime = System.currentTimeMillis()
        val filteredResults = largePackageList.filter { 
            it.label.contains(searchTerm, ignoreCase = true) ||
            it.packageName.contains(searchTerm, ignoreCase = true)
        }
        val endTime = System.currentTimeMillis()
        
        assertTrue("Search should complete within 500ms", endTime - startTime < 500)
        assertTrue("Should find expected results", filteredResults.isNotEmpty())
    }
}
```

**推定工数**: 4時間
- パフォーマンステスト環境設定: 1時間
- 基本性能測定テスト: 2時間
- メモリ・詳細性能テスト: 1時間

---

## 実装優先順位マトリクス

### 高優先度（今すぐ実装）

| テストID | テスト名 | 影響度 | 緊急度 | リスク軽減効果 |
|---------|----------|--------|--------|---------------|
| TC-005 | LoadPackagesUseCase 単体テスト | 高 | 高 | 高 |
| TC-006 | MainViewModel 状態管理 | 高 | 高 | 高 |
| TC-013 | MainScreen Compose UI | 高 | 中 | 中 |
| TC-009 | Repository + UseCase 統合 | 高 | 中 | 高 |

### 中優先度（次のスプリント）

| テストID | テスト名 | 影響度 | 緊急度 | リスク軽減効果 |
|---------|----------|--------|--------|---------------|
| TC-007 | PackageRepositoryImpl データアクセス | 中 | 高 | 高 |
| TC-020 | 完全ユーザーフローE2E | 中 | 中 | 高 |
| TC-008 | UserDataStore 設定管理 | 中 | 中 | 中 |
| TC-024 | 大量データ処理性能 | 中 | 中 | 中 |

### 低優先度（継続的改善）

| テストID | テスト名 | 影響度 | 緊急度 | リスク軽減効果 |
|---------|----------|--------|--------|---------------|
| TC-014-018 | UI コンポーネント群 | 低 | 低 | 中 |
| TC-025-026 | パフォーマンス詳細 | 低 | 低 | 低 |
| TC-027-028 | セキュリティ | 低 | 低 | 中 |

## 週次実装計画

### 第1週: コアロジック単体テスト
- **TC-005**: LoadPackagesUseCase 単体テスト（6h）
- **TC-006**: MainViewModel 状態管理テスト（8h）
- **合計**: 14時間

### 第2週: データ層・UI基盤テスト  
- **TC-007**: PackageRepositoryImpl 単体テスト（6h）
- **TC-013**: MainScreen Compose UI テスト（5h）
- **TC-008**: UserDataStore テスト（4h）
- **合計**: 15時間

### 第3週: 統合・E2Eテスト
- **TC-009**: Repository + UseCase 統合テスト（4h）
- **TC-020**: 完全ユーザーフローE2E（8h）
- **TC-024**: 大量データ処理性能テスト（4h）
- **合計**: 16時間

### 第4週: 詳細UI・パフォーマンステスト
- **TC-014-016**: UI コンポーネントテスト（10h）
- **TC-025**: メモリリークテスト（3h）
- **TC-027**: セキュリティテスト（3h）
- **合計**: 16時間

## 期待される成果

### カバレッジ向上
- **現在**: 10% (5テストファイル)
- **第1週後**: 35% (+6テストファイル)
- **第2週後**: 55% (+8テストファイル) 
- **第3週後**: 70% (+6テストファイル)
- **第4週後**: 80% (+8テストファイル)

### 品質指標改善
- **バグ検出率**: 現在0% → 80%（リリース前検出）
- **リファクタリング安全性**: 現在低 → 高
- **新機能開発速度**: 現在100% → 120%（安心感向上）
- **保守性**: 現在中 → 高（動作保証）

### 開発プロセス改善
- **CI/CD**: 自動テスト実行による品質ゲート設置
- **コードレビュー**: テスト観点での品質確認
- **ドキュメント**: テストケースが仕様書代替
- **新人オンボーディング**: テストコードから仕様理解促進

## 継続的改善計画

### 月次レビュー項目
1. **テストカバレッジ分析**: 新規コード・既存コードの網羅状況
2. **テスト実行時間**: CI/CD パフォーマンスへの影響
3. **テスト保守コスト**: テストコード修正頻度・工数
4. **品質指標**: バグ検出率・回帰率・顧客満足度

### 改善アクション
1. **カバレッジ不足領域**: 優先順位を見直して追加テスト実装
2. **テスト実行時間超過**: 不要テスト除去・並列実行導入
3. **保守コスト高**: テストコード設計見直し・共通化推進
4. **品質目標未達**: テスト観点追加・品質基準見直し

このテストケース一覧により、Aplin プロジェクトの品質向上と持続可能な開発体制の構築を目指します。