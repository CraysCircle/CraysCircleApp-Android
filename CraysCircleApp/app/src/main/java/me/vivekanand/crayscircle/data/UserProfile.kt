/*
 * CraysCircle - Peer-to-Peer Communication App
 * Developer: Vivekanand Pandey [@https://www.linkedin.com/in/itsvnp/]
 * Copyright © 2025
 */

package me.vivekanand.crayscircle.data

import java.util.UUID

data class UserProfile(
    val uniqueId: String = UUID.randomUUID().toString(),
    val nickname: String = "",
    val fullName: String = "",
    val avatarId: Int = 1,
    val gender: Gender = Gender.PREFER_NOT_TO_SAY,
    val bio: String = "",
    val interests: List<String> = emptyList(),
    val website: String = "",
    val email: String = "",
    val phone: String = "",
    val location: String = "",
    val hasCompletedSetup: Boolean = false
)

enum class Gender {
    MALE, FEMALE, NON_BINARY, TRANSGENDER, OTHER, PREFER_NOT_TO_SAY
}

enum class SocialPlatform {
    INSTAGRAM, TWITTER, LINKEDIN, GITHUB, FACEBOOK
} 