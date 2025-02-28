package com.rudraksha.secretchat.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val email: String = "",
    val username: String = "",
    val fullName: String = "",
    val online: Boolean = false,
    val description: String = "",
    val profilePictureUrl: String = "",
    val contacts: MutableList<User> = mutableListOf()
)