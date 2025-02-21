package com.rudraksha.secretchat.data.remote

import android.util.Log
import io.ktor.client.*
import io.ktor.client.engine.ProxyBuilder
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import java.net.InetSocketAddress
import java.net.Proxy

const val HOST = "192.168.43.63"
const val PORT = 8080

class ChatClient {
    private val client = HttpClient() {
        engine {
            this.proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress(HOST, PORT))
        }
        install(WebSockets) {
        }
    }
    private var socket: WebSocketSession? = null
    private val isConnected = MutableStateFlow(false)

    fun connect(username: String, messages: MutableList<String>) {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                try {
                    socket = client.webSocketSession(host = HOST, port = PORT, path = "/chat/$username")
                    isConnected.value = true

                    socket?.incoming?.consumeEach { frame ->
                        if (frame is Frame.Text) {
                            messages.add(frame.readText())
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

    fun sendMessage(message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            socket?.send( Frame.Text(message) )
        }
    }
}