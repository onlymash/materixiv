package onlymash.materixiv.app

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import onlymash.materixiv.crash.CrashHandler
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.api.PixivOauthApi
import onlymash.materixiv.network.pixiv.createApi
import onlymash.materixiv.data.db.MyDatabase
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider
import org.kodein.di.erased.singleton

class App : Application(), KodeinAware {

    companion object {
        lateinit var app: App
    }

    override val kodein: Kodein by Kodein.lazy {
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