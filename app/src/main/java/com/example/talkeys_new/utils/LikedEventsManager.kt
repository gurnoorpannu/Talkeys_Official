package com.example.talkeys_new.utils

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manager class for handling liked events using local storage (SharedPreferences)
 * Provides a single source of truth for liked event state across the app
 */
class LikedEventsManager private constructor(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )
    
    private val _likedEventIds = MutableStateFlow<Set<String>>(loadLikedEvents())
    val likedEventIds: StateFlow<Set<String>> = _likedEventIds.asStateFlow()
    
    companion object {
        private const val PREFS_NAME = "liked_events_prefs"
        private const val KEY_LIKED_EVENTS = "liked_event_ids"
        
        @Volatile
        private var instance: LikedEventsManager? = null
        
        fun getInstance(context: Context): LikedEventsManager {
            return instance ?: synchronized(this) {
                instance ?: LikedEventsManager(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }
    
    /**
     * Load liked event IDs from SharedPreferences
     */
    private fun loadLikedEvents(): Set<String> {
        return prefs.getStringSet(KEY_LIKED_EVENTS, emptySet()) ?: emptySet()
    }
    
    /**
     * Save liked event IDs to SharedPreferences
     */
    private fun saveLikedEvents(eventIds: Set<String>) {
        prefs.edit().putStringSet(KEY_LIKED_EVENTS, eventIds).apply()
        _likedEventIds.value = eventIds
    }
    
    /**
     * Like an event by adding its ID to the liked set
     * @param eventId The ID of the event to like
     */
    fun likeEvent(eventId: String) {
        val currentLiked = _likedEventIds.value.toMutableSet()
        if (currentLiked.add(eventId)) {
            saveLikedEvents(currentLiked)
        }
    }
    
    /**
     * Unlike an event by removing its ID from the liked set
     * @param eventId The ID of the event to unlike
     */
    fun unlikeEvent(eventId: String) {
        val currentLiked = _likedEventIds.value.toMutableSet()
        if (currentLiked.remove(eventId)) {
            saveLikedEvents(currentLiked)
        }
    }
    
    /**
     * Check if an event is liked
     * @param eventId The ID of the event to check
     * @return true if the event is liked, false otherwise
     */
    fun isEventLiked(eventId: String): Boolean {
        return _likedEventIds.value.contains(eventId)
    }
    
    /**
     * Get all liked event IDs
     * @return Set of liked event IDs
     */
    fun getLikedEventIds(): Set<String> {
        return _likedEventIds.value
    }
    
    /**
     * Toggle like state for an event
     * @param eventId The ID of the event to toggle
     * @return true if event is now liked, false if unliked
     */
    fun toggleLike(eventId: String): Boolean {
        return if (isEventLiked(eventId)) {
            unlikeEvent(eventId)
            false
        } else {
            likeEvent(eventId)
            true
        }
    }
    
    /**
     * Clear all liked events
     */
    fun clearAllLikedEvents() {
        saveLikedEvents(emptySet())
    }
}
