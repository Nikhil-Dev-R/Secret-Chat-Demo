package com.rudraksha.secretchat.data.model

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Message(
    val id: String = UUID.randomUUID().toString(),
    val senderId: String,
    val receiversId: List<String> = listOf(),
    val timestamp: Long = System.currentTimeMillis(),
    val type: MessageType = MessageType.TEXT,  // TEXT, IMAGE, VIDEO, etc.
    val content: String? = null, // For text messages
    val fileMetadata: FileMetadata? = null, // For files
)

@Serializable
data class FileMetadata(
    val fileName: String,
    val fileType: String,
    val fileSize: Long,
    val totalChunks: Long // Total number of chunks
)