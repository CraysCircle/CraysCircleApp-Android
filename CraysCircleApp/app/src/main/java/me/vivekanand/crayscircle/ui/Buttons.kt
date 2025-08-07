/*
 * CraysCircle - Peer-to-Peer Communication App
 * Developer: Vivekanand Pandey [@https://www.linkedin.com/in/itsvnp/]
 * Copyright © 2025
 */

package me.vivekanand.crayscircle.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.vivekanand.crayscircle.ui.theme.BrandPrimary
import me.vivekanand.crayscircle.ui.theme.BrandSecondary
import me.vivekanand.crayscircle.ui.theme.BrandTertiary
import me.vivekanand.crayscircle.ui.theme.CraysCircleTheme

@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    useGradient: Boolean = true,
    leadingIcon: (@Composable (() -> Unit))? = null,
    trailingIcon: (@Composable (() -> Unit))? = null,
    contentPadding: PaddingValues = PaddingValues(vertical = 19.dp),
) {
    val shape = MaterialTheme.shapes.extraLarge
    val gradient = Brush.horizontalGradient(
        colors = listOf(BrandPrimary, BrandTertiary, BrandSecondary),
        startX = 0f,
        endX = 1000f,
        tileMode = androidx.compose.ui.graphics.TileMode.Clamp
    )
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        if (leadingIcon != null) {
            leadingIcon()
            Spacer(Modifier.padding(end = 8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = (MaterialTheme.typography.labelLarge.fontSize.value * 1.2).sp
            )
        )
        if (trailingIcon != null) {
            Spacer(Modifier.padding(start = 8.dp))
            trailingIcon()
        }
    }
}

@Composable
fun SecondaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    contentPadding: PaddingValues = PaddingValues(vertical = 19.dp),
) {
    val shape = MaterialTheme.shapes.extraLarge
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = (MaterialTheme.typography.labelLarge.fontSize.value * 1.2).sp
            )
        )
    }
}

@Composable
fun OutlinedAppButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    contentPadding: PaddingValues = PaddingValues(vertical = 19.dp),
    borderColor: Color = BrandPrimary
) {
    val shape = MaterialTheme.shapes.extraLarge
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = borderColor,
                fontSize = (MaterialTheme.typography.labelLarge.fontSize.value * 1.2).sp
            )
        )
    }
}

@Composable
fun IconAppButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable () -> Unit,
    contentDescription: String? = null,
    tint: Color = MaterialTheme.colorScheme.primary
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
    ) {
        icon()
    }
}

private fun Brush.toBrushColor(): Color = Color.Transparent

@Composable
fun PreviewPrimaryButtonLight() {
    CraysCircleTheme {
        PrimaryButton(onClick = {}, text = "Primary Button")
    }
}

@Composable
fun PreviewPrimaryButtonDark() {
    CraysCircleTheme {
        PrimaryButton(onClick = {}, text = "Primary Button")
    }
}

@Composable
fun PreviewSecondaryButtonLight() {
    CraysCircleTheme {
        SecondaryButton(onClick = {}, text = "Secondary Button")
    }
}

@Composable
fun PreviewOutlinedAppButtonLight() {
    CraysCircleTheme {
        OutlinedAppButton(onClick = {}, text = "Outlined Button")
    }
} 