package com.example.talkeys_new.utils.cache

import android.content.Context
import android.util.Log
import com.example.talkeys_new.api.DashboardRepository
import com.example.talkeys_new.screens.events.EventsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Example class demonstrating how to use the LRU caching system
 * This shows best practices for cache usage across the application
 */
class CacheUsageExample(
    private val eventsRepository: EventsRepository,
    private val dashboardRepository: DashboardRepository,
    private val context: Context
) {
    private val TAG = "CacheUsageExample"
    private val scope = CoroutineScope(Dispatchers.Main)
    
    /**
     * Example 1: Basic data caching with events
     */
    fun demonstrateEventCaching() {
        scope.launch {
            Log.d(TAG, "=== Event Caching Demo ===")
            
            // First call - will fetch from API and cache
            Log.d(TAG, "First call (from API):")
            val result1 = eventsRepository.getAllEvents()
            
            // Second call - will return from cache
            Log.d(TAG, "Second call (from cache):")
            val result2 = eventsRepository.getAllEvents()
            
            // Force refresh - will bypass cache
            Log.d(TAG, "Third call (force refresh):")
            val result3 = eventsRepository.getAllEvents(forceRefresh = true)
            
            // Get cache statistics
            val stats = CacheManager.getCacheStats()
            Log.d(TAG, "Cache stats: $stats")
        }
    }
    
    /**
     * Example 2: User profile caching
     */
    fun demonstrateUserProfileCaching() {
        scope.launch {
            Log.d(TAG, "=== User Profile Caching Demo ===")
            
            // Fetch user profile (will cache automatically)
            val profile = dashboardRepository.getUserProfile()
            
            // Update profile (will update cache)
            val updateData = mapOf("name" to "Updated Name")
            dashboardRepository.updateUserProfile(updateData)
            
            // Fetch again (will return updated cached data)
            val updatedProfile = dashboardRepository.getUserProfile()
        }
    }
    
    /**
     * Example 3: Image caching usage
     */
    fun demonstrateImageCaching() {
        scope.launch {
            Log.d(TAG, "=== Image Caching Demo ===")
            
            val imageCache = ImageCache.getInstance(context)
            val imageUrl = "https://example.com/event-image.jpg"
            
            // Load image (will cache automatically)
            val bitmap1 = imageCache.getImage(imageUrl)
            
            // Load same image again (will return from cache)
            val bitmap2 = imageCache.getImage(imageUrl)
            
            // Load with size constraints
            val resizedBitmap = imageCache.getImage(imageUrl, maxWidth = 300, maxHeight = 200)
            
            // Preload images for better UX
            val imageUrls = listOf(
                "https://example.com/image1.jpg",
                "https://example.com/image2.jpg",
                "https://example.com/image3.jpg"
            )
            
            imageUrls.forEach { url ->
                imageCache.preloadImage(url)
            }
            
            // Get image cache statistics
            val imageStats = imageCache.getCacheStats()
            Log.d(TAG, "Image cache stats: $imageStats")
        }
    }
    
    /**
     * Example 4: Cache invalidation scenarios
     */
    fun demonstrateCacheInvalidation() {
        scope.launch {
            Log.d(TAG, "=== Cache Invalidation Demo ===")
            
            // Scenario 1: User creates a new event
            // This should invalidate all event caches
            CacheInvalidator.invalidateByDataType(
                CacheInvalidator.DataChangeType.EVENT_CREATED
            )
            
            // Scenario 2: User updates a specific event
            val eventId = "event123"
            CacheInvalidator.invalidateByDataType(
                CacheInvalidator.DataChangeType.EVENT_UPDATED,
                eventId
            )
            
            // Scenario 3: User updates their profile
            CacheInvalidator.invalidateByDataType(
                CacheInvalidator.DataChangeType.USER_PROFILE_UPDATED
            )
            
            // Scenario 4: User logs out
            CacheInvalidator.invalidateByDataType(
                CacheInvalidator.DataChangeType.USER_LOGOUT
            )
        }
    }
    
    /**
     * Example 5: Manual cache management
     */
    fun demonstrateManualCacheManagement() {
        scope.launch {
            Log.d(TAG, "=== Manual Cache Management Demo ===")
            
            // Check if specific data is cached
            val hasEvents = CacheManager.eventsCache.containsKey(CacheManager.Keys.ALL_EVENTS)
            Log.d(TAG, "Events cached: $hasEvents")
            
            // Get all cache keys
            val eventKeys = CacheManager.eventsCache.keys()
            Log.d(TAG, "Cached event keys: $eventKeys")
            
            // Clear specific cache
            CacheManager.eventsCache.clear()
            
            // Clear all user-specific caches (useful on logout)
            CacheManager.clearUserCaches()
            
            // Get comprehensive cache statistics
            val allStats = CacheManager.getCacheStats()
            Log.d(TAG, "All cache stats: $allStats")
        }
    }
    
    /**
     * Example 6: Best practices for ViewModels
     */
    fun demonstrateViewModelBestPractices() {
        Log.d(TAG, "=== ViewModel Best Practices ===")
        
        // In your ViewModel:
        // 1. Use caching by default for better UX
        // fetchAllEvents() // Uses cache
        
        // 2. Provide refresh functionality that bypasses cache
        // refreshEvents() // Forces fresh data
        
        // 3. Handle cache invalidation on data changes
        // After creating/updating an event:
        // CacheInvalidator.invalidateEventCaches()
        
        // 4. Use appropriate cache TTL based on data freshness requirements
        // Events: 30 minutes (relatively static)
        // User profile: 15 minutes (changes occasionally)
        // Recent activity: 5 minutes (changes frequently)
        
        Log.d(TAG, "See comments in code for ViewModel best practices")
    }
    
    /**
     * Example 7: Error handling with caching
     */
    fun demonstrateErrorHandling() {
        scope.launch {
            Log.d(TAG, "=== Error Handling with Caching ===")
            
            try {
                // Try to get data (will use cache if available)
                val events = eventsRepository.getAllEvents()
                
                // If network fails but cache is available, user still sees data
                // If both fail, show appropriate error message
                
            } catch (e: Exception) {
                Log.e(TAG, "Error occurred, but cache might still have data", e)
                
                // You can still try to get cached data even if API fails
                val cachedEvents = CacheManager.eventsCache.get(CacheManager.Keys.ALL_EVENTS)
                if (cachedEvents != null) {
                    Log.d(TAG, "Using cached data despite API error")
                    // Use cached data
                } else {
                    Log.d(TAG, "No cached data available, showing error")
                    // Show error to user
                }
            }
        }
    }
}
