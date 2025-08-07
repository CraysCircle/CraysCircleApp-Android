/*
 * CraysCircle - Peer-to-Peer Communication App
 * Developer: Vivekanand Pandey [@https://www.linkedin.com/in/itsvnp/]
 * Copyright © 2025
 */

package me.vivekanand.crayscircle.wifi

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.aware.AttachCallback
import android.net.wifi.aware.DiscoverySessionCallback
import android.net.wifi.aware.PeerHandle
import android.net.wifi.aware.PublishConfig
import android.net.wifi.aware.PublishDiscoverySession
import android.net.wifi.aware.SubscribeConfig
import android.net.wifi.aware.SubscribeDiscoverySession
import android.net.wifi.aware.WifiAwareManager
import android.net.wifi.aware.WifiAwareSession
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.vivekanand.crayscircle.chat.ChatMessage
import me.vivekanand.crayscircle.chat.ChatState
import me.vivekanand.crayscircle.chat.MessageStatus
import me.vivekanand.crayscircle.data.ChatDatabase
import me.vivekanand.crayscircle.data.Gender
import me.vivekanand.crayscircle.data.MessageEntity
import me.vivekanand.crayscircle.data.UserProfile
import org.json.JSONObject
import org.json.JSONArray
import java.nio.charset.StandardCharsets
import java.util.concurrent.atomic.AtomicInteger
import java.util.UUID
import android.net.wifi.rtt.WifiRttManager
import android.net.wifi.rtt.RangingRequest
import android.net.wifi.rtt.RangingResult
import android.net.wifi.rtt.RangingResultCallback
import android.net.MacAddress
import android.net.ConnectivityManager
import android.net.NetworkSpecifier
import android.net.NetworkRequest
import android.net.Network
import android.os.Build
import androidx.annotation.RequiresApi

class WifiAwareController(
    private val context: Context,
    private val userProfileProvider: (() -> UserProfile?)? = null,
    private val chatDatabase: ChatDatabase? = null
) {
    private val wifiAwareManager = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        context.getSystemService(Context.WIFI_AWARE_SERVICE) as? WifiAwareManager
    } else null
    private val mainHandler = Handler(Looper.getMainLooper())

    private var wifiAwareSession: WifiAwareSession? = null
    private var publishSession: PublishDiscoverySession? = null
    private var subscribeSession: SubscribeDiscoverySession? = null
    private var currentPeerHandle: PeerHandle? = null

    private val _discoveredPeers = MutableStateFlow<List<PeerDevice>>(emptyList())
    val discoveredPeers: StateFlow<List<PeerDevice>> = _discoveredPeers.asStateFlow()

    private val _chatState = MutableStateFlow(ChatState())
    val chatState: StateFlow<ChatState> = _chatState.asStateFlow()

    private var connectedPeer: PeerDevice? = null
    private val messageQueue = mutableListOf<ChatMessage>()

    private var isAutoConnectEnabled: Boolean = false
    fun setAutoConnectEnabled(enabled: Boolean) {
        isAutoConnectEnabled = enabled
    }

    companion object {
        private const val SERVICE_NAME = "me.vivekanand.crayscircle"
        private const val MESSAGE_TYPE = 1
    }

    fun startDiscovery() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
            _chatState.value = _chatState.value.copy(error = "Wi-Fi Aware is not supported on this Android version.")
            return
        }
        if (wifiAwareManager == null || !wifiAwareManager.isAvailable) {
            _chatState.value = _chatState.value.copy(error = "Wi-Fi Aware is not supported on this device.")
            return
        }
        if (!hasPermissions()) {
            _chatState.value = _chatState.value.copy(error = "Location and Nearby permissions are not granted.")
            return
        }
        val profile = userProfileProvider?.invoke()
        if (profile == null || profile.uniqueId.isBlank()) {
            _chatState.value = _chatState.value.copy(error = "Please complete your profile before scanning for peers.")
            return
        }
        try {
            wifiAwareManager.attach(object : AttachCallback() {
                override fun onAttached(session: WifiAwareSession) {
                    wifiAwareSession = session
                    startPublishing(session)
                    startSubscribing(session)
                }
                override fun onAttachFailed() {
                    _chatState.value = _chatState.value.copy(error = "Failed to attach to Wi-Fi Aware.")
                }
            }, mainHandler)
        } catch (e: Exception) {
            _chatState.value = _chatState.value.copy(error = "Wi-Fi Aware attach error: ${e.localizedMessage}")
        }
    }

    private fun startPublishing(session: WifiAwareSession) {
        if (!hasPermissions()) return
        val profile = userProfileProvider?.invoke() ?: UserProfile()
        if (profile.uniqueId.isBlank()) return
        val serviceInfo = PeerDevice.toJson(profile).toByteArray(StandardCharsets.UTF_8)
        val config = PublishConfig.Builder()
            .setServiceName(SERVICE_NAME)
            .setServiceSpecificInfo(serviceInfo)
            .build()
        try {
            session.publish(config, object : DiscoverySessionCallback() {
                override fun onPublishStarted(session: PublishDiscoverySession) {
                    publishSession = session
                }
                override fun onMessageReceived(peerHandle: PeerHandle, message: ByteArray) {
                    handleIncomingMessage(peerHandle, message)
                }
                override fun onSessionTerminated() {
                    publishSession = null
                    _chatState.update { it.copy(isConnected = false, error = "Publish session terminated.") }
                }
            }, mainHandler)
        } catch (e: SecurityException) {
            _chatState.value = _chatState.value.copy(error = "Permission denied: ${e.message}")
        } catch (e: Exception) {
            _chatState.value = _chatState.value.copy(error = "Publish session error: ${e.localizedMessage}")
        }
    }

    private fun startSubscribing(session: WifiAwareSession) {
        if (!hasPermissions()) return
        val config = SubscribeConfig.Builder()
            .setServiceName(SERVICE_NAME)
            .build()
        try {
            session.subscribe(config, object : DiscoverySessionCallback() {
                override fun onSubscribeStarted(session: SubscribeDiscoverySession) {
                    subscribeSession = session
                }
                override fun onServiceDiscovered(
                    peerHandle: PeerHandle,
                    serviceSpecificInfo: ByteArray?,
                    matchFilter: List<ByteArray>?
                ) {
                    val peer = serviceSpecificInfo?.let {
                        PeerDevice.fromJson(String(it, StandardCharsets.UTF_8), peerHandle)
                    } ?: PeerDevice("", "Peer", 1, Gender.OTHER, peerHandle)
                    if (peer.uniqueId.isBlank()) return
                    updatePeerDistance(peer)
                    if (isAutoConnectEnabled) {
                        peer.status = "Connecting"
                        connectToPeer(peer)
                    }
                    _discoveredPeers.update { currentPeers ->
                        val filteredPeers = currentPeers.filter { it.uniqueId != peer.uniqueId }
                        filteredPeers + peer
                    }
                }
                override fun onMessageReceived(peerHandle: PeerHandle, message: ByteArray) {
                    handleIncomingMessage(peerHandle, message)
                }
                override fun onSessionTerminated() {
                    subscribeSession = null
                    _chatState.update { it.copy(isConnected = false, error = "Subscribe session terminated.") }
                }
            }, mainHandler)
        } catch (e: SecurityException) {
            _chatState.value = _chatState.value.copy(error = "Permission denied: ${e.message}")
        } catch (e: Exception) {
            _chatState.value = _chatState.value.copy(error = "Subscribe session error: ${e.localizedMessage}")
        }
    }

    fun connectToPeer(peer: PeerDevice) {
        currentPeerHandle = peer.handle
        connectedPeer = peer
        peer.status = "Connected"
        _chatState.value = _chatState.value.copy(isConnected = true, error = null)
        if (messageQueue.isNotEmpty()) {
            sendBatchMessages(messageQueue)
            messageQueue.clear()
        }
        
    }

    private fun sendBatchMessages(messages: List<ChatMessage>) {
        val peerHandle = currentPeerHandle ?: run {
            _chatState.update { it.copy(error = "No peer handle for batch send.") }
            return
        }
        if (messages.isEmpty()) return
        val jsonArray = JSONArray()
        messages.forEach { msg ->
            val obj = JSONObject().apply {
                put("id", msg.id)
                put("content", msg.content)
                put("timestamp", msg.timestamp)
                put("isFromMe", msg.isFromMe)
            }
            jsonArray.put(obj)
        }
        val batchBytes = jsonArray.toString().toByteArray(StandardCharsets.UTF_8)
        try {
            publishSession?.sendMessage(peerHandle, MESSAGE_TYPE, batchBytes)
            subscribeSession?.sendMessage(peerHandle, MESSAGE_TYPE, batchBytes)
        } catch (e: Exception) {
            _chatState.update { it.copy(error = "Batch send error: ${e.localizedMessage}") }
        }
        _chatState.update { state ->
            val updated = state.messages.map {
                if (messages.any { m -> m.id == it.id }) it.copy(status = MessageStatus.SENT) else it
            }
            state.copy(messages = updated)
        }
        chatDatabase?.let { db ->
            val peerId = connectedPeer?.id?.toString() ?: "queued"
            messages.forEach { msg ->
                CoroutineScope(Dispatchers.IO).launch {
                    db.messageDao().insertMessage(
                        MessageEntity(
                            id = msg.id,
                            peerId = peerId,
                            content = msg.content,
                            isFromMe = true,
                            timestamp = msg.timestamp,
                            status = MessageStatus.SENT.name
                        )
                    )
                }
            }
        }
    }

    fun disconnectFromPeer() {
        currentPeerHandle = null
        connectedPeer = null
        _chatState.value = _chatState.value.copy(isConnected = false, messages = emptyList(), error = "Disconnected from peer.")
    }

    fun sendMessage(message: String) {
        if (!_chatState.value.isConnected || currentPeerHandle == null) {
            val chatMessage = ChatMessage(content = message, isFromMe = true, status = MessageStatus.QUEUED)
            messageQueue.add(chatMessage)
            _chatState.update { state ->
                state.copy(messages = state.messages + chatMessage, error = "Not connected. Message queued.")
            }
            chatDatabase?.let { db ->
                val peerId = connectedPeer?.id?.toString() ?: "queued"
                CoroutineScope(Dispatchers.IO).launch {
                    db.messageDao().insertMessage(
                        MessageEntity(
                            peerId = peerId,
                            content = message,
                            isFromMe = true,
                            status = MessageStatus.QUEUED.name
                        )
                    )
                }
            }
            return
        }
        try {
            actuallySendMessage(message)
        } catch (e: Exception) {
            _chatState.update { state ->
                state.copy(error = "Failed to send message: ${e.localizedMessage}")
            }
        }
    }

    private fun actuallySendMessage(message: String) {
        val peerHandle = currentPeerHandle ?: throw IllegalStateException("No peer handle available")
        val chatMessage = ChatMessage(content = message, isFromMe = true, status = MessageStatus.SENT)
        val obj = try {
            JSONObject().apply {
                put("id", chatMessage.id)
                put("content", chatMessage.content)
                put("timestamp", chatMessage.timestamp)
                put("isFromMe", chatMessage.isFromMe)
            }
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to serialize message to JSON: ${e.localizedMessage}")
        }
        val messageBytes = obj.toString().toByteArray(StandardCharsets.UTF_8)
        try {
            publishSession?.sendMessage(peerHandle, MESSAGE_TYPE, messageBytes)
            subscribeSession?.sendMessage(peerHandle, MESSAGE_TYPE, messageBytes)
        } catch (e: Exception) {
            _chatState.update { it.copy(error = "Send error: ${e.localizedMessage}") }
            throw IllegalStateException("Failed to send message over Wi-Fi Aware: ${e.localizedMessage}")
        }
        _chatState.update { state ->
            val updatedMessages = state.messages.map {
                if (it.content == message && it.status == MessageStatus.QUEUED) it.copy(status = MessageStatus.SENT) else it
            } + chatMessage
            state.copy(messages = updatedMessages.distinctBy { it.id })
        }
        chatDatabase?.let { db ->
            val peerId = connectedPeer?.id?.toString() ?: "queued"
            CoroutineScope(Dispatchers.IO).launch {
                db.messageDao().insertMessage(
                    MessageEntity(
                        id = chatMessage.id,
                        peerId = peerId,
                        content = message,
                        isFromMe = true,
                        status = MessageStatus.SENT.name
                    )
                )
            }
        }
    }

    fun addHistoricalMessage(content: String, isFromMe: Boolean, timestamp: Long = System.currentTimeMillis(), status: MessageStatus = MessageStatus.DELIVERED) {
        val message = ChatMessage(
            content = content,
            isFromMe = isFromMe,
            timestamp = timestamp,
            status = status
        )
        _chatState.update { state ->
            state.copy(messages = state.messages + message)
        }
    }

    fun stopDiscovery() {
        try { 
            publishSession?.close() 
        } catch (e: Exception) {
            // Log error but don't crash
        }
        try { 
            subscribeSession?.close() 
        } catch (e: Exception) {
            // Log error but don't crash
        }
        try { 
            wifiAwareSession?.close() 
        } catch (e: Exception) {
            // Log error but don't crash
        }
        publishSession = null
        subscribeSession = null
        wifiAwareSession = null
        currentPeerHandle = null
        connectedPeer = null
        _discoveredPeers.value = emptyList()
        _chatState.value = ChatState(error = "Discovery stopped.")
    }

    private fun hasPermissions(): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val nearbyWifi = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.NEARBY_WIFI_DEVICES)
        } else PackageManager.PERMISSION_GRANTED 
        return fineLocation == PackageManager.PERMISSION_GRANTED &&
                nearbyWifi == PackageManager.PERMISSION_GRANTED
    }

    private fun handleIncomingMessage(peerHandle: PeerHandle, message: ByteArray) {
        
        if (currentPeerHandle == null) {
            currentPeerHandle = peerHandle
            _chatState.update { it.copy(isConnected = true, error = null) }
        }
        val messageStr = String(message, StandardCharsets.UTF_8)
        if (messageStr.startsWith("__STATUS__")) {
            val parts = messageStr.split(":")
            if (parts.size == 3) {
                val msgId = parts[1]
                val status = runCatching { MessageStatus.valueOf(parts[2]) }.getOrNull()
                if (status != null) {
                    _chatState.update { state ->
                        state.copy(messages = state.messages.map {
                            if (it.id == msgId && it.isFromMe) it.copy(status = status) else it
                        })
                    }
                }
            }
            return
        }
        try {
            when {
                messageStr.trim().startsWith("[") -> {
                    val arr = JSONArray(messageStr)
                    for (i in 0 until arr.length()) {
                        processJsonMessage(peerHandle, arr.getJSONObject(i))
                    }
                }
                messageStr.trim().startsWith("{") -> {
                    processJsonMessage(peerHandle, JSONObject(messageStr))
                }
                else -> {
                    _chatState.update { it.copy(error = "Malformed message received.") }
                }
            }
        } catch (e: Exception) {
            _chatState.update { it.copy(error = "Failed to parse incoming message: ${e.localizedMessage}") }
        }
    }

    private fun processJsonMessage(peerHandle: PeerHandle, obj: JSONObject) {
        val chatMessage = ChatMessage(
            id = obj.optString("id", UUID.randomUUID().toString()),
            content = obj.optString("content", ""),
            isFromMe = false,
            timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
            status = MessageStatus.DELIVERED
        )
        if (chatMessage.content.isBlank()) return
        if (_chatState.value.messages.any { it.id == chatMessage.id }) {
            return
        }
        _chatState.update { state ->
            state.copy(messages = state.messages + chatMessage)
        }
        sendStatus(peerHandle, chatMessage.id, MessageStatus.DELIVERED)
    }

    private fun sendStatus(peerHandle: PeerHandle, msgId: String, status: MessageStatus) {
        val statusMsg = "__STATUS__:$msgId:${status.name}".toByteArray(StandardCharsets.UTF_8)
        try {
            publishSession?.sendMessage(peerHandle, MESSAGE_TYPE, statusMsg)
            subscribeSession?.sendMessage(peerHandle, MESSAGE_TYPE, statusMsg)
        } catch (_: Exception) {}
    }

    fun markMessageRead(msgId: String) {
        val peerHandle = currentPeerHandle ?: return
        sendStatus(peerHandle, msgId, MessageStatus.READ)
        _chatState.update { state ->
            state.copy(messages = state.messages.map {
                if (it.id == msgId && !it.isFromMe) it.copy(status = MessageStatus.READ) else it
            })
        }
    }

    fun isCurrentlyConnected(): Boolean {
        return _chatState.value.isConnected && currentPeerHandle != null
    }

    fun loadQueuedMessages(peerId: String) {
        chatDatabase?.let { db ->
            CoroutineScope(Dispatchers.IO).launch {
                db.messageDao().getQueuedMessagesForPeer(peerId).collect { queuedEntities ->
                    val queuedMessages = queuedEntities.map {
                        ChatMessage(
                            id = it.id,
                            content = it.content,
                            isFromMe = it.isFromMe,
                            timestamp = it.timestamp,
                            status = MessageStatus.QUEUED
                        )
                    }
                    messageQueue.clear()
                    messageQueue.addAll(queuedMessages)
                    _chatState.update { state ->
                        val existingIds = state.messages.map { it.id }.toSet()
                        val newMessages = queuedMessages.filter { it.id !in existingIds }
                        state.copy(messages = state.messages + newMessages)
                    }
                }
            }
        }
    }

    fun clearError() {
        _chatState.update { it.copy(error = null) }
    }

    private fun updatePeerDistance(peer: PeerDevice) {
        
        
        peer.distance = (20..5000).random() / 100.0f
        
        _discoveredPeers.update { peers ->
            peers.map { if (it.uniqueId == peer.uniqueId) peer else it }
        }
    }

    
    private fun isRttSupported(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            context.packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_RTT)
        } else false
    }

    
    private fun hasAllRequiredPermissions(): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val wifiState = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_WIFI_STATE)
        val changeWifi = ContextCompat.checkSelfPermission(context, Manifest.permission.CHANGE_WIFI_STATE)
        val nearbyWifi = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.NEARBY_WIFI_DEVICES)
        } else PackageManager.PERMISSION_GRANTED
        return fineLocation == PackageManager.PERMISSION_GRANTED &&
                wifiState == PackageManager.PERMISSION_GRANTED &&
                changeWifi == PackageManager.PERMISSION_GRANTED &&
                nearbyWifi == PackageManager.PERMISSION_GRANTED
    }

    
    private fun startRttRangingToPeer(peerMac: MacAddress, peer: PeerDevice) {
        if (!isRttSupported()) {
            _chatState.update { it.copy(error = "Wi-Fi RTT is not supported on this device.") }
            return
        }
        if (!hasAllRequiredPermissions()) {
            _chatState.update { it.copy(error = "Location and Wi-Fi permissions are required for distance measurement. Please grant all permissions in app settings.") }
            return
        }
        
        try {
            val rttManager = context.getSystemService("wifirtt") as? WifiRttManager ?: return
            val request = RangingRequest.Builder()
                .addAccessPoint(android.net.wifi.ScanResult().apply { BSSID = peerMac.toString() })
                .build()
            rttManager.startRanging(request, context.mainExecutor, object : RangingResultCallback() {
                override fun onRangingResults(results: List<RangingResult>) {
                    val result = results.firstOrNull()
                    if (result != null && result.status == RangingResult.STATUS_SUCCESS) {
                        peer.distance = result.distanceMm / 1000f 
                        _discoveredPeers.update { peers ->
                            peers.map { if (it.uniqueId == peer.uniqueId) peer else it }
                        }
                    } else {
                        _chatState.update { it.copy(error = "Failed to measure distance to peer.") }
                    }
                }
                override fun onRangingFailure(code: Int) {
                    _chatState.update { it.copy(error = "Wi-Fi RTT ranging failed (code $code). Please check permissions and try again.") }
                }
            })
        } catch (e: SecurityException) {
            _chatState.update { it.copy(error = "Permission denied for Wi-Fi RTT ranging: ${e.message}") }
        } catch (e: Exception) {
            _chatState.update { it.copy(error = "Wi-Fi RTT ranging error: ${e.message}") }
        }
    }
} 