package mk.digital.kmpshowcase

import android.app.Application
import com.google.firebase.FirebaseApp

import mk.digital.kmpshowcase.di.androidAppModule
import mk.digital.kmpshowcase.di.commonModule
import mk.digital.kmpshowcase.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class App : Application() {

    private val appConfig by lazy {
        AppConfig(
            buildType = if (BuildConfig.DEBUG) BuildType.DEBUG else BuildType.RELEASE,
            versionName = BuildConfig.VERSION_NAME,
            versionCode = BuildConfig.VERSION_CODE.toString()
        )
    }

    override fun onCreate() {
        super.onCreate()
        initKoin()
        initFb()
    }

    private fun initKoin() {
        initKoin(appConfig) {
            androidLogger()
            androidContext(this@App)
            modules(commonModule(appConfig), androidAppModule)
        }
    }

    private fun initFb() {
        FirebaseApp.initializeApp(this)
        initFBAppCheck()
    }

    private fun initFBAppCheck() {
        AppCheckInitializer.init()
    }
}
