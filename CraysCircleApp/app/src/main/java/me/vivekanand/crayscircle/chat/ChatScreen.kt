/*
 * CraysCircle - Peer-to-Peer Communication App
 * Developer: Vivekanand Pandey [@https://www.linkedin.com/in/itsvnp/]
 * Copyright © 2025
 */

package me.vivekanand.crayscircle.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.vivekanand.crayscircle.data.UserProfile
import me.vivekanand.crayscircle.ui.IconAppButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarData
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatState: ChatState,
    onSendMessage: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onReadMessage: (String) -> Unit = {},
    isConnected: Boolean = true,
    peerProfile: UserProfile? = null,
    onClearError: (() -> Unit)? = null
) {
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val dateFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val avatarEmojis = listOf(
        "😀", "🦄", "🐱", "🤖", "👽", "🦊", "🐧", "🐸", "🐼", "🦁", "🐵", "🧑‍🎤", "🧙‍♂️", "🧑‍🚀", "🧑‍💻"
    )
    val peerAvatar = peerProfile?.let { avatarEmojis.getOrNull(it.avatarId - 1) } ?: "🧑‍💻"
    val peerNickname = peerProfile?.nickname ?: "Peer"
    val peerDistance = peerProfile?.let { (it as? me.vivekanand.crayscircle.wifi.PeerDevice)?.distance }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(chatState.error) {
        chatState.error?.let { errorMsg ->
            snackbarHostState.showSnackbar(errorMsg)
            onClearError?.invoke()
        }
    }

    LaunchedEffect(chatState.messages.size) {
        if (chatState.messages.isNotEmpty()) {
            listState.animateScrollToItem(chatState.messages.size - 1)
        }
    }

    LaunchedEffect(chatState.messages) {
        chatState.messages
            .filter { !it.isFromMe && it.status != MessageStatus.READ }
            .forEach { onReadMessage(it.id) }
    }

    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = peerAvatar,
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(4.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column{
                                Text(
                                    text = peerNickname,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = if (isConnected) "Online" else "Connecting...",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isConnected) Color(0xFF4CAF50) else Color.Gray,
                                    fontWeight = FontWeight.Medium,
                                )
                                if (peerDistance != null) {
                                    Text(
                                        text = when {
                                            peerDistance < 1 -> "~${(peerDistance * 100).toInt()}cm away"
                                            peerDistance < 1000 -> "~${peerDistance.toInt()}m away"
                                            else -> "~%.2fkm away".format(peerDistance / 1000)
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconAppButton(onClick = onBackClick, icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    })
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shape = MaterialTheme.shapes.large,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .border(
                            1.5.dp,
                            MaterialTheme.colorScheme.outline,
                            MaterialTheme.shapes.medium
                        )
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surface),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp, end = 4.dp),
                        placeholder = { Text(text = "Type a message") },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                        )
                    )
                    IconAppButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                onSendMessage(messageText)
                                messageText = ""
                            }
                        },
                        enabled = messageText.isNotBlank(),
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.Send,
                                contentDescription = "Send",
                                tint = if (messageText.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            )
                        },
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.98f))
        ) {
            
            val bgIcons = listOf(
                Icons.Rounded.Star,
                Icons.Rounded.Favorite,
                Icons.Rounded.Face,
                Icons.Rounded.Lightbulb,
                Icons.Rounded.Pets,
                Icons.Rounded.Bolt,
                Icons.Rounded.Cloud,
                Icons.Rounded.SportsEsports,
                Icons.Rounded.SelfImprovement
            )
            val bgColors = listOf(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.06f),
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.06f),
                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.06f),
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.10f),
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.08f),
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.08f),
                MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.08f)
            )
            val iconCount = remember { (48..84).random() }
            val boxWidth = 400f
            val boxHeight = 800f
            val cols = kotlin.math.ceil(kotlin.math.sqrt(iconCount.toFloat())).toInt()
            val rows = kotlin.math.ceil(iconCount / cols.toFloat()).toInt()
            val cellWidth = boxWidth / cols
            val cellHeight = boxHeight / rows
            val random = remember { kotlin.random.Random(System.currentTimeMillis()) }
            val iconData = List(iconCount) { idx ->
                val icon = bgIcons.random(random)
                val color = bgColors.random(random)
                val col = idx % cols
                val row = idx / cols
                val maxSize = minOf(cellWidth, cellHeight) * 0.5f
                val minSize = minOf(cellWidth, cellHeight) * 0.25f
                val size = random.nextFloat() * (maxSize - minSize) + minSize
                val x = col * cellWidth + random.nextFloat() * (cellWidth - size)
                val y = row * cellHeight + random.nextFloat() * (cellHeight - size)
                Triple(icon, size, color) to androidx.compose.ui.geometry.Rect(x, y, x + size, y + size)
            }
            iconData.forEach { (iconTriple, rect) ->
                Icon(
                    imageVector = iconTriple.first,
                    contentDescription = null,
                    tint = iconTriple.third,
                    modifier = Modifier
                        .size(iconTriple.second.dp)
                        .absoluteOffset(x = rect.left.dp, y = rect.top.dp)
                )
            }
            if (!isConnected) {
                Text(
                    text = "Not connected. Messages will be sent when connection is restored.",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(8.dp)
                )
            }
            if (chatState.error != null) {
                Text(
                    text = chatState.error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            } else if (!chatState.isConnected) {
                Text(
                    text = "Connecting to peer...",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    itemsIndexed(chatState.messages) { index, message ->
                        val prev = chatState.messages.getOrNull(index - 1)
                        val showAvatar = prev == null || prev.isFromMe != message.isFromMe
                        ChatMessageBubble(
                            message = message,
                            dateFormat = dateFormat,
                            modifier = Modifier.fillMaxWidth(),
                            avatarEmoji = if (message.isFromMe) avatarEmojis.getOrNull(
                                peerProfile?.avatarId?.minus(
                                    1
                                ) ?: 13
                            ) ?: "🙂" else peerAvatar,
                            isMe = message.isFromMe,
                            showAvatar = showAvatar
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(
    message: ChatMessage,
    dateFormat: SimpleDateFormat,
    modifier: Modifier = Modifier,
    avatarEmoji: String = "🙂",
    isMe: Boolean = false,
    showAvatar: Boolean = true
) {
    val bubbleColor =
        if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor =
        if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val alignment = if (isMe) Arrangement.End else Arrangement.Start
    val bubbleModifier = if (isMe) {
        modifier
            .fillMaxWidth()
            .padding(start = 42.dp, top = 2.dp, bottom = 2.dp)
    } else {
        modifier
            .fillMaxWidth()
            .padding(end = 42.dp, top = 2.dp, bottom = 2.dp)
    }
    Row(
        modifier = bubbleModifier,
        horizontalArrangement = alignment
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = Color.Transparent,
            modifier = Modifier.weight(1f, fill = false),
        ) {
            val bubbleBg = if (isMe) {
                Modifier.background(
                    MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.medium
                )
            } else {
                Modifier.background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium
                )
            }
            Column(
                modifier = Modifier
                    .then(bubbleBg)
                    .padding(12.dp)
            ) {
                Text(
                    text = message.content,
                    color = textColor,
                    style = MaterialTheme.typography.bodyLarge
                )
                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatFriendlyTimestamp(message.timestamp),
                        color = textColor.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.labelSmall
                    )
                    if (isMe) {
                        Spacer(modifier = Modifier.width(4.dp))
                        when (message.status) {
                            MessageStatus.QUEUED -> {
                                Icon(
                                    imageVector = Icons.Rounded.AccessTime,
                                    contentDescription = "Queued",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            MessageStatus.SENT -> {
                                Icon(
                                    imageVector = Icons.Rounded.Done,
                                    contentDescription = "Sent",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            MessageStatus.DELIVERED -> {
                                Icon(
                                    imageVector = Icons.Rounded.DoneAll,
                                    contentDescription = "Delivered",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            MessageStatus.READ -> {
                                Icon(
                                    imageVector = Icons.Rounded.DoneAll,
                                    contentDescription = "Read",
                                    tint = MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


fun formatFriendlyTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val cal = Calendar.getInstance()
    val msgCal = Calendar.getInstance().apply { timeInMillis = timestamp }
    val nowCal = Calendar.getInstance().apply { timeInMillis = now }

    return when {
        diff < 60_000 -> "now"
        nowCal.get(Calendar.YEAR) == msgCal.get(Calendar.YEAR) &&
        nowCal.get(Calendar.DAY_OF_YEAR) == msgCal.get(Calendar.DAY_OF_YEAR) -> {
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(timestamp)
        }
        nowCal.get(Calendar.YEAR) == msgCal.get(Calendar.YEAR) &&
        nowCal.get(Calendar.DAY_OF_YEAR) - msgCal.get(Calendar.DAY_OF_YEAR) == 1 -> {
            "yesterday"
        }
        else -> {
            SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(timestamp)
        }
    }
}

@Composable
fun ChatScreenPreview() {
    val now = System.currentTimeMillis()
    val oneMinuteAgo = now - 60 * 1000L
    val tenMinutesAgo = now - 10 * 60 * 1000L
    val oneHourAgo = now - 60 * 60 * 1000L
    val todayMorning = now - 6 * 60 * 60 * 1000L
    val yesterday = now - 24 * 60 * 60 * 1000L
    val twoDaysAgo = now - 2 * 24 * 60 * 60 * 1000L
    val lastWeek = now - 7 * 24 * 60 * 60 * 1000L

    val sampleState = ChatState(
        messages = listOf(
            ChatMessage(
                content = "Hey! Did you check the latest design I sent last night?",
                isFromMe = false,
                timestamp = yesterday
            ),
            ChatMessage(
                content = "Oh sorry! Just saw it this morning. Looks really neat ✨",
                isFromMe = true,
                status = MessageStatus.DELIVERED,
                timestamp = todayMorning
            ),
            ChatMessage(
                content = "Thanks 😊 Thinking of tweaking the top section a bit tho.",
                isFromMe = false,
                timestamp = oneHourAgo
            ),
            ChatMessage(
                content = "Hmm, why? I actually liked it that way. Minimal and clean.",
                isFromMe = true,
                status = MessageStatus.SENT,
                timestamp = tenMinutesAgo
            ),
            ChatMessage(
                content = "I felt the heading looked a bit cramped on smaller screens.",
                isFromMe = false,
                timestamp = oneMinuteAgo
            ),
            ChatMessage(
                content = "Fair point. Maybe increase padding slightly? Or use a different type scale?",
                isFromMe = true,
                status = MessageStatus.SENT,
                timestamp = now
            ),
            ChatMessage(
                content = "Exactly what I was thinking. Let me try a couple of variations tonight.",
                isFromMe = false,
                timestamp = now
            ),
            ChatMessage(
                content = "Cool. Also, any update from the backend team? Last I heard, the APIs were lagging.",
                isFromMe = true,
                timestamp = now
            ),
            ChatMessage(
                content = "Yep, Vivek said he'll push an optimized build by tomorrow evening.",
                isFromMe = false,
                timestamp = now
            ),
            ChatMessage(
                content = "Perfect. Let me know if you need me to test anything!",
                isFromMe = true,
                timestamp = now
            ),
            ChatMessage(
                content = "Will do! Also, don't forget the team sync at 5 tomorrow 😅",
                isFromMe = false,
                timestamp = now
            ),
            ChatMessage(
                content = "Alright, catch you later. Peace ✌️",
                isFromMe = false,
                timestamp = now
            ),
            ChatMessage(
                content = "Laters 👋",
                isFromMe = true,
                status = MessageStatus.READ,
                timestamp = now
            ),
            ChatMessage(
                content = "BTW, the Figma links from last week — could you resend?",
                isFromMe = true,
                timestamp = lastWeek
            ),
            ChatMessage(
                content = "Sure, sending now.",
                isFromMe = false,
                timestamp = lastWeek + 5 * 60 * 1000L
            ),
                    ChatMessage(
                content = "Sure, sending now.",
                isFromMe = false,
                timestamp = lastWeek + 5 * 60 * 1000L
            )
        ),
        isConnected = true
    )

    val dummyPeer = UserProfile(nickname = "Vivek", avatarId = 2)

    me.vivekanand.crayscircle.ui.theme.CraysCircleTheme {
        ChatScreen(
            chatState = sampleState,
            onSendMessage = {},
            onBackClick = {},
            peerProfile = dummyPeer
        )
    }
}

@Composable
fun ChatScreenLargeFontPreview() {
    val now = System.currentTimeMillis()
    val sampleState = ChatState(
        messages = listOf(
            ChatMessage(content = "Hello!", isFromMe = false, timestamp = now),
            ChatMessage(content = "Hi!", isFromMe = true, timestamp = now)
        ),
        isConnected = true
    )
    val dummyPeer = UserProfile(nickname = "Vivek", avatarId = 2)
    me.vivekanand.crayscircle.ui.theme.CraysCircleTheme {
        ChatScreen(
            chatState = sampleState,
            onSendMessage = {},
            onBackClick = {},
            peerProfile = dummyPeer
        )
    }
}

@Composable
fun ChatScreenHighContrastPreview() {
    val now = System.currentTimeMillis()
    val sampleState = ChatState(
        messages = listOf(
            ChatMessage(content = "Hello!", isFromMe = false, timestamp = now),
            ChatMessage(content = "Hi!", isFromMe = true, timestamp = now)
        ),
        isConnected = true
    )
    val dummyPeer = UserProfile(nickname = "Vivek", avatarId = 2)
    me.vivekanand.crayscircle.ui.theme.CraysCircleTheme {
        ChatScreen(
            chatState = sampleState,
            onSendMessage = {},
            onBackClick = {},
            peerProfile = dummyPeer
        )
    }
}

@Composable
fun ChatMessageBubblePreview() {
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    me.vivekanand.crayscircle.ui.theme.CraysCircleTheme {
        ChatMessageBubble(
            message = ChatMessage(
                content = "Sample message",
                isFromMe = false,
                timestamp = System.currentTimeMillis()
            ),
            dateFormat = dateFormat
        )
    }
} 