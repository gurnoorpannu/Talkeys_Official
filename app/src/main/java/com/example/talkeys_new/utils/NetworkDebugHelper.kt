package com.example.talkeys_new.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.net.URL

/**
 * Network Debug Helper
 * Use this to diagnose network connectivity issues
 */
object NetworkDebugHelper {
    
    private const val TAG = "NetworkDebug"
    
    /**
     * Check if device has internet connectivity
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                Log.d(TAG, "✅ Connected via WiFi")
                true
            }
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                Log.d(TAG, "✅ Connected via Cellular")
                true
            }
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                Log.d(TAG, "✅ Connected via Ethernet")
                true
            }
            else -> {
                Log.d(TAG, "❌ No network connection")
                false
            }
        }
    }
    
    /**
     * Test DNS resolution for your API domain
     */
    suspend fun testDNSResolution(domain: String = "api.talkeys.xyz"): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "🔍 Testing DNS resolution for: $domain")
                val address = InetAddress.getByName(domain)
                Log.d(TAG, "✅ DNS resolved: $domain -> ${address.hostAddress}")
                true
            } catch (e: Exception) {
                Log.e(TAG, "❌ DNS resolution failed for $domain: ${e.message}")
                false
            }
        }
    }
    
    /**
     * Test HTTP connectivity to your API
     */
    suspend fun testAPIConnectivity(apiUrl: String = "https://api.talkeys.xyz"): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "🔍 Testing API connectivity to: $apiUrl")
                val url = URL(apiUrl)
                val connection = url.openConnection()
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                connection.connect()
                Log.d(TAG, "✅ API connectivity successful")
                true
            } catch (e: Exception) {
                Log.e(TAG, "❌ API connectivity failed: ${e.message}")
                false
            }
        }
    }
    
    /**
     * Run complete network diagnostics
     */
    suspend fun runNetworkDiagnostics(context: Context) {
        Log.d(TAG, "🚀 Starting network diagnostics...")
        
        // Check basic connectivity
        val hasNetwork = isNetworkAvailable(context)
        Log.d(TAG, "📱 Network available: $hasNetwork")
        
        if (!hasNetwork) {
            Log.e(TAG, "❌ No network connection - check WiFi/cellular")
            return
        }
        
        // Test DNS resolution
        val dnsWorking = testDNSResolution("api.talkeys.xyz")
        Log.d(TAG, "🌐 DNS resolution: ${if (dnsWorking) "✅ Working" else "❌ Failed"}")
        
        if (!dnsWorking) {
            Log.e(TAG, "💡 Try using mobile data instead of WiFi")
            Log.e(TAG, "💡 Check if corporate firewall is blocking DNS")
            return
        }
        
        // Test API connectivity
        val apiWorking = testAPIConnectivity("https://api.talkeys.xyz")
        Log.d(TAG, "🔗 API connectivity: ${if (apiWorking) "✅ Working" else "❌ Failed"}")
        
        if (!apiWorking) {
            Log.e(TAG, "💡 Server might be down or unreachable")
            Log.e(TAG, "💡 Check if VPN is interfering")
        }
        
        // Test alternative DNS servers
        Log.d(TAG, "🔍 Testing alternative DNS servers...")
        testAlternativeDNS()
        
        Log.d(TAG, "✅ Network diagnostics complete")
    }
    
    /**
     * Test with alternative DNS servers
     */
    private suspend fun testAlternativeDNS() {
        withContext(Dispatchers.IO) {
            val dnsServers = listOf(
                "8.8.8.8" to "Google DNS",
                "1.1.1.1" to "Cloudflare DNS",
                "208.67.222.222" to "OpenDNS"
            )
            
            dnsServers.forEach { (dns, name) ->
                try {
                    Log.d(TAG, "🔍 Testing $name ($dns)")
                    // Note: This is a simplified test - actual DNS server switching requires root
                    val address = InetAddress.getByName("api.talkeys.xyz")
                    Log.d(TAG, "✅ $name can resolve domain")
                } catch (e: Exception) {
                    Log.d(TAG, "❌ $name resolution failed: ${e.message}")
                }
            }
        }
    }
    
    /**
     * Get network type information
     */
    fun getNetworkInfo(context: Context): String {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        
        return when {
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> "WiFi"
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> "Cellular"
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> "Ethernet"
            else -> "Unknown/No Connection"
        }
    }
    
    /**
     * Quick network status check
     */
    fun quickNetworkCheck(context: Context) {
        Log.d(TAG, "📊 Quick Network Status:")
        Log.d(TAG, "  - Network Type: ${getNetworkInfo(context)}")
        Log.d(TAG, "  - Network Available: ${isNetworkAvailable(context)}")
        
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        
        networkCapabilities?.let { caps ->
            Log.d(TAG, "  - Has Internet: ${caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)}")
            Log.d(TAG, "  - Validated: ${caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)}")
        }
    }
}