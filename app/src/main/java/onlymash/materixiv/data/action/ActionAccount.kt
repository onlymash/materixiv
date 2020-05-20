package onlymash.materixiv.data.action

import onlymash.materixiv.data.db.entity.Token

data class ActionAccount(
    val token: Token,
    var currentPassword: String,
    var newPassword: String = "",
    var newAccount: String = "",
    var newMail: String = ""
) {
    private val currentAccount get() = token.data.profile.account
    private val currentMail get() = token.data.profile.mailAddress

    val updatedMap: Map<String, String>
        get() {
            val map: MutableMap<String, String> = mutableMapOf()
            map["current_password"] = currentPassword
            if (newPassword.isNotEmpty() && newPassword != currentPassword) {
                map["new_password"] = newPassword
            }
            if (newAccount.isNotEmpty() && newAccount != currentAccount) {
                map["new_user_account"] = newAccount
            }
            if (newMail.isNotEmpty() && newMail != currentMail) {
                map["new_mail_address"] = newMail
            }
            return map
        }
}