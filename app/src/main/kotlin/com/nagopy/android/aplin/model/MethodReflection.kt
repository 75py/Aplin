/*
 * Copyright (C) 2015 75py
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nagopy.android.aplin.model

import java.lang.reflect.Method

/**
 * リフレクションでメソッドを取得する際、try-catchを書かずに済ませるためのラッパークラス.
 * 例外が発生した場合、MethodReflectionException をスローする。
 * MethodReflectionException は RuntimeExceptionのサブクラスなので、失敗した場合にアプリを強制終了させて良い場合、特に例外を捕捉する必要はない。
 */
public class MethodReflection<T>
/**
 * コンストラクタ

 * @param cls            対象クラス
 * *
 * @param methodName     対象メソッド名
 * *
 * @param parameterTypes パラメータ
 * *
 * @throws MethodReflectionException リフレクションに失敗した倍
 */
@Throws(MethodReflection.MethodReflectionException::class)
constructor(cls: Class<*>, methodName: String, vararg parameterTypes: Class<*>) {

    private val method: Method

    init {
        try {
            method = cls.getDeclaredMethod(methodName, *parameterTypes)
            method.isAccessible = true
        } catch (e: NoSuchMethodException) {
            throw MethodReflectionException("メソッドの取得に失敗", e)
        }

    }

    /**
     * メソッドを実行する

     * @param receiver [Method.invoke] 第一引数
     * *
     * @param args     　[Method.invoke]　第二引数
     * *
     * @return 実行結果
     * *
     * @throws MethodReflectionException リフレクションに失敗した倍
     */
    @Throws(MethodReflectionException::class)
    public fun invoke(receiver: Any?, vararg args: Any): T {
        try {
            @Suppress("UNCHECKED_CAST")
            return method.invoke(receiver, *args) as T
        } catch (e: Exception) {
            throw MethodReflectionException("メソッドの実行に失敗", e)
        }

    }

    /**
     * MethodReflectionクラスでリフレクションによる例外が発生した場合
     */
    public class MethodReflectionException
    /**
     * コンストラクタ

     * @param detailMessage メッセージ
     * *
     * @param e             Exception
     */
    (detailMessage: String, e: Exception) : RuntimeException(detailMessage, e) {
    }

}
