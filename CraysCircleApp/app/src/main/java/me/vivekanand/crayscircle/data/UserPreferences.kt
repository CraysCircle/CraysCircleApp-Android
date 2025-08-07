/*
 * CraysCircle - Peer-to-Peer Communication App
 * Developer: Vivekanand Pandey [@https://www.linkedin.com/in/itsvnp/]
 * Copyright © 2025
 */

package me.vivekanand.crayscircle.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {
    
    companion object {
        private val NICKNAME = stringPreferencesKey("nickname")
        private val GENDER = stringPreferencesKey("gender")
        private val AVATAR_ID = intPreferencesKey("avatar_id")
        private val HAS_COMPLETED_SETUP = booleanPreferencesKey("has_completed_setup")
        private val HAS_SEEN_WELCOME = booleanPreferencesKey("has_seen_welcome")
        private val HAS_SEEN_ONBOARDING = booleanPreferencesKey("has_seen_onboarding")
        private val HAS_GRANTED_PERMISSIONS = booleanPreferencesKey("has_granted_permissions")
        private val IS_DEVICE_COMPATIBLE = booleanPreferencesKey("is_device_compatible")
        private val UNIQUE_ID = stringPreferencesKey("unique_id")
        private val EMAIL = stringPreferencesKey("email")
        private val PHONE = stringPreferencesKey("phone")
        private val LOCATION = stringPreferencesKey("location")
    }

    val userProfileFlow: Flow<UserProfile> = context.dataStore.data.map { preferences ->
        UserProfile(
            uniqueId = preferences[UNIQUE_ID] ?: UUID.randomUUID().toString(),
            nickname = preferences[NICKNAME] ?: "",
            gender = Gender.valueOf(preferences[GENDER] ?: Gender.OTHER.name),
            avatarId = preferences[AVATAR_ID] ?: 1,
            hasCompletedSetup = preferences[HAS_COMPLETED_SETUP] ?: false,
            fullName = preferences[stringPreferencesKey("fullName")] ?: "",
            bio = preferences[stringPreferencesKey("bio")] ?: "",
            interests = preferences[stringPreferencesKey("interests")]?.split("||") ?: emptyList(),
            website = preferences[stringPreferencesKey("website")] ?: "",
            email = preferences[EMAIL] ?: "",
            phone = preferences[PHONE] ?: "",
            location = preferences[LOCATION] ?: ""
        )
    }

    val hasSeenWelcomeFlow: Flow<Boolean> = context.dataStore.data.map { it[HAS_SEEN_WELCOME] ?: false }
    val hasSeenOnboardingFlow: Flow<Boolean> = context.dataStore.data.map { it[HAS_SEEN_ONBOARDING] ?: false }
    val hasGrantedPermissionsFlow: Flow<Boolean> = context.dataStore.data.map { it[HAS_GRANTED_PERMISSIONS] ?: false }
    val isDeviceCompatibleFlow: Flow<Boolean> = context.dataStore.data.map { it[IS_DEVICE_COMPATIBLE] ?: false }

    suspend fun saveUserProfile(profile: UserProfile) {
        val ensuredProfile = if (profile.uniqueId.isBlank()) {
            profile.copy(uniqueId = UUID.randomUUID().toString())
        } else profile
        context.dataStore.edit { preferences ->
            preferences[UNIQUE_ID] = ensuredProfile.uniqueId
            preferences[NICKNAME] = ensuredProfile.nickname
            preferences[GENDER] = ensuredProfile.gender.name
            preferences[AVATAR_ID] = ensuredProfile.avatarId
            preferences[HAS_COMPLETED_SETUP] = ensuredProfile.hasCompletedSetup
            preferences[stringPreferencesKey("fullName")] = ensuredProfile.fullName
            preferences[stringPreferencesKey("bio")] = ensuredProfile.bio
            preferences[stringPreferencesKey("interests")] = ensuredProfile.interests.joinToString("||")
            preferences[stringPreferencesKey("website")] = ensuredProfile.website
            preferences[EMAIL] = ensuredProfile.email
            preferences[PHONE] = ensuredProfile.phone
            preferences[LOCATION] = ensuredProfile.location
        }
    }

    suspend fun setHasSeenWelcome(value: Boolean) {
        context.dataStore.edit { it[HAS_SEEN_WELCOME] = value }
    }

    suspend fun setHasSeenOnboarding(value: Boolean) {
        context.dataStore.edit { it[HAS_SEEN_ONBOARDING] = value }
    }

    suspend fun setHasGrantedPermissions(value: Boolean) {
        context.dataStore.edit { it[HAS_GRANTED_PERMISSIONS] = value }
    }

    suspend fun setIsDeviceCompatible(value: Boolean) {
        context.dataStore.edit { it[IS_DEVICE_COMPATIBLE] = value }
    }

    suspend fun logout() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
} 