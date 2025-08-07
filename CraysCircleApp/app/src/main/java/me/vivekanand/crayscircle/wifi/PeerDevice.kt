/*
 * CraysCircle - Peer-to-Peer Communication App
 * Developer: Vivekanand Pandey [@https://www.linkedin.com/in/itsvnp/]
 * Copyright © 2025
 */

package me.vivekanand.crayscircle.wifi

import me.vivekanand.crayscircle.data.Gender
import me.vivekanand.crayscircle.data.UserProfile
import org.json.JSONObject
import java.util.concurrent.atomic.AtomicInteger

data class PeerDevice(
    val uniqueId: String,
    val nickname: String,
    val avatarId: Int,
    val gender: Gender,
    val handle: android.net.wifi.aware.PeerHandle?,
    var distance: Float? = null,
    var status: String = "Discovered"
) {
    companion object {
        internal val idCounter = AtomicInteger(0)
        
        fun fromJson(json: String, handle: android.net.wifi.aware.PeerHandle?): PeerDevice? {
            return try {
                val obj = JSONObject(json)
                PeerDevice(
                    uniqueId = obj.optString("uniqueId", ""),
                    nickname = obj.optString("nickname", "Peer"),
                    avatarId = obj.optInt("avatarId", 1),
                    gender = Gender.valueOf(obj.optString("gender", Gender.OTHER.name)),
                    handle = handle
                )
            } catch (e: Exception) {
                null
            }
        }
        
        fun toJson(profile: UserProfile): String {
            return JSONObject().apply {
                put("uniqueId", profile.uniqueId)
                put("nickname", profile.nickname)
                put("avatarId", profile.avatarId)
                put("gender", profile.gender.name)
            }.toString()
        }
    }

    val id: Int = idCounter.incrementAndGet()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PeerDevice) return false
        return uniqueId.isNotEmpty() && uniqueId == other.uniqueId
    }

    override fun hashCode(): Int = uniqueId.hashCode()
} 