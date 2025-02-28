package com.rudraksha.secretchat.data.remote

import android.util.Log
import com.rudraksha.secretchat.HOST
import com.rudraksha.secretchat.PORT
import com.rudraksha.secretchat.data.model.FileMetadata
import com.rudraksha.secretchat.data.model.Message
import com.rudraksha.secretchat.userName
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class ChatClient(
    private val username: String = "default",
    private val messages: MutableList<Message> = mutableListOf()
) {
    private val client = HttpClient(OkHttp) {
        engine {
//            this.proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress(HOST, PORT))
        }
        install(WebSockets) {
        }
    }
    private var webSocketSession: WebSocketSession? = null
    private var onMessageReceived: ((Message) -> Unit)? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    val isConnected = MutableStateFlow(false)

    fun connect(username: String = this.username, messages: MutableList<Message> = this.messages) {
        scope.launch {
            while (true) {
                try {
                    webSocketSession = client.webSocketSession(
                        host = HOST,
                        port = PORT,
                        path = "/chat/$username"
                    )
                    isConnected.value = true

                    scope.launch {
                        webSocketSession?.incoming?.consumeEach { frame ->
                            when (frame) {
                                is Frame.Text -> {
                                    val message = Json.decodeFromString<Message>(frame.readText())
                                    messages.add(message)
                                    onMessageReceived?.invoke(message)
                                }
                                is Frame.Binary -> {
                                }
                                else -> {
                                }
                            }
                        }
                    }

                } catch (e: Exception) {
                    e.localizedMessage?.let { Log.e("Exception", it) }
                    isConnected.value = false
                    delay(5000) // Retry connection
                }
            }
        }
    }

    fun sendMessage(message: Message) {
        scope.launch {
            val jsonMessage = Json.encodeToString(message)
            webSocketSession?.send(Frame.Text(jsonMessage))
        }
    }

    fun setOnMessageReceivedListener(listener: (Message) -> Unit) {
        onMessageReceived = listener
    }

    private fun receiveMessage(messages: MutableList<Message>) {
        scope.launch {
            webSocketSession?.incoming?.consumeEach { frame ->
                when (frame) {
                    is Frame.Text -> {
                        val message = Json.decodeFromString<Message>(frame.readText())
                        messages.add(message)
                    }
                    else -> {
                    }
                }
            }
        }
    }

    suspend fun sendFile(file: File, recipient: String) {
        val fileSize = file.length()
        val chunkSize = 1024 * 64 // 64 KB per chunk
        val totalChunks = (fileSize + chunkSize - 1) / chunkSize
        val fileMetadata = FileMetadata(
            fileName = file.name,
            fileType = file.extension,
            fileSize = fileSize,
            totalChunks = totalChunks
        )

        // Send metadata first as a JSON message
        webSocketSession?.send(
            Frame.Text(
                Json.encodeToString(
                    Message(
                        senderId = userName,
                        receiversId = listOf(recipient),
                        content = Json.encodeToString(fileMetadata),
                    )
                )
            )
        )

        // Read the file in chunks and send via WebSocket
        file.inputStream().use { inputStream ->
            val buffer = ByteArray(chunkSize)
            var bytesRead: Int
            var chunkIndex = 0

            while ( inputStream.read(buffer).also { bytesRead = it } != -1) {
                chunkIndex++
                val chunkData = buffer.copyOf(bytesRead)
                webSocketSession?.send(Frame.Binary(true, chunkData))
                println("📤 Sent chunk $chunkIndex/$totalChunks")
            }
        }
    }

    suspend fun disconnect() {
        webSocketSession?.close(
            CloseReason(CloseReason.Codes.NORMAL, "Disconnecting the client")
        )
    }
}