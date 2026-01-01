package com.example.talkeys_new.utils

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.talkeys_new.api.DashboardApiService
import com.example.talkeys_new.api.DashboardRepository
import com.example.talkeys_new.screens.events.EventApiService
import com.example.talkeys_new.screens.events.provideEventApiService
import com.example.talkeys_new.screens.authentication.TokenManager
import com.example.talkeys_new.screens.dashboard.DashboardViewModel
import com.example.talkeys_new.screens.events.EventViewModel
import com.example.talkeys_new.screens.events.EventsRepository

/**
 * Factory class for creating ViewModels with proper dependencies and error handling
 */
class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    
    companion object {
        private const val TAG = "ViewModelFactory"
    }
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return try {
            Log.d(TAG, "Creating ViewModel of type: ${modelClass.simpleName}")
            
            when {
                modelClass.isAssignableFrom(EventViewModel::class.java) -> {
                    val eventApiService = provideEventApiService(context)
                    val eventsRepository = EventsRepository(eventApiService)
                    EventViewModel(eventsRepository, context) as T
                }
                
                modelClass.isAssignableFrom(DashboardViewModel::class.java) -> {
                    val dashboardApiService = com.example.talkeys_new.api.RetrofitClient.provideDashboardApiService()
                    val tokenManager = TokenManager(context)
                    val dashboardRepository = DashboardRepository(dashboardApiService, tokenManager)
                    DashboardViewModel(dashboardRepository) as T
                }
                
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creating ViewModel: ${e.message}", e)
            throw e
        }
    }
}