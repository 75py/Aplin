package com.nagopy.android.aplin

import android.app.Application
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
open class ApplicationModule(val application: Application) {

    @Singleton
    @Provides
    open fun provideApplication(): Application = application

}