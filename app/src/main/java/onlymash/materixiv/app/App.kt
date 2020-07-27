package onlymash.materixiv.app

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import onlymash.materixiv.crash.CrashHandler
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.api.PixivOauthApi
import onlymash.materixiv.network.pixiv.createApi
import onlymash.materixiv.data.db.MyDatabase
import org.kodein.di.*

class App : Application(), DIAware {

    companion object {
        lateinit var app: App
    }

    override val di by DI.lazy {
        bind<Context>() with instance(this@App)
        bind<SharedPreferences>() with provider {
            instance<Context>().getSharedPreferences(Values.PREFERENCE_NAME_SETTINGS, Context.MODE_PRIVATE)
        }
        bind() from singleton { MyDatabase(instance()) }
        bind() from singleton { instance<MyDatabase>().tokenDao() }
        bind() from singleton { instance<MyDatabase>().illustDao() }
        bind() from singleton { instance<MyDatabase>().downloadDao() }
        bind() from singleton { createApi<PixivAppApi>() }
        bind() from singleton { createApi<PixivOauthApi>() }
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        CrashHandler.getInstance().init(this)
    }
}