package com.rudraksha.secretchat.data.repo

import com.rudraksha.secretchat.data.model.Chat
import com.rudraksha.secretchat.data.model.User
import java.util.UUID

class FakeUsers {
    val users = listOf(
        User(
            id = UUID.randomUUID().toString(),
            name = "Ram"
        ),
        User(
            id = UUID.randomUUID().toString(),
            name = "Shyam"
        ),
        User(
            id = UUID.randomUUID().toString(),
            name = "Mohan"
        ),
        User(
            id = UUID.randomUUID().toString(),
            name = "Radha"
        ),
        User(
            id = UUID.randomUUID().toString(),
            name = "Krishna"
        ),
        User(
            id = UUID.randomUUID().toString(),
            name = "Manmohan"
        ),
        User(
            id = UUID.randomUUID().toString(),
            name = "Balram"
        ),
        User(
            id = UUID.randomUUID().toString(),
            name = "Kanha"
        ),
        User(
            id = UUID.randomUUID().toString(),
            name = "Lakshman"
        ),
        User(
            id = UUID.randomUUID().toString(),
            name = "Sita"
        ),
        User(
            id = UUID.randomUUID().toString(),
            name = "Dashrath"
        ),
        User(
            id = UUID.randomUUID().toString(),
            name = "Mate"
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