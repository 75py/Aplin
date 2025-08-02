# API仕様書（逆生成）

## ベースURL
`android://`

## 認証方式
Android Permissions

## エンドポイント一覧

### INTENT ))
        if (activity.isInMultiWindowMode) {
            intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        try {
            activity.startActivity(intent)
        } catch (e: Exception) {
            logcat { 
**説明**: Android Intent action

**レスポンス**: `void`

## レスポンス共通形式

### 成功レスポンス
```
Intent result OK
```

### エラーレスポンス
```
Intent result ERROR
```
