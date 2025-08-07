/*
 * CraysCircle - Peer-to-Peer Communication App
 * Developer: Vivekanand Pandey [@https://www.linkedin.com/in/itsvnp/]
 * Copyright © 2025
 */

package me.vivekanand.crayscircle.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.*

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val peerId: String,
    val content: String,
    val isFromMe: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "SENT"
)

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE peerId = :peerId ORDER BY timestamp ASC")
    fun getMessagesForPeer(peerId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE peerId = :peerId AND status = 'QUEUED' ORDER BY timestamp ASC")
    fun getQueuedMessagesForPeer(peerId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("DELETE FROM messages WHERE peerId = :peerId")
    suspend fun deleteMessagesForPeer(peerId: String)
}

@Database(entities = [MessageEntity::class], version = 1)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var INSTANCE: ChatDatabase? = null

        fun getDatabase(context: Context): ChatDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChatDatabase::class.java,
                    "chat_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
} 