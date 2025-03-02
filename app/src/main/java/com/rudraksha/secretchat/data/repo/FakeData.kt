package com.rudraksha.secretchat.data.repo

import com.rudraksha.secretchat.data.model.Chat
import com.rudraksha.secretchat.data.model.User
import java.util.UUID

class FakeUsers {
    val users = listOf(
        User(
            id = UUID.randomUUID().toString(),
            username = "@ram",
            fullName = "Ram"
        ),
        User(
            id = UUID.randomUUID().toString(),
            username = "@shyam",
            fullName = "Shyam"
        ),
        User(
            id = UUID.randomUUID().toString(),
            fullName = "Mohan",
            username = "@mohan"
        ),
        User(
            id = UUID.randomUUID().toString(),
            fullName = "Radha",
            username = "@radha"
        ),
        User(
            id = UUID.randomUUID().toString(),
            fullName = "Krishna",
            username = "@krishna"
        ),
        User(
            id = UUID.randomUUID().toString(),
            fullName = "Manmohan",
            username = "@manmmohan"
        ),
        User(
            id = UUID.randomUUID().toString(),
            fullName = "Balram",
            username = "@balram"
        ),
        User(
            id = UUID.randomUUID().toString(),
            fullName = "Kanha",
            username = "@kanha"
        ),
        User(
            id = UUID.randomUUID().toString(),
            fullName = "Lakshman",
            username = "@lakshman"
        ),
        User(
            id = UUID.randomUUID().toString(),
            fullName = "Sita",
            username = "@sita"
        ),
        User(
            id = UUID.randomUUID().toString(),
            fullName = "Dashrath",
            username = "@dashrath"
        ),
        User(
            id = UUID.randomUUID().toString(),
            fullName = "Mate",
            username = "@mate"
        ),
    )
}

//class FakeChat {
//    val chatList = listOf(
//        Chat(
//
//        )
//    )
//}