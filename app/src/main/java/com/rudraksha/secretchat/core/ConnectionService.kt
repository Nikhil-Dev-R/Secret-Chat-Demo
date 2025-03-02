package com.rudraksha.secretchat.core

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.rudraksha.secretchat.MainActivity
import com.rudraksha.secretchat.R
import com.rudraksha.secretchat.data.model.Message
import com.rudraksha.secretchat.data.remote.ChatClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.WebSocket

class ConnectionService : Service() {
    private val CHANNEL_ID = "ForegroundServiceChannel"
    private val NOTIFICATION_ID = 1
    private val username = "default"
    private val messages = mutableListOf<Message>()

    private lateinit var chatClient: ChatClient
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: NetworkCallback

    private var webSocket: WebSocket? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    var isReconnecting = false
    var hasLost = false

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)

        serviceScope.launch {
            chatClient = ChatClient(username, messages)
            chatClient.connect()
            chatClient.setOnMessageReceivedListener { message ->
                // Handle received message
                messages.add(message)
            }
        }
//        connectWebSocket()

        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkCallback = object : NetworkCallback(){
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                serviceScope.launch {
                    isReconnecting = true
                    chatClient.disconnect()
                    chatClient.connect()
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                hasLost = true
            }
        }
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Start your network connection logic here
        return START_STICKY
    }

    private fun connectWebSocket() {

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "WebSocket Foreground Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Chat Service")
            .setContentText("Connected to chat server")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        connectivityManager?.unregisterNetworkCallback(networkCallback)
        serviceScope.launch {
            chatClient.disconnect()
        }
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}