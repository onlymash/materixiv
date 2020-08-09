package onlymash.materixiv.data.db

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import onlymash.materixiv.data.model.TokenResponse
import onlymash.materixiv.data.model.common.Illust
import onlymash.materixiv.data.model.common.UserPreview

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

    @TypeConverter
    fun userPreviewToString(userPreview: UserPreview): String =
        Json.stringify(UserPreview.serializer(), userPreview)

    @TypeConverter
    fun stringToUserPreview(json: String): UserPreview =
        Json.parse(UserPreview.serializer(), json)
}