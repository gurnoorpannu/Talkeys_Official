package com.example.talkeys_new.utils.cache

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Utility class for cache invalidation strategies
 * Provides methods to invalidate related caches when data changes
 */
object CacheInvalidator {
    private val TAG = "CacheInvalidator"
    private val invalidationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    /**
     * Invalidates all event-related caches
     * Call this when events are created, updated, or deleted
     */
    fun invalidateEventCaches() {
        invalidationScope.launch {
            try {
                // Clear all events cache
                CacheManager.eventsCache.clear()
                
                // Clear event details cache
                CacheManager.eventDetailsCache.clear()
                
                Log.d(TAG, "Event caches invalidated")
            } catch (e: Exception) {
                Log.e(TAG, "Error invalidating event caches", e)
            }
        }
    }
    
    /**
     * Invalidates cache for a specific event
     * Call this when a specific event is updated
     */
    fun invalidateEventCache(eventId: String) {
        invalidationScope.launch {
            try {
                // Remove specific event from details cache
                val cacheKey = CacheManager.Keys.eventDetail(eventId)
                CacheManager.eventDetailsCache.remove(cacheKey)
                
                // Also invalidate all events cache since the event list might have changed
                CacheManager.eventsCache.clear()
                
                Log.d(TAG, "Event cache invalidated for ID: $eventId")
            } catch (e: Exception) {
                Log.e(TAG, "Error invalidating event cache for ID: $eventId", e)
            }
        }
    }
    
    /**
     * Invalidates user profile cache
     * Call this when user profile is updated
     */
    fun invalidateUserProfileCache() {
        invalidationScope.launch {
            try {
                CacheManager.userProfileCache.clear()
                Log.d(TAG, "User profile cache invalidated")
            } catch (e: Exception) {
                Log.e(TAG, "Error invalidating user profile cache", e)
            }
        }
    }
    
    /**
     * Invalidates user events cache
     * Call this when user registers/unregisters for events
     */
    fun invalidateUserEventsCache() {
        invalidationScope.launch {
            try {
                CacheManager.userEventsCache.clear()
                Log.d(TAG, "User events cache invalidated")
            } catch (e: Exception) {
                Log.e(TAG, "Error invalidating user events cache", e)
            }
        }
    }
    
    /**
     * Invalidates user events cache for specific type and filters
     */
    fun invalidateUserEventsCache(type: String, status: String? = null, period: String? = null) {
        invalidationScope.launch {
            try {
                val cacheKey = CacheManager.Keys.userEvents(type, status, period)
                CacheManager.userEventsCache.remove(cacheKey)
                Log.d(TAG, "User events cache invalidated for type: $type, status: $status, period: $period")
            } catch (e: Exception) {
                Log.e(TAG, "Error invalidating user events cache", e)
            }
        }
    }
    
    /**
     * Invalidates recent activity cache
     * Call this when user performs new activities
     */
    fun invalidateRecentActivityCache() {
        invalidationScope.launch {
            try {
                CacheManager.recentActivityCache.clear()
                Log.d(TAG, "Recent activity cache invalidated")
            } catch (e: Exception) {
                Log.e(TAG, "Error invalidating recent activity cache", e)
            }
        }
    }
    
    /**
     * Invalidates all user-specific caches
     * Call this on logout or user switch
     */
    fun invalidateAllUserCaches() {
        invalidationScope.launch {
            try {
                CacheManager.clearUserCaches()
                Log.d(TAG, "All user caches invalidated")
            } catch (e: Exception) {
                Log.e(TAG, "Error invalidating all user caches", e)
            }
        }
    }
    
    /**
     * Invalidates all caches
     * Call this for complete cache reset
     */
    fun invalidateAllCaches() {
        invalidationScope.launch {
            try {
                CacheManager.clearAllCaches()
                Log.d(TAG, "All caches invalidated")
            } catch (e: Exception) {
                Log.e(TAG, "Error invalidating all caches", e)
            }
        }
    }
    
    /**
     * Invalidates caches based on data change type
     */
    fun invalidateByDataType(dataType: DataChangeType, identifier: String? = null) {
        when (dataType) {
            DataChangeType.EVENT_CREATED,
            DataChangeType.EVENT_UPDATED -> {
                if (identifier != null) {
                    invalidateEventCache(identifier)
                } else {
                    invalidateEventCaches()
                }
            }
            DataChangeType.EVENT_DELETED -> invalidateEventCaches()
            DataChangeType.USER_PROFILE_UPDATED -> invalidateUserProfileCache()
            DataChangeType.USER_EVENT_REGISTERED,
            DataChangeType.USER_EVENT_UNREGISTERED -> invalidateUserEventsCache()
            DataChangeType.USER_ACTIVITY_CHANGED -> invalidateRecentActivityCache()
            DataChangeType.USER_LOGOUT -> invalidateAllUserCaches()
        }
    }
    
    /**
     * Enum representing different types of data changes that require cache invalidation
     */
    enum class DataChangeType {
        EVENT_CREATED,
        EVENT_UPDATED,
        EVENT_DELETED,
        USER_PROFILE_UPDATED,
        USER_EVENT_REGISTERED,
        USER_EVENT_UNREGISTERED,
        USER_ACTIVITY_CHANGED,
        USER_LOGOUT
    }
}
