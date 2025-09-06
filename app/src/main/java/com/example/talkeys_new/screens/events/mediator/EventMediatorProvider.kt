package com.example.talkeys_new.screens.events.mediator

import android.content.Context
import androidx.navigation.NavController
import com.example.talkeys_new.api.RetrofitClient
import com.example.talkeys_new.screens.events.EventApiService
import com.example.talkeys_new.screens.events.EventsRepository
import com.example.talkeys_new.screens.events.provideEventApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Singleton provider for EventMediator instances.
 * This ensures we have a single mediator instance across the application
 * and provides proper lifecycle management.
 */
object EventMediatorProvider {
    
    @Volatile
    private var INSTANCE: EventMediatorImpl? = null
    
    /**
     * Get the singleton EventMediator instance
     * @param context Application context
     * @return EventMediator instance
     */
    fun getMediator(context: Context): EventMediator {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: createMediator(context).also { INSTANCE = it }
        }
    }
    
    /**
     * Set the navigation controller for the mediator
     * @param navController Navigation controller instance
     */
    fun setNavController(navController: NavController) {
        INSTANCE?.setNavController(navController)
    }
    
    /**
     * Add a listener to the mediator
     * @param key Unique key for the listener
     * @param listener EventListener instance
     */
    fun addListener(key: String, listener: EventListener) {
        INSTANCE?.addListener(key, listener)
    }
    
    /**
     * Remove a listener from the mediator
     * @param key Unique key for the listener
     */
    fun removeListener(key: String) {
        INSTANCE?.removeListener(key)
    }
    
    /**
     * Clear the singleton instance (useful for testing or app restart)
     */
    fun clearInstance() {
        INSTANCE?.cleanup()
        INSTANCE = null
    }
    
    /**
     * Create a new EventMediator instance
     */
    private fun createMediator(context: Context): EventMediatorImpl {
        // Create API service using the existing provider function
        val apiService = provideEventApiService(context)
        
        // Create repository
        val repository = EventsRepository(apiService)
        
        // Create coroutine scope for mediator
        val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
        
        // Create and return mediator
        return EventMediatorImpl(repository, context.applicationContext, scope)
    }
}

/**
 * Extension function to easily get the mediator from any context
 */
fun Context.getEventMediator(): EventMediator = EventMediatorProvider.getMediator(this)

/**
 * Abstract base class for components that use the EventMediator
 * Provides common functionality for mediator-aware components
 */
abstract class EventMediatorAware(protected val context: Context) {
    
    protected val mediator: EventMediator by lazy { 
        EventMediatorProvider.getMediator(context) 
    }
    
    /**
     * Register this component as a listener with a unique key
     */
    protected fun registerAsListener(key: String, listener: EventListener) {
        EventMediatorProvider.addListener(key, listener)
    }
    
    /**
     * Unregister this component as a listener
     */
    protected fun unregisterAsListener(key: String) {
        EventMediatorProvider.removeListener(key)
    }
    
    /**
     * Called when this component is created/initialized
     */
    abstract fun onComponentCreated()
    
    /**
     * Called when this component is destroyed/cleaned up
     */
    abstract fun onComponentDestroyed()
}
