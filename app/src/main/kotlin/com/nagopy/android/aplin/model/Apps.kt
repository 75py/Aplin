package com.nagopy.android.aplin.model

import android.app.Application
import com.nagopy.android.aplin.entity.AppEntity
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.properties.Delegates


@Singleton
public class Apps {

    var context: Application by Delegates.notNull()
        @Inject set

    var appManager: AppManager by Delegates.notNull()
        @Inject set

    var cache: List<AppEntity>? = null

    @Inject
    constructor() {
    }

    /**
     * キャッシュを無効化する
     */
    public fun invalidateCache() {
        cache = null
    }

    public fun getAll(): Observable<AppEntity> {
        return Observable.create(Observable.OnSubscribe<AppEntity> { s ->
            try {
                val resultList = cache ?: appManager.getAll()
                cache = resultList
                resultList.forEach { app ->
                    s.onNext(app)
                }
                s.onCompleted()
            } catch (e: kotlin.Throwable) {
                s.onError(e)
            }
        })
    }

}