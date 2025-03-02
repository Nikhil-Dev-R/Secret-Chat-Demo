package com.rudraksha.secretchat.ui.screens.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rudraksha.secretchat.data.model.Message
import com.rudraksha.secretchat.data.remote.ChatClient
import com.rudraksha.secretchat.userName
import kotlinx.coroutines.launch

@Preview
@Composable
fun ChatScreen(
    chatClient: ChatClient = remember { ChatClient() },
    saveMessage: (Message) -> Unit = {},
) {
    var recipient by remember { mutableStateOf("@") }
    var message by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<Message>() }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        OutlinedTextField(
            value = recipient,
            onValueChange = { recipient = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Recipient Username:") },
            placeholder = { Text("Recipient Username:") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(messages) { msg ->
                msg.content?.let {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                    ) {
                        Text(it, modifier = Modifier.padding(8.dp))
                    }
                }
            }
        }

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = MaterialTheme.shapes.large),
            singleLine = true,
            placeholder = { Text("Enter message to send") },
            trailingIcon = {
                IconButton(
                    onClick = {
                        scope.launch {
                            val sendMessage = Message(
                                content = message,
                                senderId = userName,
                                receiversId = listOf(recipient)
                            )
                            chatClient.sendMessage(sendMessage)
                            saveMessage(sendMessage)
                            message = ""
                        }
                    },
                    modifier = Modifier.padding(start = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "send",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(64.dp)
                    )
                }
            },
            shape = MaterialTheme.shapes.large
        )
    }
}

suspend fun validate(user: String): Boolean {
    return if (user.isEmpty()) {
        false
    } else {
        isExistingUser(user)
    }
}

suspend fun isExistingUser(data: String): Boolean {
    return false
}