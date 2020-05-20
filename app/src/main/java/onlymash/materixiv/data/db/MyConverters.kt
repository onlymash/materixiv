package onlymash.materixiv.data.db

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import onlymash.materixiv.data.model.TokenResponse
import onlymash.materixiv.data.model.common.Illust

class MyConverters {

    @TypeConverter
    fun tokenInfoToString(data: TokenResponse.TokenInfo): String =
        Json.stringify(TokenResponse.TokenInfo.serializer(), data)

    @TypeConverter
    fun stringToTokenInfo(json: String): TokenResponse.TokenInfo =
        Json.parse(TokenResponse.TokenInfo.serializer(), json)

    @TypeConverter
    fun illustToString(illust: Illust): String =
        Json.stringify(Illust.serializer(), illust)

    @TypeConverter
    fun stringToIllust(json: String): Illust =
        Json.parse(Illust.serializer(), json)
}