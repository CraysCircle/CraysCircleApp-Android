/*
 * CraysCircle - Peer-to-Peer Communication App
 * Developer: Vivekanand Pandey [@https://www.linkedin.com/in/itsvnp/]
 * Copyright © 2025
 */

@file:OptIn(ExperimentalMaterial3Api::class)

package me.vivekanand.crayscircle.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import me.vivekanand.crayscircle.data.Gender
import me.vivekanand.crayscircle.data.UserProfile
import me.vivekanand.crayscircle.ui.theme.CraysCircleTheme
import kotlin.random.Random

private val avatarEmojis = listOf(
    "😀", "🦄", "🐱", "🤖", "👽", "🦊", "🐧", "🐸", "🐶", "🦁", "🐵", "🧑‍🎤", "🧙‍♂️", "🧑‍🚀", "🧑‍💻"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userProfile: UserProfile,
    onProfileChange: (UserProfile) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val showSheet = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val isSetup = userProfile.hasCompletedSetup
    val avatarEmoji = avatarEmojis.getOrNull(userProfile.avatarId - 1) ?: "🧑‍💻"
    val joinDate = "recently"

    if (!userProfile.hasCompletedSetup) {
        Surface(modifier = modifier.fillMaxSize().systemBarsPadding()) {
            ProfileEditSheet(
                initialProfile = userProfile,
                onSave = { onProfileChange(it) },
                onCancel = onLogout
            )
        }
        return
    }

    Scaffold(
        modifier = modifier.systemBarsPadding(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    if (onBackClick != null) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .systemBarsPadding()
                    .shadow(
                        elevation = 0.dp,
                        spotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(topStart = 8.dp, bottomStart = 32.dp, topEnd = 8.dp, bottomEnd = 32.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f)
                            )
                        )
                    )
            ) {
                val coverIcons = listOf(
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
                val coverColors = listOf(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f),
                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.10f),
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.18f),
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f),
                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.12f),
                    MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.12f)
                )
                val iconCount = remember { (5..10).random() }
                val coverWidth = 360f
                val coverHeight = 140f
                val cols = kotlin.math.ceil(kotlin.math.sqrt(iconCount.toFloat())).toInt()
                val rows = kotlin.math.ceil(iconCount / cols.toFloat()).toInt()
                val cellWidth = coverWidth / cols
                val cellHeight = coverHeight / rows
                val random = remember { Random(System.currentTimeMillis()) }
                val iconData = List(iconCount) { idx ->
                    val icon = coverIcons.random(random)
                    val color = coverColors.random(random)
                    val col = idx % cols
                    val row = idx / cols
                    val maxSize = minOf(cellWidth, cellHeight) * 0.8f
                    val minSize = minOf(cellWidth, cellHeight) * 0.5f
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
            }
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = avatarEmoji,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = userProfile.nickname,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (userProfile.fullName.isNotBlank()) {
                    Text(
                        text = userProfile.fullName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (userProfile.location.isNotBlank()) {
                    Text(
                        text = userProfile.location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "Joined $joinDate",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            if (userProfile.interests.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(userProfile.interests) { interest ->
                        Surface(
                            modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = interest,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            if (userProfile.bio.isNotBlank()) {
                Text(
                    text = userProfile.bio,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (userProfile.website.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Language,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = userProfile.website,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    if (userProfile.email.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.AlternateEmail,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = userProfile.email,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    if (userProfile.phone.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Phone,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = userProfile.phone,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            PrimaryButton(
                onClick = { showSheet.value = true },
                text = "Edit Profile",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedAppButton(
                onClick = onLogout,
                text = "Logout",
                modifier = Modifier.fillMaxWidth(),
                borderColor = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (showSheet.value) {
            ModalBottomSheet(
                onDismissRequest = {
                    showSheet.value = false
                },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxHeight()
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                ProfileEditSheet(
                    initialProfile = userProfile,
                    onSave = {
                        onProfileChange(it)
                        showSheet.value = false
                    },
                    onCancel = {
                        showSheet.value = false
                    }
                )
            }
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String, onClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .let {
                if (onClick != null) it.clickable { onClick() } else it
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun ProfileEditSheet(
    initialProfile: UserProfile,
    onSave: (UserProfile) -> Unit,
    onCancel: () -> Unit
) {
    var nickname by remember { mutableStateOf(initialProfile.nickname) }
    var fullName by remember { mutableStateOf(initialProfile.fullName) }
    var selectedGender by remember { mutableStateOf(initialProfile.gender) }
    var selectedAvatarId by remember { mutableStateOf(initialProfile.avatarId) }
    var bio by remember { mutableStateOf(initialProfile.bio) }
    var website by remember { mutableStateOf(initialProfile.website) }
    var interests by remember { mutableStateOf(initialProfile.interests.joinToString(", ")) }
    var showError by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf(initialProfile.email ?: "") }
    var phone by remember { mutableStateOf(initialProfile.phone ?: "") }
    var location by remember { mutableStateOf(initialProfile.location ?: "") }

    val isInitialSetup = !initialProfile.hasCompletedSetup
    val coroutineScope = rememberCoroutineScope()

    val chipColors = listOf(
        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
        MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.18f),
        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.18f),
        MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.18f),
        Color(0xFFE3F2FD), 
        Color(0xFFFFF9C4), 
        Color(0xFFFFE0B2), 
        Color(0xFFC8E6C9), 
        Color(0xFFFFCDD2), 
        Color(0xFFD1C4E9), 
        Color(0xFFFFF8E1), 
        Color(0xFFB2EBF2), 
        Color(0xFFFFF3E0), 
        Color(0xFFE1BEE7), 
        Color(0xFFD7CCC8)  
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .imePadding(),
        contentPadding = PaddingValues(vertical = 24.dp)
    ) {
        item {
            Text(
                text = if (isInitialSetup) "Create Your Profile" else "Edit Profile",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (isInitialSetup) {
                Text(
                    text = "Choose an avatar and set your nickname to get started",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

        }

        
        item {
            Text(
                text = "Profile Picture",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
            ) {
                avatarEmojis.forEachIndexed { idx, emoji ->
                    val emojiId = idx + 1
                    val selected = selectedAvatarId == emojiId
                    Surface(
                        modifier = Modifier
                            .size(if (selected) 64.dp else 56.dp)
                            .clip(CircleShape)
                            .clickable { selectedAvatarId = emojiId },
                        color = if (selected)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surface,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = emoji,
                                fontSize = if (selected) 36.sp else 32.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = MaterialTheme.shapes.large,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Basic Information",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    
                    if (!isInitialSetup) {
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Full Name") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusEvent { event ->
                                    if (event.isFocused) {
                                        coroutineScope.launch {
                                            
                                        }
                                    }
                                },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Person,
                                    contentDescription = "Full Name"
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    
                    OutlinedTextField(
                        value = nickname,
                        onValueChange = { newValue ->
                            nickname = newValue.trim()
                            showError = newValue.trim().length < 3
                        },
                        label = { Text("Nickname") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusEvent { event ->
                                if (event.isFocused) {
                                    coroutineScope.launch {
                                        
                                    }
                                }
                            },
                        isError = showError,
                        supportingText = if (showError) {
                            { Text("Nickname must be at least 3 characters") }
                        } else null,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.AlternateEmail,
                                contentDescription = "Nickname"
                            )
                        }
                    )

                    if (!isInitialSetup) {
                        Spacer(modifier = Modifier.height(16.dp))

                        
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            val genderDisplayName: (Gender) -> String = { gender ->
                                when (gender) {
                                    Gender.PREFER_NOT_TO_SAY -> "Not set"
                                    Gender.MALE -> "Male"
                                    Gender.FEMALE -> "Female"
                                    Gender.NON_BINARY -> "Non-binary"
                                    Gender.TRANSGENDER -> "Transgender"
                                    Gender.OTHER -> "Plus+"
                                }
                            }
                            OutlinedTextField(
                                value = genderDisplayName(selectedGender),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Gender") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Person,
                                        contentDescription = "Gender"
                                    )
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Not set") },
                                    onClick = {
                                        selectedGender = Gender.PREFER_NOT_TO_SAY
                                        expanded = false
                                    }
                                )
                                Gender.values().filter { it != Gender.PREFER_NOT_TO_SAY }.forEach { gender ->
                                    DropdownMenuItem(
                                        text = { Text(genderDisplayName(gender)) },
                                        onClick = {
                                            selectedGender = gender
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = MaterialTheme.shapes.large,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Additional Information",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { newValue -> bio = newValue },
                        label = { Text("Bio") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min), 
                        minLines = 3,
                        maxLines = 5,
                        leadingIcon = {
                            Box(
                                contentAlignment = Alignment.TopCenter,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(top = 16.dp) 
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Description,
                                    contentDescription = "Bio"
                                )
                            }
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    
                    OutlinedTextField(
                        value = interests,
                        onValueChange = { newValue -> interests = newValue },
                        label = { Text("Interests (comma-separated)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusEvent { event ->
                                if (event.isFocused) {
                                    coroutineScope.launch {
                                        
                                    }
                                }
                            },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Favorite,
                                contentDescription = "Interests"
                            )
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    
                    OutlinedTextField(
                        value = website,
                        onValueChange = { newValue -> website = newValue },
                        label = { Text("Website") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusEvent { event ->
                                if (event.isFocused) {
                                    coroutineScope.launch {
                                        
                                    }
                                }
                            },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Uri,
                            imeAction = ImeAction.Done
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Language,
                                contentDescription = "Website"
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    
                    OutlinedTextField(
                        value = email,
                        onValueChange = { newValue -> email = newValue },
                        label = { Text("Email") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusEvent { event ->
                                if (event.isFocused) {
                                    coroutineScope.launch {
                                        
                                    }
                                }
                            },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Person,
                                contentDescription = "Email"
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { newValue -> phone = newValue },
                        label = { Text("Phone") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusEvent { event ->
                                if (event.isFocused) {
                                    coroutineScope.launch {
                                        
                                    }
                                }
                            },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Next
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Phone,
                                contentDescription = "Phone"
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = location,
                        onValueChange = { newValue -> location = newValue },
                        label = { Text("Location") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusEvent { event ->
                                if (event.isFocused) {
                                    coroutineScope.launch {
                                        
                                    }
                                }
                            },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = "Location"
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (initialProfile.hasCompletedSetup) {
                    OutlinedAppButton(
                        onClick = onCancel,
                        text = "Cancel",
                        modifier = Modifier.weight(1f)
                    )
                }

                PrimaryButton(
                    onClick = {
                        if (nickname.length >= 3) {
                            onSave(
                                UserProfile(
                                    nickname = nickname,
                                    fullName = fullName,
                                    gender = selectedGender,
                                    avatarId = selectedAvatarId,
                                    bio = bio,
                                    interests = interests.split(",")
                                        .map { it.trim() }
                                        .filter { it.isNotEmpty() },
                                    website = website,
                                    email = email,
                                    phone = phone,
                                    location = location,
                                    hasCompletedSetup = true
                                )
                            )
                        } else {
                            showError = true
                        }
                    },
                    text = "Save",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun AdaptiveChips(interests: List<String>) {
    val chipsPerRow = 3 
    val chipColors = listOf(
        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
        MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.18f),
        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.18f),
        MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.18f),
        Color(0xFFE3F2FD), 
        Color(0xFFFFF9C4), 
        Color(0xFFFFE0B2), 
        Color(0xFFC8E6C9), 
        Color(0xFFFFCDD2), 
        Color(0xFFD1C4E9), 
        Color(0xFFFFF8E1), 
        Color(0xFFB2EBF2), 
        Color(0xFFFFF3E0), 
        Color(0xFFE1BEE7), 
        Color(0xFFD7CCC8)  
    )
    Column {
        interests.chunked(chipsPerRow).forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                row.forEach { interest ->
                    val bgColor = remember(interest) { chipColors.random() }
                    Surface(
                        color = bgColor,
                        shape = MaterialTheme.shapes.small,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = interest,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PreviewProfileScreenCompletedLight() {
    CraysCircleTheme {
        Surface {
            ProfileScreen(
                userProfile = UserProfile(
                    nickname = "Arnoldy",
                    avatarId = 1,
                    gender = Gender.MALE,
                    fullName = "Arnoldy Chafe",
                    bio = "CEO System D, Because your satisfaction is everything & Standing out from the rest, and that's what we want you to be as well.",
                    interests = listOf("UI Designer", "UX Designer", "Design System", "Product", "Succesfull"),
                    website = "www.Arnoldy.com",
                    email = "arnoldy@email.com",
                    phone = "+1 234 567 8901",
                    location = "San Francisco, CA",
                    hasCompletedSetup = true
                ),
                onProfileChange = {},
                onLogout = {},
                onBackClick = {}
            )
        }
    }
}

@Composable
fun PreviewProfileScreenInitialLight() {
    CraysCircleTheme {
        Surface {
            ProfileScreen(
                userProfile = UserProfile(
                    nickname = "",
                    avatarId = 1,
                    gender = Gender.PREFER_NOT_TO_SAY,
                    hasCompletedSetup = false
                ),
                onProfileChange = {},
                onLogout = {},
                onBackClick = {}
            )
        }
    }
}

@Composable
fun PreviewProfileScreenCompletedDark() {
    CraysCircleTheme {
        Surface {
            ProfileScreen(
                userProfile = UserProfile(
                    nickname = "Arnoldy",
                    avatarId = 2,
                    gender = Gender.FEMALE,
                    fullName = "Arnoldy Chafe",
                    bio = "CEO System D, Because your satisfaction is everything & Standing out from the rest, and that's what we want you to be as well.",
                    interests = listOf("UI Designer", "UX Designer", "Design System", "Product", "Succesfull"),
                    website = "www.Arnoldy.com",
                    email = "arnoldy@email.com",
                    phone = "+1 234 567 8901",
                    location = "New York, NY",
                    hasCompletedSetup = true
                ),
                onProfileChange = {},
                onLogout = {},
                onBackClick = {}
            )
        }
    }
}

@Composable
fun PreviewProfileEditSheetLight() {
    CraysCircleTheme {
        Surface {
            ProfileEditSheet(
                initialProfile = UserProfile(
                    nickname = "John Doe",
                    avatarId = 1,
                    gender = Gender.NON_BINARY,
                    fullName = "John Doe",
                    bio = "Product designer and developer.",
                    interests = listOf("Design", "Code", "Music"),
                    website = "www.johndoe.com",
                    email = "john@email.com",
                    phone = "+1 555 123 4567",
                    location = "Los Angeles, CA",
                    hasCompletedSetup = true
                ),
                onSave = {},
                onCancel = {}
            )
        }
    }
}

@Composable
fun PreviewProfileEditSheetInitialDark() {
    CraysCircleTheme {
        Surface {
            ProfileEditSheet(
                initialProfile = UserProfile(
                    nickname = "",
                    avatarId = 1,
                    gender = Gender.PREFER_NOT_TO_SAY,
                    hasCompletedSetup = false
                ),
                onSave = {},
                onCancel = {}
            )
        }
    }
}

@Composable
fun PreviewProfileScreenCompletedLargeFont() {
    CraysCircleTheme {
        Surface {
            ProfileScreen(
                userProfile = UserProfile(
                    nickname = "Arnoldy",
                    avatarId = 1,
                    gender = Gender.MALE,
                    fullName = "Arnoldy Chafe",
                    bio = "CEO System D, Because your satisfaction is everything & Standing out from the rest, and that's what we want you to be as well.",
                    interests = listOf("UI Designer", "UX Designer", "Design System", "Product", "Succesfull"),
                    website = "www.Arnoldy.com",
                    hasCompletedSetup = true
                ),
                onProfileChange = {},
                onLogout = {},
                onBackClick = {}
            )
        }
    }
}

@Composable
fun PreviewProfileScreenCompletedHighContrast() {
    CraysCircleTheme {
        Surface {
            ProfileScreen(
                userProfile = UserProfile(
                    nickname = "Arnoldy",
                    avatarId = 1,
                    gender = Gender.MALE,
                    fullName = "Arnoldy Chafe",
                    bio = "CEO System D, Because your satisfaction is everything & Standing out from the rest, and that's what we want you to be as well.",
                    interests = listOf("UI Designer", "UX Designer", "Design System", "Product", "Succesfull"),
                    website = "www.Arnoldy.com",
                    hasCompletedSetup = true
                ),
                onProfileChange = {},
                onLogout = {},
                onBackClick = {}
            )
        }
    }
}

@Composable
fun PreviewProfileScreenInitialLargeFont() {
    CraysCircleTheme {
        Surface {
            ProfileScreen(
                userProfile = UserProfile(
                    nickname = "",
                    avatarId = 1,
                    gender = Gender.PREFER_NOT_TO_SAY,
                    hasCompletedSetup = false
                ),
                onProfileChange = {},
                onLogout = {},
                onBackClick = {}
            )
        }
    }
}

@Composable
fun PreviewProfileScreenInitialHighContrast() {
    CraysCircleTheme {
        Surface {
            ProfileScreen(
                userProfile = UserProfile(
                    nickname = "",
                    avatarId = 1,
                    gender = Gender.PREFER_NOT_TO_SAY,
                    hasCompletedSetup = false
                ),
                onProfileChange = {},
                onLogout = {},
                onBackClick = {}
            )
        }
    }
}

@Composable
fun PreviewProfileEditSheetCreateProfileLight() {
    CraysCircleTheme {
        Surface {
            ProfileEditSheet(
                initialProfile = UserProfile(
                    nickname = "",
                    avatarId = 1,
                    gender = Gender.PREFER_NOT_TO_SAY,
                    hasCompletedSetup = false
                ),
                onSave = {},
                onCancel = {}
            )
        }
    }
}

@Composable
fun PreviewProfileEditSheetUpdateProfileLight() {
    CraysCircleTheme {
        Surface {
            ProfileEditSheet(
                initialProfile = UserProfile(
                    nickname = "Jane Smith",
                    avatarId = 3,
                    gender = Gender.FEMALE,
                    fullName = "Jane Smith",
                    bio = "Mobile developer and tech enthusiast.",
                    interests = listOf("Android", "Kotlin", "Open Source"),
                    website = "www.janesmith.dev",
                    email = "jane@email.com",
                    phone = "+1 987 654 3210",
                    location = "Seattle, WA",
                    hasCompletedSetup = true
                ),
                onSave = {},
                onCancel = {}
            )
        }
    }
} 