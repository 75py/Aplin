package com.nagopy.android.aplin.model

import com.nagopy.android.aplin.entity.AppEntity
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

class SortTest {

    lateinit var app1: AppEntity

    lateinit var app2: AppEntity

    @Before
    fun setup() {
        app1 = AppEntity()
        app2 = AppEntity()
    }

    @Test
    @Throws(Exception::class)
    fun defaultSort() {
        app1.isInstalled = true
        app2.isInstalled = false
        assertTrue(Sort.DEFAULT.compare(app1, app2) < 0)

        app1.isInstalled = false
        app2.isInstalled = true
        assertTrue(Sort.DEFAULT.compare(app2, app1) < 0)

        app1.isInstalled = app2.isInstalled
        app1.label = "a";
        app2.label = "b";
        assertTrue(Sort.DEFAULT.compare(app1, app2) < 0)

        app1.label = "a";
        app2.label = "0";
        assertTrue(Sort.DEFAULT.compare(app2, app1) < 0)

        app1.label = app2.label
        app1.packageName = "a"
        app2.packageName = "b"
        assertTrue(Sort.DEFAULT.compare(app1, app2) < 0)
    }

    @Test
    @Throws(Exception::class)
    fun packageName() {
        app1.isInstalled = true
        app2.isInstalled = false
        assertTrue(Sort.PACKAGE_NAME.compare(app1, app2) < 0)

        app1.isInstalled = false
        app2.isInstalled = true
        assertTrue(Sort.PACKAGE_NAME.compare(app2, app1) < 0)

        app1.isInstalled = app2.isInstalled
        app1.packageName = "a";
        app2.packageName = "b";
        assertTrue(Sort.PACKAGE_NAME.compare(app1, app2) < 0)

        app1.packageName = "a";
        app2.packageName = "0";
        assertTrue(Sort.PACKAGE_NAME.compare(app2, app1) < 0)

        // これは通常ありえない
        app1.packageName = app2.packageName
        assertTrue(Sort.PACKAGE_NAME.compare(app1, app2) == 0)
    }


    @Test
    @Throws(Exception::class)
    fun updateDateDesc() {
        app1.lastUpdateTime = 1
        app2.lastUpdateTime = 0
        assertTrue(Sort.UPDATE_DATE_DESC.compare(app1, app2) < 0)

        app1.lastUpdateTime = 0
        app2.lastUpdateTime = 1
        assertTrue(Sort.UPDATE_DATE_DESC.compare(app2, app1) < 0)

        app1.lastUpdateTime = app2.lastUpdateTime
        // 以降はデフォルトと同じ

        app1.isInstalled = true
        app2.isInstalled = false
        assertTrue(Sort.UPDATE_DATE_DESC.compare(app1, app2) < 0)

        app1.isInstalled = false
        app2.isInstalled = true
        assertTrue(Sort.UPDATE_DATE_DESC.compare(app2, app1) < 0)

        app1.isInstalled = app2.isInstalled
        app1.label = "a";
        app2.label = "b";
        assertTrue(Sort.UPDATE_DATE_DESC.compare(app1, app2) < 0)

        app1.label = "a";
        app2.label = "0";
        assertTrue(Sort.UPDATE_DATE_DESC.compare(app2, app1) < 0)

        app1.label = app2.label
        app1.packageName = "a"
        app2.packageName = "b"
        assertTrue(Sort.UPDATE_DATE_DESC.compare(app1, app2) < 0)
    }
}
