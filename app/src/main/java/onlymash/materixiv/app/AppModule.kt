package onlymash.materixiv.app

import android.content.Context
import android.content.SharedPreferences
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.api.PixivOauthApi
import onlymash.materixiv.data.db.MyDatabase
import onlymash.materixiv.network.pixiv.createApi
import org.kodein.di.*


fun appModule(applicationContext: Context) = DI.Module("AppModule") {
    bind<Context>() with singleton { applicationContext }
    bind<SharedPreferences>() with provider {
        instance<Context>().getSharedPreferences(Values.PREFERENCE_NAME_SETTINGS, Context.MODE_PRIVATE)
    }
    bind { singleton { MyDatabase(instance()) } }
    bind { singleton { instance<MyDatabase>().tokenDao() } }
    bind { singleton { instance<MyDatabase>().illustDao() } }
    bind { singleton { instance<MyDatabase>().downloadDao() } }
    bind { singleton { createApi<PixivAppApi>() } }
    bind { singleton { createApi<PixivOauthApi>() } }
}