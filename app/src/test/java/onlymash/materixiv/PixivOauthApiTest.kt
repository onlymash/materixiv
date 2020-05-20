package onlymash.materixiv

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import onlymash.materixiv.data.api.PixivOauthApi
import onlymash.materixiv.network.pixiv.createApi
import org.junit.Test

import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], manifest = Config.NONE)
class PixivOauthApiTest {

    private val username: String = ""
    private val password: String = ""
    private val refreshToken: String = ""
    private val deviceToken: String = ""

    private lateinit var oauthApi: PixivOauthApi

    @Before
    fun setup() {
        oauthApi = createApi()
    }

    @Test
    fun login() {
        var success = false
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val token = oauthApi.login(username, password).body()
                println(token)
            } catch (e: IOException) {
                println(e.message)
            }
            success = true
        }
        Thread.sleep(10000)
        assert(success)
    }

    @Test
    fun refresh() {
        var success = false
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val token = oauthApi.refreshToken(refreshToken, deviceToken).body()
                println(token?.data?.accessToken)
            } catch (e: IOException) {
                println(e.message)
            }
            success = true
        }
        Thread.sleep(10000)
        assert(success)
    }
}