package onlymash.materixiv.data.repository.autocomplete

import onlymash.materixiv.data.model.common.Tag

interface AutocompleteRepository {
    suspend fun getTags(auth: String, word: String?): List<Tag>?
    suspend fun getUsers(auth: String, word: String?): List<String>?
}