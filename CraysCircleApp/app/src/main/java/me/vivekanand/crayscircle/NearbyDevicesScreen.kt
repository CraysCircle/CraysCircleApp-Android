/*
 * CraysCircle - Peer-to-Peer Communication App
 * Developer: Vivekanand Pandey [@https://www.linkedin.com/in/itsvnp/]
 * Copyright © 2025
 */

package me.vivekanand.crayscircle

import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import me.vivekanand.crayscircle.ui.theme.CraysCircleTheme
import me.vivekanand.crayscircle.wifi.PeerDevice

@Composable
fun SwipeRefreshLayoutCompat(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            val swipeRefreshLayout = SwipeRefreshLayout(context)
            val frameLayout = FrameLayout(context)
            swipeRefreshLayout.addView(frameLayout)
            swipeRefreshLayout.setOnRefreshListener { onRefresh() }
            val composeView = ComposeView(context)
            frameLayout.addView(composeView)
            swipeRefreshLayout.tag = composeView
            swipeRefreshLayout
        },
        update = { swipeRefreshLayout ->
            swipeRefreshLayout.isRefreshing = isRefreshing
            val composeView = swipeRefreshLayout.tag as ComposeView
            composeView.setContent {
                content()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyDevicesScreen(
    discoveredPeers: List<PeerDevice>,
    onPeerSelected: (PeerDevice) -> Unit,
    modifier: Modifier = Modifier,
    userProfile: me.vivekanand.crayscircle.data.UserProfile? = null,
    onProfileClick: () -> Unit = {},
    onRefresh: () -> Unit = {},
    isRefreshing: Boolean = false,
    pastPeers: List<PeerDevice> = emptyList(),
    onPeerHistorySelected: (PeerDevice) -> Unit = {},
    isAutoConnectEnabled: Boolean = false,
    onAutoConnectToggle: (Boolean) -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    var showHistorySheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    Scaffold(
        modifier = modifier.systemBarsPadding(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val avatarEmojis = listOf(
                            "😀",
                            "🦄",
                            "🐱",
                            "🤖",
                            "👽",
                            "🦊",
                            "🐧",
                            "🐸",
                            "🐼",
                            "🦁",
                            "🐵",
                            "🧑‍🎤",
                            "🧙‍♂️",
                            "🧑‍🚀",
                            "🧑‍💻"
                        )
                        val avatarEmoji = userProfile?.let { avatarEmojis.getOrNull(it.avatarId - 1) } ?: "🧑‍💻"
                        Text(
                            text = avatarEmoji,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        if (userProfile != null && userProfile.nickname.isNotBlank()) {
                            Text(
                                text = userProfile.nickname,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            imageVector = Icons.Rounded.Person,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Switch(
                        checked = isAutoConnectEnabled,
                        onCheckedChange = onAutoConnectToggle,
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showHistorySheet = true },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.large)
            ) {
                Icon(Icons.Rounded.History, contentDescription = "Chat History", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { innerPadding ->
        SwipeRefreshLayoutCompat(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (discoveredPeers.isEmpty() || isRefreshing) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.medium
                            ),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Let's see who's near you...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(discoveredPeers) { peer ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium),
                                    onClick = { onPeerSelected(peer) },
                                    shape = MaterialTheme.shapes.large
                                ) {
                                    ListItem(
                                        leadingContent = {
                                            val avatarEmojis = listOf(
                                                "😀",
                                                "🦄",
                                                "🐱",
                                                "🤖",
                                                "👽",
                                                "🦊",
                                                "🐧",
                                                "🐸",
                                                "🐼",
                                                "🦁",
                                                "🐵",
                                                "🧑‍🎤",
                                                "🧙‍♂️",
                                                "🧑‍🚀",
                                                "🧑‍💻"
                                            )
                                            val avatarEmoji = avatarEmojis.getOrNull(peer.avatarId - 1) ?: "🧑‍💻"
                                            Text(
                                                text = avatarEmoji,
                                                style = MaterialTheme.typography.headlineMedium,
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                                    .padding(4.dp),
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        },
                                        headlineContent = {
                                            Column {
                                                Text(peer.nickname)
                                                Text(
                                                    text = peer.status,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = when (peer.status) {
                                                        "Connected" -> Color(0xFF4CAF50)
                                                        "Connecting" -> Color(0xFFFFA000)
                                                        else -> Color.Gray
                                                    }
                                                )
                                            }
                                        },
                                        supportingContent = {
                                            val distance = peer.distance
                                            val distanceText = if (distance != null) {
                                                when {
                                                    distance < 1 -> "~${(distance * 100).toInt()}cm away"
                                                    distance < 1000 -> "~${distance.toInt()}m away"
                                                    else -> "~%.2fkm away".format(distance / 1000)
                                                }
                                            } else {
                                                "live, Tap to connect"
                                            }
                                            Text(text = "${peer.nickname} is $distanceText")
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        if (showHistorySheet) {
            ModalBottomSheet(
                onDismissRequest = { showHistorySheet = false },
                sheetState = sheetState
            ) {
                ChatHistorySheet(
                    pastPeers = pastPeers,
                    onPeerSelected = {
                        showHistorySheet = false
                        onPeerHistorySelected(it)
                    }
                )
            }
        }
    }
}

@Composable
fun ChatHistorySheet(
    pastPeers: List<PeerDevice>,
    onPeerSelected: (PeerDevice) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Chat History",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        if (pastPeers.isEmpty()) {
            Text(text = "No past chats yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            pastPeers.forEach { peer ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
                        .clickable { onPeerSelected(peer) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val avatarEmojis = listOf(
                        "😀", "🦄", "🐱", "🤖", "👽", "🦊", "🐧", "🐸", "🐼", "🦁", "🐵", "🧑‍🎤", "🧙‍♂️", "🧑‍🚀", "🧑‍💻"
                    )
                    val avatarEmoji = avatarEmojis.getOrNull(peer.avatarId - 1) ?: "🧑‍💻"
                    Text(
                        text = avatarEmoji,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(4.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = peer.nickname,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun NearbyDevicesScreenLight() {
    val samplePeers = listOf(
        PeerDevice(uniqueId = java.util.UUID.randomUUID().toString(), nickname = "Alice", avatarId = 2, gender = me.vivekanand.crayscircle.data.Gender.FEMALE, handle = null),
        PeerDevice(uniqueId = java.util.UUID.randomUUID().toString(), nickname = "Bob", avatarId = 5, gender = me.vivekanand.crayscircle.data.Gender.MALE, handle = null),
        PeerDevice(uniqueId = java.util.UUID.randomUUID().toString(), nickname = "Vivek", avatarId = 8, gender = me.vivekanand.crayscircle.data.Gender.MALE, handle = null)
    )
    CraysCircleTheme {
        NearbyDevicesScreen(
            discoveredPeers = samplePeers,
            onPeerSelected = {},
        )
    }
}

@Composable
fun NearbyDevicesScreenLargeFontPreview() {
    val samplePeers = listOf(
        PeerDevice(uniqueId = java.util.UUID.randomUUID().toString(), nickname = "Alice", avatarId = 2, gender = me.vivekanand.crayscircle.data.Gender.FEMALE, handle = null),
        PeerDevice(uniqueId = java.util.UUID.randomUUID().toString(), nickname = "Bob", avatarId = 5, gender = me.vivekanand.crayscircle.data.Gender.MALE, handle = null),
        PeerDevice(uniqueId = java.util.UUID.randomUUID().toString(), nickname = "Vivek", avatarId = 8, gender = me.vivekanand.crayscircle.data.Gender.MALE, handle = null)
    )
    CraysCircleTheme {
        NearbyDevicesScreen(
            discoveredPeers = samplePeers,
            onPeerSelected = {},
        )
    }
}

@Composable
fun NearbyDevicesScreenHighContrastPreview() {
    val samplePeers = listOf(
        PeerDevice(uniqueId = java.util.UUID.randomUUID().toString(), nickname = "Alice", avatarId = 2, gender = me.vivekanand.crayscircle.data.Gender.FEMALE, handle = null),
        PeerDevice(uniqueId = java.util.UUID.randomUUID().toString(), nickname = "Bob", avatarId = 5, gender = me.vivekanand.crayscircle.data.Gender.MALE, handle = null),
        PeerDevice(uniqueId = java.util.UUID.randomUUID().toString(), nickname = "Vivek", avatarId = 8, gender = me.vivekanand.crayscircle.data.Gender.MALE, handle = null)
    )
    CraysCircleTheme {
        NearbyDevicesScreen(
            discoveredPeers = samplePeers,
            onPeerSelected = {},
        )
    }
}

@Composable
fun NearbyDevicesScreenDark() {
    val samplePeers = listOf(
        PeerDevice(uniqueId = java.util.UUID.randomUUID().toString(), nickname = "Alice", avatarId = 2, gender = me.vivekanand.crayscircle.data.Gender.FEMALE, handle = null),
        PeerDevice(uniqueId = java.util.UUID.randomUUID().toString(), nickname = "Bob", avatarId = 5, gender = me.vivekanand.crayscircle.data.Gender.MALE, handle = null),
        PeerDevice(uniqueId = java.util.UUID.randomUUID().toString(), nickname = "Vivek", avatarId = 8, gender = me.vivekanand.crayscircle.data.Gender.MALE, handle = null)
    )
    CraysCircleTheme {
        NearbyDevicesScreen(
            discoveredPeers = samplePeers,
            onPeerSelected = {},
        )
    }
}

