# データフロー図（逆生成）

## ユーザーインタラクションフロー

### Authentication Flow
```mermaid
sequenceDiagram
    participant U as User
    participant UI as UI Layer
    participant VM as ViewModel
    participant UC as UseCase
    participant R as Repository
    
    U->>UI: Input credentials
    UI->>VM: Submit form
    VM->>UC: Execute login
    UC->>R: Authenticate
    R-->>UC: Token
    UC-->>VM: Success
    VM-->>UI: Update state
    UI-->>U: Show success
```

## 状態管理フロー

### StateFlow + Compose
State flows from ViewModel to UI, events flow from UI to ViewModel

```mermaid
flowchart LR
    A[UI Component] --> B[ViewModel]
    B --> C[UseCase]
    C --> D[Repository]
    D --> E[Data Source]
    E --> D
    D --> C
    C --> B
    B --> A
```

## エラーハンドリングフロー

### 戦略
- Try-catch blocks
- Result wrapper classes
- Error states in UI

### パターン
- Centralized error handling
- User-friendly error messages
- Retry mechanisms

```mermaid
flowchart TD
    A[エラー発生] --> B{エラー種別}
    B -->|システムエラー| C[ログ出力]
    B -->|ユーザーエラー| D[UI通知]
    B -->|ネットワークエラー| E[リトライ機能]
    C --> F[アプリ継続]
    D --> F
    E --> F
```
