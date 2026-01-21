package mk.digital.kmpshowcase

import android.app.Application
import mk.digital.kmpshowcase.di.commonModule
import mk.digital.kmpshowcase.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    private fun initKoin() {
        initKoin {
            androidLogger()
            androidContext(this@App)
            modules(commonModule())
        }
    }
}
