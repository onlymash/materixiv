package onlymash.materixiv.data.action

import onlymash.materixiv.data.db.entity.Token


data class ActionDetail(
    val token: Token,
    val query: String,
    val initialPosition: Int = 0
)