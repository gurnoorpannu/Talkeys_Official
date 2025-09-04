package com.example.talkeys_new.utils.cache

import android.util.Log
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

/**
 * Thread-safe LRU (Least Recently Used) cache implementation
 * Uses a combination of HashMap and doubly-linked list for O(1) operations
 * 
 * @param T The type of values stored in the cache
 * @param maxSize Maximum number of entries the cache can hold
 * @param ttlMillis Time-to-live for cache entries in milliseconds (optional)
 */
class LruCache<T>(
    private val maxSize: Int,
    private val ttlMillis: Long? = null
) {
    private val TAG = "LruCache"
    
    // Thread-safe map for O(1) key lookup
    private val cache = ConcurrentHashMap<String, CacheNode<T>>()
    
    // Mutex for thread-safe operations on the linked list
    private val mutex = Mutex()
    
    // Dummy head and tail nodes for easier list manipulation
    private val head = CacheNode<T>("", null, 0)
    private val tail = CacheNode<T>("", null, 0)
    
    init {
        if (maxSize <= 0) {
            throw IllegalArgumentException("Cache size must be positive")
        }
        head.next = tail
        tail.prev = head
        Log.d(TAG, "LruCache initialized with maxSize: $maxSize, ttl: ${ttlMillis}ms")
    }
    
    /**
     * Retrieves a value from the cache
     * @param key The key to look up
     * @return The cached value or null if not found or expired
     */
    suspend fun get(key: String): T? {
        val node = cache[key] ?: return null
        
        // Check if entry has expired
        if (isExpired(node)) {
            remove(key)
            return null
        }
        
        // Move to front (most recently used)
        mutex.withLock {
            moveToFront(node)
        }
        
        Log.d(TAG, "Cache hit for key: $key")
        return node.value
    }
    
    /**
     * Stores a value in the cache
     * @param key The key to store the value under
     * @param value The value to cache
     */
    suspend fun put(key: String, value: T) {
        val currentTime = System.currentTimeMillis()
        val existingNode = cache[key]
        
        mutex.withLock {
            if (existingNode != null) {
                // Update existing entry
                existingNode.value = value
                existingNode.timestamp = currentTime
                moveToFront(existingNode)
                Log.d(TAG, "Updated existing cache entry for key: $key")
            } else {
                // Create new entry
                val newNode = CacheNode(key, value, currentTime)
                cache[key] = newNode
                addToFront(newNode)
                
                // Remove least recently used entries if over capacity
                while (cache.size > maxSize) {
                    val lru = removeLast()
                    if (lru != null) {
                        cache.remove(lru.key)
                        Log.d(TAG, "Evicted LRU entry: ${lru.key}")
                    }
                }
                
                Log.d(TAG, "Added new cache entry for key: $key, cache size: ${cache.size}")
            }
        }
    }
    
    /**
     * Removes a specific entry from the cache
     * @param key The key to remove
     * @return The removed value or null if not found
     */
    suspend fun remove(key: String): T? {
        val node = cache.remove(key) ?: return null
        
        mutex.withLock {
            removeNode(node)
        }
        
        Log.d(TAG, "Removed cache entry for key: $key")
        return node.value
    }
    
    /**
     * Clears all entries from the cache
     */
    suspend fun clear() {
        mutex.withLock {
            cache.clear()
            head.next = tail
            tail.prev = head
        }
        Log.d(TAG, "Cache cleared")
    }
    
    /**
     * Returns the current size of the cache
     */
    fun size(): Int = cache.size
    
    /**
     * Checks if the cache contains a key
     */
    suspend fun containsKey(key: String): Boolean {
        val node = cache[key] ?: return false
        
        if (isExpired(node)) {
            remove(key)
            return false
        }
        
        return true
    }
    
    /**
     * Gets all keys currently in the cache (non-expired)
     */
    suspend fun keys(): Set<String> {
        // Remove expired entries first
        val expiredKeys = cache.entries
            .filter { isExpired(it.value) }
            .map { it.key }
        
        expiredKeys.forEach { remove(it) }
        
        return cache.keys.toSet()
    }
    
    /**
     * Removes expired entries from the cache
     */
    suspend fun cleanupExpired() {
        val expiredKeys = cache.entries
            .filter { isExpired(it.value) }
            .map { it.key }
        
        expiredKeys.forEach { remove(it) }
        
        if (expiredKeys.isNotEmpty()) {
            Log.d(TAG, "Cleaned up ${expiredKeys.size} expired entries")
        }
    }
    
    // Private helper methods
    
    private fun isExpired(node: CacheNode<T>): Boolean {
        return ttlMillis != null && (System.currentTimeMillis() - node.timestamp) > ttlMillis
    }
    
    private fun addToFront(node: CacheNode<T>) {
        node.prev = head
        node.next = head.next
        head.next?.prev = node
        head.next = node
    }
    
    private fun removeNode(node: CacheNode<T>) {
        node.prev?.next = node.next
        node.next?.prev = node.prev
    }
    
    private fun moveToFront(node: CacheNode<T>) {
        removeNode(node)
        addToFront(node)
    }
    
    private fun removeLast(): CacheNode<T>? {
        val last = tail.prev
        return if (last == head) {
            null
        } else {
            removeNode(last!!)
            last
        }
    }
    
    /**
     * Internal node class for the doubly-linked list
     */
    private data class CacheNode<T>(
        val key: String,
        var value: T?,
        var timestamp: Long,
        var prev: CacheNode<T>? = null,
        var next: CacheNode<T>? = null
    )
}
