package onlymash.materixiv.data.repository.detail

import onlymash.materixiv.data.model.UserDetailResponse

interface UserDetailRepository {

    suspend fun getUserDetail(auth: String, userId: String): UserDetailResponse?
}