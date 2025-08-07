/*
 * CraysCircle - Peer-to-Peer Communication App
 * Developer: Vivekanand Pandey [@https://www.linkedin.com/in/itsvnp/]
 * Copyright © 2025
 */

package me.vivekanand.crayscircle.setup

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.WifiTethering
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import me.vivekanand.crayscircle.R
import me.vivekanand.crayscircle.ui.PrimaryButton

@Composable
fun WelcomeScreen(
    onUnderstand: () -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(200)
        visible = true
        delay(500)
        showButton = true
    }

    val gradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f),
        ),
        start = Offset(1000f, 0f),
        end = Offset(0f, 2000f)
    )

    Box(
        modifier = modifier
            .systemBarsPadding()
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 })
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Share your",
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "location easily",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                }
                Surface(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(28.dp)
                            .fillMaxSize(),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = "Our upgraded system makes it easier than ever to share your location",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    )
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    )
                }
                AnimatedVisibility(
                    visible = showButton,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
                ) {
                    PrimaryButton(
                        onClick = onUnderstand,
                        text = "Start",
                        modifier = Modifier.fillMaxWidth(0.5f)
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.padding(12.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun OnboardingPageIndicator(pageCount: Int, currentPage: Int, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth()
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .size(if (isSelected) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    )
            )
        }
    }
}

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentPage by remember { mutableStateOf(0) }
    val pages = listOf(
        OnboardingPage(
            icon = Icons.AutoMirrored.Rounded.Chat,
            title = "Welcome to CraysCircle!",
            description = "Your private, instant way to connect and chat with people nearby."
        ),
        OnboardingPage(
            icon = Icons.Rounded.WifiTethering,
            title = "Connect Instantly",
            description = "Discover and chat with people nearby—no internet needed, just Wi-Fi Aware."
        ),
        OnboardingPage(
            icon = Icons.Rounded.Security,
            title = "Private & Secure",
            description = "Your conversations and data stay on your device. No servers, no tracking."
        ),
        OnboardingPage(
            icon = Icons.Rounded.Person,
            title = "Personalize Your Profile",
            description = "Choose a nickname and avatar so friends can recognize you."
        )
    )
    val isLastPage = currentPage == pages.lastIndex
    val gradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f),
        ),
        start = Offset(1000f, 0f),
        end = Offset(0f, 2000f)
    )
    Box(
        modifier = modifier
            .systemBarsPadding()
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = pages[currentPage].title,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Surface(
                modifier = Modifier
                    .size(140.dp)
                    .clip(MaterialTheme.shapes.extraLarge),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                if (currentPage == 0) {
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .padding(28.dp)
                            .fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = pages[currentPage].icon,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(28.dp)
                            .fillMaxSize(),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Text(
                text = pages[currentPage].description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            OnboardingPageIndicator(
                pageCount = pages.size,
                currentPage = currentPage
            )
            PrimaryButton(
                onClick = {
                    if (!isLastPage) currentPage++ else onComplete()
                },
                text = if (!isLastPage) "Next" else "Start",
                modifier = Modifier.fillMaxWidth(0.7f)
            )
        }
    }
}

private data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val description: String
)

@Composable
fun WelcomeScreenPreview() {
    me.vivekanand.crayscircle.ui.theme.CraysCircleTheme {
        WelcomeScreen(onUnderstand = {})
    }
}

@Composable
fun WelcomeScreenLargeFontPreview() {
    me.vivekanand.crayscircle.ui.theme.CraysCircleTheme {
        WelcomeScreen(onUnderstand = {})
    }
}

@Composable
fun WelcomeScreenHighContrastPreview() {
    me.vivekanand.crayscircle.ui.theme.CraysCircleTheme(darkTheme = true) {
        WelcomeScreen(onUnderstand = {})
    }
}

@Composable
fun OnboardingScreenPreview() {
    me.vivekanand.crayscircle.ui.theme.CraysCircleTheme {
        OnboardingScreen(onComplete = {})
    }
}

@Composable
fun OnboardingScreenLargeFontPreview() {
    me.vivekanand.crayscircle.ui.theme.CraysCircleTheme {
        OnboardingScreen(onComplete = {})
    }
}

@Composable
fun OnboardingScreenHighContrastPreview() {
    me.vivekanand.crayscircle.ui.theme.CraysCircleTheme(darkTheme = true) {
        OnboardingScreen(onComplete = {})
    }
}