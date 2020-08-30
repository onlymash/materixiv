package onlymash.materixiv.data.db

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import onlymash.materixiv.data.model.TokenResponse
import onlymash.materixiv.data.model.common.Illust
import onlymash.materixiv.data.model.common.UserPreview

class MyConverters {

    @TypeConverter
    fun tokenInfoToString(data: TokenResponse.TokenInfo): String =
        Json.encodeToString(TokenResponse.TokenInfo.serializer(), data)

    @TypeConverter
    fun stringToTokenInfo(json: String): TokenResponse.TokenInfo =
        Json.decodeFromString(TokenResponse.TokenInfo.serializer(), json)

    @TypeConverter
    fun illustToString(illust: Illust): String =
        Json.encodeToString(Illust.serializer(), illust)

    @TypeConverter
    fun stringToIllust(json: String): Illust =
        Json.decodeFromString(Illust.serializer(), json)

    @TypeConverter
    fun userPreviewToString(userPreview: UserPreview): String =
        Json.encodeToString(UserPreview.serializer(), userPreview)

    @TypeConverter
    fun stringToUserPreview(json: String): UserPreview =
        Json.decodeFromString(UserPreview.serializer(), json)
}