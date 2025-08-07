/*
 * CraysCircle - Peer-to-Peer Communication App
 * Developer: Vivekanand Pandey [@https://www.linkedin.com/in/itsvnp/]
 * Copyright © 2025
 */

package me.vivekanand.crayscircle

import android.Manifest
import android.content.Context
import android.net.wifi.aware.WifiAwareManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.vivekanand.crayscircle.chat.ChatScreen
import me.vivekanand.crayscircle.chat.MessageStatus
import me.vivekanand.crayscircle.data.ChatDatabase
import me.vivekanand.crayscircle.data.MessageEntity
import me.vivekanand.crayscircle.data.UserPreferencesRepository
import me.vivekanand.crayscircle.data.UserProfile
import me.vivekanand.crayscircle.setup.OnboardingScreen
import me.vivekanand.crayscircle.ui.OutlinedAppButton
import me.vivekanand.crayscircle.ui.PrimaryButton
import me.vivekanand.crayscircle.ui.ProfileScreen
import me.vivekanand.crayscircle.ui.theme.CraysCircleTheme
import me.vivekanand.crayscircle.wifi.PeerDevice
import me.vivekanand.crayscircle.wifi.WifiAwareController

class MainActivity : ComponentActivity() {
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var chatDatabase: ChatDatabase
    private var allLoaded: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !allLoaded }
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        userPreferencesRepository = UserPreferencesRepository(this)
        chatDatabase = ChatDatabase.getDatabase(this)
        setContent {
            CraysCircleTheme {
                MainScreen(
                    userPreferencesRepository = userPreferencesRepository,
                    chatDatabase = chatDatabase,
                    onStateLoaded = { loaded -> allLoaded = loaded }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up resources - no need to set to null as they're lateinit
        // The garbage collector will handle cleanup
    }
}

@Composable
fun SplashScreen(visible: Boolean) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(500)),
        exit = fadeOut(animationSpec = tween(500))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(120.dp)
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    userPreferencesRepository: UserPreferencesRepository,
    chatDatabase: ChatDatabase,
    onStateLoaded: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val permissions = buildList {
        add(Manifest.permission.ACCESS_FINE_LOCATION)
        add(Manifest.permission.ACCESS_WIFI_STATE)
        add(Manifest.permission.CHANGE_WIFI_STATE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.NEARBY_WIFI_DEVICES)
        }
    }
    val permissionsState = rememberMultiplePermissionsState(permissions)
    var isReady by remember { mutableStateOf(false) }
    var selectedPeer by remember { mutableStateOf<PeerDevice?>(null) }
    var isAutoConnectEnabled by remember { mutableStateOf(false) }

    val hasSeenOnboarding by userPreferencesRepository.hasSeenOnboardingFlow.collectAsState(initial = null)
    val hasGrantedPermissions by userPreferencesRepository.hasGrantedPermissionsFlow.collectAsState(initial = null)
    val isDeviceCompatible by userPreferencesRepository.isDeviceCompatibleFlow.collectAsState(initial = null)
    val userProfile by userPreferencesRepository.userProfileFlow.collectAsState(initial = null)

    val allLoaded = hasSeenOnboarding != null && hasGrantedPermissions != null && isDeviceCompatible != null && userProfile != null

    val currentProfile = userProfile
    val wifiAwareController = remember(currentProfile) {
        WifiAwareController(context, { currentProfile }, chatDatabase)
    }
    val discoveredPeers by wifiAwareController.discoveredPeers.collectAsState()
    val chatState by wifiAwareController.chatState.collectAsState()

    if (isDeviceCompatible == null) {
        LaunchedEffect(Unit) {
            val supported = isWifiAwareSupported(context)
            userPreferencesRepository.setIsDeviceCompatible(supported)
        }
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LaunchedEffect(chatState.messages) {
        selectedPeer?.let { peer ->
            chatState.messages.forEach { message ->
                chatDatabase.messageDao().insertMessage(
                    MessageEntity(
                        id = message.id,
                        peerId = peer.id.toString(),
                        content = message.content,
                        isFromMe = message.isFromMe,
                        timestamp = message.timestamp,
                        status = message.status.name
                    )
                )
            }
        }
    }

    LaunchedEffect(selectedPeer) {
        selectedPeer?.let { peer ->
            val messages = chatDatabase.messageDao().getMessagesForPeer(peer.id.toString()).first()
            messages.forEach { message ->
                wifiAwareController.addHistoricalMessage(
                    content = message.content,
                    isFromMe = message.isFromMe,
                    timestamp = message.timestamp,
                    status = MessageStatus.valueOf(message.status)
                )
            }
        }
    }

    when {
        hasSeenOnboarding == false -> {
            OnboardingScreen(
                onComplete = {
                    coroutineScope.launch { userPreferencesRepository.setHasSeenOnboarding(true) }
                },
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            )
        }
        hasGrantedPermissions == false -> {
            PermissionsRequest(
                onRequestPermissions = {
                    permissionsState.launchMultiplePermissionRequest()
                    coroutineScope.launch { userPreferencesRepository.setHasGrantedPermissions(true) }
                },
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            )
        }
        currentProfile == null || currentProfile.hasCompletedSetup == false -> {
            ProfileScreen(
                userProfile = currentProfile ?: UserProfile(),
                onProfileChange = { profile ->
                    coroutineScope.launch {
                        userPreferencesRepository.saveUserProfile(profile)
                    }
                },
                onLogout = {
                    coroutineScope.launch {
                        userPreferencesRepository.logout()
                    }
                },
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            )
        }
        isDeviceCompatible == false -> {
            SupportErrorScreen(
                onRecheck = {
                    val supported = isWifiAwareSupported(context)
                    coroutineScope.launch { userPreferencesRepository.setIsDeviceCompatible(supported) }
                    if (supported) {
                        wifiAwareController.startDiscovery()
                        isReady = true
                    }
                },
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            )
        }
        else -> {
            var showProfile by remember { mutableStateOf(false) }
            if (showProfile) {
                ProfileScreen(
                    userProfile = currentProfile!!,
                    onProfileChange = { profile ->
                        coroutineScope.launch { userPreferencesRepository.saveUserProfile(profile) }
                    },
                    onLogout = {
                        coroutineScope.launch {
                            userPreferencesRepository.logout()
                            showProfile = false
                        }
                    },
                    onBackClick = { showProfile = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.background)
                )
            } else if (selectedPeer != null) {
                LaunchedEffect(selectedPeer) {
                    wifiAwareController.connectToPeer(selectedPeer!!)
                    wifiAwareController.loadQueuedMessages(selectedPeer!!.id.toString())
                }
                ChatScreen(
                    chatState = chatState,
                    onSendMessage = { message ->
                        wifiAwareController.sendMessage(message)
                    },
                    onBackClick = {
                        wifiAwareController.disconnectFromPeer()
                        selectedPeer = null
                    },
                    isConnected = wifiAwareController.isCurrentlyConnected(),
                    peerProfile = selectedPeer?.let { peer ->
                        UserProfile(
                            nickname = peer.nickname,
                            avatarId = peer.avatarId,
                            gender = peer.gender
                        )
                    },
                    onClearError = { wifiAwareController.clearError() }
                )
            } else {
                var isRefreshing = false
                if (currentProfile != null && currentProfile.hasCompletedSetup && currentProfile.uniqueId.isNotBlank()) {
                    LaunchedEffect(currentProfile.uniqueId) {
                        wifiAwareController.startDiscovery()
                    }
                }
                NearbyDevicesScreen(
                    discoveredPeers = discoveredPeers,
                    onPeerSelected = { peer ->
                        selectedPeer = peer
                        wifiAwareController.connectToPeer(peer)
                    },
                    userProfile = currentProfile,
                    onProfileClick = { showProfile = true },
                    onRefresh = { wifiAwareController.startDiscovery() },
                    isRefreshing = isRefreshing,
                    isAutoConnectEnabled = isAutoConnectEnabled,
                    onAutoConnectToggle = { enabled ->
                        isAutoConnectEnabled = enabled
                        wifiAwareController.setAutoConnectEnabled(enabled)
                    }
                )
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            wifiAwareController.stopDiscovery()
        }
    }

    onStateLoaded(allLoaded)
}

@Composable
fun PermissionsRequest(
    onRequestPermissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .systemBarsPadding()
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .widthIn(min = 280.dp)
                .heightIn(min = 180.dp)
                .border(1.5.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = "Permissions",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "Permissions Required",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "CraysCircle needs location and Wi-Fi permissions to discover and connect with nearby devices for peer-to-peer chat.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                PrimaryButton(
                    onClick = onRequestPermissions,
                    text = "Grant Permissions"
                )
            }
        }
    }
}

@Composable
fun SupportErrorScreen(
    onRecheck: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .systemBarsPadding()
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .widthIn(min = 280.dp)
                .heightIn(min = 180.dp)
                .border(1.5.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Wi-Fi Aware",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                
                Text(
                    text = "Wi-Fi Aware Not Supported",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                
                Text(
                    text = "Your device doesn't support Wi-Fi Aware (NAN), which is required for peer-to-peer communication. Please ensure Wi-Fi is enabled and try again.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                
                OutlinedAppButton(
                    onClick = onRecheck,
                    text = "Recheck Support",
                    borderColor = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

fun isWifiAwareSupported(context: Context): Boolean {
    val manager = context.getSystemService(Context.WIFI_AWARE_SERVICE) as? WifiAwareManager
    return manager?.isAvailable == true
}

@Composable
fun MainScreenPreview() {
    CraysCircleTheme {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "MainScreen Preview")
        }
    }
}

@Composable
fun PermissionsRequestPreview() {
    CraysCircleTheme {
        PermissionsRequest(onRequestPermissions = {})
    }
}

@Composable
fun SupportErrorScreenPreview() {
    CraysCircleTheme {
        SupportErrorScreen(onRecheck = {})
    }
}
