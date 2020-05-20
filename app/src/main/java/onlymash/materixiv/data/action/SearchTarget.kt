package onlymash.materixiv.data.action

enum class SearchTarget(val value: String) {
    PARTIAL_MATCH("partial_match_for_tags"),
    EXACT_MATCH("exact_match_for_tags"),
    TITLE_CAPTION("title_and_caption")
}