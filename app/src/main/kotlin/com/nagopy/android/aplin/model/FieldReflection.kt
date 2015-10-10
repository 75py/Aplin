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

import java.lang.reflect.Field

/**
 * リフレクションでフィールドを取得する際、try-catchを書かずに済ませるためのラッパークラス.
 * 例外が発生した場合、FieldReflectionExceptionをスローする。
 * FieldReflectionException は RuntimeExceptionのサブクラスなので、失敗した場合にアプリを強制終了させて良い場合、特に例外を捕捉する必要はない。
 */
public class FieldReflection<T>
/**
 * コンストラクタ

 * @param cls       対象クラス
 * *
 * @param fieldName 対象フィールド名
 * *
 * @throws FieldReflectionException リフレクションに失敗した場合
 */
@Throws(FieldReflection.FieldReflectionException::class)
constructor(cls: Class<*>, fieldName: String) {

    val field: Field

    init {
        try {
            field = cls.getDeclaredField(fieldName)
            field.isAccessible = true
        } catch (e: NoSuchFieldException) {
            throw FieldReflectionException("フィールドの取得に失敗", e)
        }
    }

    /**
     * 値を取得する
     * @param obj [Field.getInt] の引数
     * *
     * @return フィールド値
     * *
     * @throws FieldReflectionException リフレクションに失敗した場合
     */
    @Throws(FieldReflectionException::class)
    public fun get(obj: Any): T {
        try {
            @Suppress("UNCHECKED_CAST")
            return field.get(obj) as T
        } catch (e: IllegalAccessException) {
            throw FieldReflectionException("フィールドの値の取得に失敗", e)
        }

    }

    /**
     * FieldReflectionクラスでリフレクションによる例外が発生した場合
     */
    public class FieldReflectionException
    /**
     * コンストラクタ

     * @param detailMessage メッセージ
     * *
     * @param e             Exception
     */
    (detailMessage: String, e: Exception) : RuntimeException(detailMessage, e) {
    }

}
