# Aplin データフロー図（逆生成）

## 分析日時
2025-08-02T09:20:00Z

## システムデータフロー概要

Aplinは Android パッケージ管理アプリケーションとして、システムAPIからアプリケーション情報を取得し、ユーザーフレンドリーなUI で表示する一方向データフローを採用しています。

## メインデータフロー

### アプリケーション起動〜表示フロー

```mermaid
sequenceDiagram
    participant U as ユーザー
    participant A as MainActivity
    participant VM as MainViewModel
    participant UC as LoadPackagesUseCase
    participant CUC as CategorizePackageUseCase
    participant PR as PackageRepository
    participant DPR as DevicePolicyRepository
    participant PM as PackageManager
    participant DPM as DevicePolicyManager
    participant UI as Compose UI

    U->>A: アプリ起動
    A->>VM: viewModel初期化
    VM->>UC: loadPackagesUseCase.execute()
    
    par 並行データ取得
        UC->>PR: loadAll()
        PR->>PM: queryIntentActivities()
        PM-->>PR: List<PackageInfo>
    and
        UC->>PR: loadHomePackageNames()
        PR->>PM: getHomeActivities()
        PM-->>PR: Set<String>
    and
        UC->>DPR: isDeviceManaged()
        DPR->>DPM: isDeviceOwnerApp()
        DPM-->>DPR: Boolean
    end
    
    PR-->>UC: パッケージデータ
    DPR-->>UC: デバイス管理情報
    
    UC->>CUC: categorizePackage()
    CUC-->>UC: カテゴリ分類結果
    
    UC-->>VM: PackagesModel
    VM-->>UI: MainUiState更新
    UI-->>U: アプリリスト表示
```

### データ変換フロー

```mermaid
flowchart TD
    A[Android PackageManager] --> B[PackageInfo List]
    B --> C[PackageRepository]
    C --> D[PackageModel変換]
    D --> E[CategorizePackageUseCase]
    E --> F{カテゴリ判定}
    
    F -->|システムアプリ| G[無効化可能]
    F -->|ユーザーアプリ| H[ユーザーアプリ]
    F -->|無効化済み| I[無効化済み]
    F -->|全て| J[全アプリ]
    
    G --> K[PackagesModel]
    H --> K
    I --> K
    J --> K
    
    K --> L[SortOrder適用]
    L --> M[MainUiState]
    M --> N[Compose UI]
    N --> O[ユーザー表示]
```

### 検索機能データフロー

```mermaid
flowchart LR
    A[ユーザー入力] --> B[SearchTextField]
    B --> C[MainViewModel.updateSearchTextState]
    C --> D[MainUiState.searchText更新]
    D --> E[Compose recomposition]
    E --> F[フィルタリング実行]
    F --> G[マッチング結果表示]
    
    F --> H{検索対象}
    H -->|アプリ名| I[PackageModel.label]
    H -->|パッケージ名| J[PackageModel.packageName]
    
    I --> K[部分一致フィルタ]
    J --> K
    K --> G
```

## 設定データフロー

### ユーザー設定の永続化フロー

```mermaid
sequenceDiagram
    participant U as ユーザー
    participant PS as PreferenceScreen
    participant DS as DataStore
    participant VM as MainViewModel
    participant UI as MainScreen

    U->>PS: 設定変更
    PS->>DS: preferences更新
    DS->>DS: ファイル永続化
    
    DS->>VM: Flow emission
    VM->>VM: sortOrder変更
    VM->>VM: PackagesModel再ソート
    VM->>UI: StateFlow更新
    UI-->>U: 並び順反映
```

### ソート機能データフロー

```mermaid
flowchart TD
    A[ユーザー設定] --> B{SortOrder}
    B -->|LABEL| C[アプリ名順]
    B -->|PACKAGE_NAME| D[パッケージ名順]
    B -->|FIRST_INSTALL_TIME| E[インストール時刻順]
    B -->|LAST_UPDATE_TIME| F[更新時刻順]
    
    C --> G[compareBy label, packageName]
    D --> H[compareBy packageName, label]
    E --> I[compareBy firstInstallTime desc]
    F --> J[compareBy lastUpdateTime desc]
    
    G --> K[ソート済みList]
    H --> K
    I --> K
    J --> K
    
    K --> L[UI表示更新]
```

## 広告データフロー

### GDPR同意管理フロー

```mermaid
sequenceDiagram
    participant U as ユーザー
    participant A as Activity
    participant AVM as AdsViewModel
    participant UMP as UserMessagingPlatform
    participant SP as SharedPreferences
    participant AdV as AdView

    A->>AVM: init()
    AVM->>UMP: requestConsentInfoUpdate()
    UMP->>UMP: 地域判定・同意状態確認
    
    alt GDPR地域
        UMP->>U: 同意フォーム表示
        U->>UMP: 同意/拒否選択
        UMP->>SP: TCF文字列保存
    else 非GDPR地域
        UMP->>AVM: 同意不要
    end
    
    SP->>AVM: 同意状態読み取り
    AVM->>AVM: 広告表示可否判定
    
    AVM->>AdV: updateAds()
    
    alt パーソナライズ広告可
        AdV->>AdV: 通常広告リクエスト
    else 非パーソナライズのみ可
        AdV->>AdV: npa=1 広告リクエスト
    else 広告拒否
        AdV->>AdV: 広告非表示
    end
```

## エラーハンドリングフロー

```mermaid
flowchart TD
    A[処理実行] --> B{例外発生?}
    B -->|No| C[正常処理継続]
    B -->|Yes| D{例外種類}
    
    D -->|SecurityException| E[権限不足エラー]
    D -->|PackageManager.NameNotFoundException| F[パッケージ未発見]
    D -->|ActivityNotFoundException| G[Activity起動失敗]
    D -->|その他| H[予期しないエラー]
    
    E --> I[logcat ERROR出力]
    F --> I
    G --> I
    H --> I
    
    I --> J[UI状態維持]
    J --> K[ユーザーに影響最小限]
```

## 状態管理データフロー

### StateFlow ベース状態管理

```mermaid
flowchart LR
    A[ユーザーアクション] --> B[ViewModel]
    B --> C[MutableStateFlow.update]
    C --> D[StateFlow emission]
    D --> E[Compose State]
    E --> F[UI recomposition]
    F --> G[画面更新]
    
    B --> H[CoroutineScope.launch]
    H --> I[suspend function実行]
    I --> J[Repository呼び出し]
    J --> K[Android API実行]
    K --> L[結果取得]
    L --> C
```

### 画面遷移データフロー

```mermaid
stateDiagram-v2
    [*] --> Loading
    Loading --> MainScreen : データ読み込み完了
    MainScreen --> Settings : 設定画面遷移
    Settings --> MainScreen : 戻る
    MainScreen --> AppDetails : アプリ詳細
    AppDetails --> MainScreen : 戻る
    MainScreen --> WebSearch : ウェブ検索
    WebSearch --> MainScreen : ブラウザ起動後戻る
    MainScreen --> Loading : 更新実行
```

## データ永続化フロー

### DataStore Preferences

```mermaid
flowchart TD
    A[設定変更] --> B[PreferencesDataStore]
    B --> C[JSON形式変換]
    C --> D[ファイルシステム書き込み]
    D --> E[永続化完了]
    
    F[アプリ起動] --> G[DataStore読み込み]
    G --> H[Flow<Preferences>]
    H --> I[型安全変換]
    I --> J[ViewModel初期化]
    J --> K[UI状態復元]
```

### メモリキャッシュフロー

```mermaid
flowchart LR
    A[API呼び出し] --> B{キャッシュ存在?}
    B -->|Yes| C[キャッシュデータ返却]
    B -->|No| D[Android API実行]
    D --> E[結果取得]
    E --> F[StateFlow更新]
    F --> G[メモリキャッシュ]
    G --> H[UI表示]
    E --> H
```

## パフォーマンス最適化フロー

### 非同期処理による最適化

```mermaid
sequenceDiagram
    participant VM as ViewModel
    participant UC as UseCase
    participant R1 as PackageRepository
    participant R2 as DevicePolicyRepository
    participant A1 as PackageManager
    participant A2 as DevicePolicyManager

    VM->>UC: execute()
    
    par 並行実行
        UC->>R1: loadAll()
        R1->>A1: queryIntentActivities()
    and
        UC->>R1: loadHomePackageNames()
        R1->>A1: getHomeActivities()
    and
        UC->>R2: isDeviceManaged()
        R2->>A2: isDeviceOwnerApp()
    end
    
    A1-->>R1: データ1
    A1-->>R1: データ2
    A2-->>R2: データ3
    
    R1-->>UC: 結合結果
    R2-->>UC: 管理情報
    UC-->>VM: 最終結果
```

## リアクティブデータフロー

### Flow ベースのリアクティブプログラミング

```kotlin
// UserDataStore → ViewModel → UI
userDataStore.sortOrder
    .collect { newOrder ->
        _viewModelState.update { state ->
            state.copy(
                packagesModel = newOrder.sort(state.packagesModel),
                sortOrder = newOrder
            )
        }
    }
```

```mermaid
flowchart TD
    A[DataStore] --> B[Flow<SortOrder>]
    B --> C[collect in ViewModel]
    C --> D[StateFlow更新]
    D --> E[Compose collectAsState]
    E --> F[UI自動更新]
    
    G[設定画面での変更] --> H[DataStore書き込み]
    H --> B
```

## 総括

Aplinのデータフローは以下の特徴を持っています：

1. **一方向データフロー**: UI → ViewModel → UseCase → Repository → Android API
2. **リアクティブ設計**: Flow/StateFlow による自動UI更新
3. **並行処理最適化**: async/await による効率的なデータ取得
4. **エラー分離**: 各層での適切な例外ハンドリング
5. **メモリ効率**: StateFlow による最小限の状態保持

この設計により、Android システムAPIの複雑さを抽象化し、ユーザーフレンドリーなアプリケーション管理体験を提供しています。