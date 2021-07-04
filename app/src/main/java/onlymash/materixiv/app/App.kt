package onlymash.materixiv.app

import android.app.Application
import org.kodein.di.*

class App : Application(), DIAware {

    companion object {
        lateinit var app: App
    }

    override val di by DI.lazy {
        import(appModule(this@App))
    }

    override fun onCreate() {
        super.onCreate()
        app = this
    }
}