package onlymash.materixiv.utils

import android.content.Context
import android.text.format.DateUtils
import onlymash.materixiv.app.Values
import java.text.SimpleDateFormat
import java.util.*

object DateUtil {

    fun formatDate(context: Context, dateString: String): String? {
        return context.formatDate(dateString.parseDate())
    }

    private fun String.parseDate(pattern: String = Values.DATE_FORMAT_NSISO8601): Long? {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.parse(this)?.time
    }

    private fun Context.formatDate(millis: Long?): String? {
        if (millis == null) {
            return null
        }
        return DateUtils.formatDateTime(
            this,
            millis,
            DateUtils.FORMAT_SHOW_TIME or DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR
        )
    }
}