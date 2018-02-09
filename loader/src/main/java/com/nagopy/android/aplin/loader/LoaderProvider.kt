package com.nagopy.android.aplin.loader

import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.support.v4.content.res.ResourcesCompat
import com.github.salomonbrys.kodein.*
import com.github.salomonbrys.kodein.conf.ConfigurableKodein
import com.nagopy.android.aplin.loader.internal.AplinDevicePolicyManager
import com.nagopy.android.aplin.loader.internal.InternalAppLoader
import com.nagopy.android.aplin.loader.internal.PackageNamesLoader
import com.nagopy.android.aplin.loader.internal.ShellCmd

class LoaderProvider(context: Context) {

    private val injector = KodeinInjector().apply {
        val module = Kodein.Module {
            // external
            bind<AppLoader>() with singleton { AppLoader(instance()) }
            bind<IconLoader>() with singleton { IconLoader(instance(), instance("defaultIcon")) }

            // internal (Android SDK)
            bind<Resources>() with singleton { context.resources }
            bind<PackageManager>() with singleton { context.packageManager }
            bind<DevicePolicyManager>() with singleton { context.getSystemService(DevicePolicyManager::class.java) }

            // internal
            bind<Drawable>("defaultIcon") with singleton { ResourcesCompat.getDrawable(instance(), android.R.drawable.sym_def_app_icon, null)!! }
            bind<InternalAppLoader>() with singleton { InternalAppLoader(instance(), instance(), instance()) }
            bind<ShellCmd>() with singleton { ShellCmd() }
            bind<AplinDevicePolicyManager>() with singleton { AplinDevicePolicyManager(instance(), instance()) }
            bind<PackageNamesLoader>() with singleton { PackageNamesLoader(instance()) }
        }

        val kodein = ConfigurableKodein(mutable = true).apply {
            addImport(module, true)
        }

        inject(kodein)
    }

    val appLoader: AppLoader by injector.instance()

    val iconLoader: IconLoader by injector.instance()

}
