/*
 * CraysCircle - Peer-to-Peer Communication App
 * Developer: Vivekanand Pandey [@https://www.linkedin.com/in/itsvnp/]
 * Copyright © 2025
 */

package me.vivekanand.crayscircle.chat

import java.util.UUID

enum class MessageStatus { QUEUED, SENT, DELIVERED, READ }

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val isFromMe: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val status: MessageStatus = MessageStatus.SENT
)

data class ChatState(
    val messages: List<ChatMessage> = emptyList(),
    val isConnected: Boolean = false,
    val error: String? = null
) 