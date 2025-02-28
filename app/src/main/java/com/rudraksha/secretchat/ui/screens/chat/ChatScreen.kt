package com.rudraksha.secretchat.ui.screens.chat

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.rudraksha.secretchat.data.model.Message
import com.rudraksha.secretchat.data.remote.ChatClient
import com.rudraksha.secretchat.userName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Preview
@Composable
fun ChatScreen(
    chatClient: ChatClient = remember { ChatClient() }
) {
    var recipient by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<Message>() }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(8.dp)
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
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    ) {
                        Text(it, modifier = Modifier.padding(8.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier
                    .clip(shape = MaterialTheme.shapes.large)
                    .weight(0.9f)
                ,
                singleLine = true,
                placeholder = { Text("Enter message to send") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    scope.launch {
                        chatClient.sendMessage(
                            Message(
                                content = message,
                                senderId = userName,
                                receiversId = listOf(recipient)
                            )
                        )
                        message = ""
                    }
                }),
                shape = MaterialTheme.shapes.large
            )

            IconButton(
                onClick = {
                    scope.launch {
                        chatClient.sendMessage(
                            Message(
                                content = message,
                                senderId = userName,
                                receiversId = listOf(recipient)
                            )
                        )
                        message = ""
                    }
                },
                modifier = Modifier
                    .weight(0.1f)
                    .padding(start = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "send",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun SendChatRequest(
    context: Context = LocalContext.current,
    currentUser: String = "",
    scope: CoroutineScope = rememberCoroutineScope(),
    validate: (String) -> Boolean = {true},
    onDone: (JoinRequest) -> Unit = {}
) {
    var receiverUsername by remember { mutableStateOf("@") }
    var message by remember { mutableStateOf("") }
    var showDialog  by remember { mutableStateOf(true) }

    if (showDialog) {
        Dialog(
            onDismissRequest = {
                scope.launch {
                    showDialog = false
                }
            },
            properties = DialogProperties(
                dismissOnClickOutside = false,
                dismissOnBackPress = true,
                usePlatformDefaultWidth = true
            )
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(0.8f).height(300.dp),
                colors = CardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    disabledContentColor = MaterialTheme.colorScheme.onSurface,
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Send Join Request",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    OutlinedTextField(
                        value = receiverUsername,
                        onValueChange = { receiverUsername = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Recipient Username:") },
                        placeholder = { Text("Recipient Username:") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
//                        keyboardActions = KeyboardActions(onDone = { scope.launch {
//                            showDialog = false
//                            onDone(JoinRequest(currentUser, recipient, message))
//                        } })
                    )

                    OutlinedTextField(
                        value = message,
                        onValueChange = { receiverUsername = it },
                        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                        label = { Text("Join Message") },
                        placeholder = { Text("Enter join message:") },
                        maxLines = 5,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { scope.launch {
                            showDialog = false
                            onDone(JoinRequest(currentUser, receiverUsername, message))
                        } })
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                scope.launch { showDialog = false }
                            },
                            colors = ButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                                disabledContentColor = MaterialTheme.colorScheme.onSurface,
                            ),
                            modifier = Modifier.weight(1f).padding(4.dp)
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                scope.launch {
                                    showDialog = false
                                    onDone(JoinRequest(currentUser, receiverUsername, message))
                                }
                            },
                            modifier = Modifier.weight(1f).padding(4.dp)
                        ) {
                            Text("Join")
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ReceiveChatRequest(
    context: Context = LocalContext.current,
    request: JoinRequest = JoinRequest(),
    scope: CoroutineScope = rememberCoroutineScope(),
    validate: (String) -> Boolean = {true},
    acceptJoinRequest: (JoinResponse) -> Unit = {},
    rejectJoinRequest: (JoinResponse) -> Unit = {}
) {
    var showDialog  by remember { mutableStateOf(true) }

    if (showDialog) {
        Dialog(
            onDismissRequest = {
                scope.launch {
                    showDialog = false
                }
            },
            properties = DialogProperties(
                dismissOnClickOutside = false,
                dismissOnBackPress = true,
                usePlatformDefaultWidth = true
            )
        ) {
            Card(
                modifier = Modifier.width(400.dp).height(300.dp),
                colors = CardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    disabledContentColor = MaterialTheme.colorScheme.onSurface,
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(4.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        request.senderUsername,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text("wants to chat with you")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                scope.launch {
                                    showDialog = false
                                    rejectJoinRequest(
                                        JoinResponse(request.receiverUsername, request.senderUsername, false)
                                    )
                                }
                            },
                            colors = ButtonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                                disabledContentColor = MaterialTheme.colorScheme.onSurface,
                            ),
                            modifier = Modifier.weight(1f).padding(4.dp)
                        ) {
                            Text("Decline")
                        }
                        Button(
                            onClick = {
                                scope.launch {
                                    showDialog = false
                                    acceptJoinRequest(
                                        JoinResponse(request.receiverUsername, request.senderUsername, true)
                                    )
                                }
                            },
                            modifier = Modifier.weight(1f).padding(4.dp)
                        ) {
                            Text("Accept")
                        }
                    }
                }
            }
        }
    }
}

@Serializable
data class JoinRequest(
    val senderUsername: String = "",
    val receiverUsername: String = "",
    val joinMessage: String = "",
)

@Serializable
data class JoinResponse(
    val senderUsername: String = "",
    val receiverUsername: String = "",
    val accepted: Boolean = false,
)

suspend fun validate(user: String): Boolean {
    return if (user.isEmpty()) {
        false
    } else {
        isExistingUser(user)
    }
}

suspend fun isExistingUser(data: String): Boolean {
    return true
}