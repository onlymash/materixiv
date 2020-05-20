package onlymash.materixiv.data.action

enum class Duration(val value: String) {
    LAST_DAY("within_last_day"),
    LAST_WEEK("within_last_week"),
    LAST_MONTH("within_last_month"),
    HALF_YEAR("within_half_year"),
    YEAR("within_year")
}