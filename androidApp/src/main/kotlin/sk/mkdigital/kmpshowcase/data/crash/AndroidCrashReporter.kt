package sk.mkdigital.kmpshowcase.data.crash

import com.google.firebase.crashlytics.FirebaseCrashlytics

class AndroidCrashReporter(
    private val crashlytics: FirebaseCrashlytics
) : CrashReporter {

    override fun recordException(throwable: Throwable) {
        crashlytics.recordException(throwable)
    }

    override fun log(message: String) {
        crashlytics.log(message)
    }
}
