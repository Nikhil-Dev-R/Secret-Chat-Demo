package com.rudraksha.secretchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rudraksha.secretchat.data.model.Message
import com.rudraksha.secretchat.data.remote.ChatClient
import com.rudraksha.secretchat.ui.theme.SecretChatTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SecretChatTheme {
//                ChatListScreen()
                ChatApp()
            }
        }
    }
}
var userName: String = ""

@Composable
fun ChatApp() {
    var username by remember { mutableStateOf("") }
    var isConnected by remember { mutableStateOf(false) }
    var recipient by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<Message>() }
    val chatClient = remember { ChatClient() }

    if (!isConnected) {
        Column(
            modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            TextField(
                value = username,
                onValueChange = { username = it },
                placeholder = { Text("Enter your username") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        CoroutineScope(Dispatchers.Default).launch {
                            isConnected = true;
                            chatClient.connect(
                                username,
                                messages
                            )
                        }
                    }
                )
            )

            Button(
                onClick = {
                    CoroutineScope(Dispatchers.Default).launch {
                        isConnected = true;
                        chatClient.connect(
                            username,
                            messages
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text("Connect")
            }
        }
    } else {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = recipient,
                onValueChange = { recipient = it },
                placeholder = { Text("Enter recipient username") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            )

            val message = remember { mutableStateOf("") }
            TextField(
                value = message.value,
                onValueChange = { message.value = it },
                placeholder = { Text("Enter message") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    chatClient.sendMessage(
                        Message(
                            content = message.value,
                            senderId = username,
                            receiversId = listOf(recipient)
                        )
                    )
                    message.value = ""
                })
            )

            Button(
                onClick = {
                    userName = username
                    chatClient.sendMessage(
                        Message(
                            content = message.value,
                            senderId = username,
                            receiversId = listOf(recipient)
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text("Send")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Chat Messages:")
            messages.forEach { msg ->
                Text("${msg.senderId}: ${msg.content}")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SecretChatTheme {
        ChatApp()
    }
}