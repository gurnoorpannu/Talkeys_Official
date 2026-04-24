# Talkeys - Event Management Android App

<div align="center">
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android">
  <img src="https://img.shields.io/badge/Kotlin-0095D5?&style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin">
  <img src="https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose">
  <img src="https://img.shields.io/badge/Material%20Design%203-757575?style=for-the-badge&logo=material-design&logoColor=white" alt="Material Design 3">
</div>

##  Overview

Talkeys is a comprehensive event management Android application built with modern Android development practices. The app allows users to discover, create, manage, and participate in events with integrated payment processing and social features.

##  Key Features

###  **Core Functionality**
- **Event Discovery**: Browse and search events with advanced filtering
- **Event Creation**: Multi-step event creation wizard with validation
- **User Authentication**: Google Sign-In integration with secure token management
- **Payment Integration**: PhonePe payment gateway for event registrations
- **Profile Management**: Customizable user profiles with avatar system
- **Event Management**: Track hosted, registered, and liked events

###  **Advanced Features**
- **Event Mediator Pattern**: Centralized event coordination and state management
- **LRU Caching System**: Comprehensive caching for optimal performance
- **Offline Support**: Cached data availability when offline
- **Real-time Validation**: Form validation with user-friendly error handling
- **Image Optimization**: Dual-layer image caching (memory + disk)
- **Smart Navigation**: Type-safe navigation with Jetpack Navigation Compose
- **Advanced Event Filtering**: Real-time search and category-based filtering
- **Event Actions**: Like, share, and register functionality with optimistic updates
- **Multi-step Event Creation**: Coordinated wizard flow with draft saving

###  **UI/UX Features**
- **Material Design 3**: Modern, accessible design system
- **Dark/Light Theme**: Automatic theme switching
- **Responsive Layout**: Optimized for different screen sizes
- **Custom Avatar System**: Personalized user avatars
- **Smooth Animations**: Polished user interactions
##  Architecture

### **Enhanced Clean Architecture with Mediator Pattern**
```
┌─────────────────────────────────────────────────────────────────┐
│                        UI Layer                                 │
│        (Screens, Compose UI, Event Listeners)                   │
├─────────────────────────────────────────────────────────────────┤
│                   Presentation Layer                            │
│          (EventMediatedViewModel, StateFlow)                    │
├─────────────────────────────────────────────────────────────────┤
│                    Mediator Layer                               │
│    (EventMediator, Coordination, State Management)              │
├─────────────────────────────────────────────────────────────────┤
│                    Domain Layer                                 │
│             (Use Cases, Repositories)                           │
├─────────────────────────────────────────────────────────────────┤
│                     Data Layer                                  │
│          (API Services, LRU Cache, DataStore)                   │
└─────────────────────────────────────────────────────────────────┘
```

### **Event Mediator Architecture Benefits**
- ** Centralized Coordination**: All event operations flow through the mediator
- ** Reactive State**: StateFlow-based state management with automatic UI updates
- ** Loose Coupling**: Components communicate through the mediator, not directly
- ** Advanced Filtering**: Real-time search and category filtering
- ** Navigation Coordination**: Centralized navigation management
- ** Error Handling**: Unified error processing with context awareness
- ** Event Listeners**: Observer pattern for component communication
```

### **Technology Stack**
- **Language**: Kotlin 2.0.21
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM + Repository Pattern
- **Dependency Injection**: Manual DI with Factory Pattern
- **Networking**: Retrofit + OkHttp
- **Image Loading**: Coil
- **Local Storage**: DataStore Preferences + Encrypted SharedPreferences
- **Navigation**: Navigation Compose
- **Authentication**: Google Sign-In
- **Payments**: PhonePe Intent SDK
- **Security**: Android Keystore, AES-256-GCM Encryption

##  Security Features

### **Production-Grade Security Implementation**

#### **Encrypted Data Storage**
- **AES-256-GCM Encryption**: All sensitive data encrypted at rest using Android Keystore
- **EncryptedSharedPreferences**: Both keys and values encrypted
- **Secure Master Key**: Generated and managed by Android Keystore system
- **Zero Plaintext Storage**: No sensitive data stored in plaintext

```kotlin
// SecureStorage wrapper for encrypted data
class SecureStorage(context: Context) {
    fun saveString(key: String, value: String): Result<Unit>
    fun getString(key: String, defaultValue: String? = null): Result<String?>
    fun remove(key: String): Result<Unit>
    fun clear(): Result<Unit>
}
```

#### **Secure Token Management**
- **Encrypted Token Storage**: Authentication tokens encrypted using SecureStorage
- **Token Expiry Validation**: 24-hour validity period with automatic expiration
- **No Token Logging**: Token values never logged to prevent exposure
- **Secure Flow API**: Reactive token access with Flow-based updates

```kotlin
// TokenManager with encrypted storage
class TokenManager(context: Context) {
    suspend fun saveToken(token: String): Result<Unit>
    suspend fun getToken(): Result<String?>
    suspend fun isTokenValid(): Boolean
    suspend fun clearToken(): Result<Unit>
}
```

#### **Network Security**
- **Certificate Pinning**: SSL certificate pinning for API endpoints
- **Network Timeouts**: 30-second timeouts (connect/read/write)
- **Debug-Only Logging**: HTTP logging only in DEBUG builds
- **Centralized Configuration**: Reusable OkHttpClient factory

```kotlin
// NetworkConfig for secure network communication
object NetworkConfig {
    fun createOkHttpClient(enableCertificatePinning: Boolean = false): OkHttpClient
    fun createOkHttpClientWithInterceptors(
        interceptors: List<Interceptor>,
        enableCertificatePinning: Boolean = false
    ): OkHttpClient
}
```

#### **Security Best Practices**
- ✅ **No Hardcoded Secrets**: All sensitive data encrypted or in environment variables
- ✅ **Secure Communication**: HTTPS with optional certificate pinning
- ✅ **Token Expiration**: Automatic token expiry and validation
- ✅ **Error Handling**: No sensitive data in error messages or logs
- ✅ **Android Keystore**: Hardware-backed encryption when available

> [!IMPORTANT]
> **Certificate Pinning**: Update placeholder pins in `NetworkConfig.kt` before production deployment. Extract your certificate pins using:
> ```bash
> openssl s_client -connect api.talkeys.xyz:443 | openssl x509 -pubkey -noout | \
> openssl pkey -pubin -outform der | openssl dgst -sha256 -binary | openssl enc -base64
> ```

## Quick Start

### **Prerequisites**
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 11 or higher
- Android SDK API 24+ (Android 7.0)
- Git

### **Installation**

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/Talkeys_Official.git
   cd Talkeys_Official
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory

3. **Sync Project**
   ```bash
   ./gradlew clean build
   ```

4. **Configure API Base URL**
   ```kotlin
   // In utils/Constants.kt
   object API {
       const val BASE_URL = "https://your-api-domain.com/"
   }
   ```

5. **Run the Application**
   - Connect an Android device or start an emulator
   - Click the "Run" button or press `Ctrl+R`

## 📁 Project Structure

```
app/src/main/java/com/example/talkeys_new/
├── api/                          # API services and data models
│   ├── DashboardApiService.kt
│   ├── DashboardRepository.kt
│   └── RetrofitClient.kt
├── security/                     #  Security module
│   └── SecureStorage.kt         # Encrypted storage wrapper
├── network/                      #  Network configuration
│   └── NetworkConfig.kt         # Certificate pinning, timeouts
├── avatar/                       # Avatar customization system
│   ├── AvatarCustomizerScreen.kt
│   └── ProfileAvatarIntegration.kt
├── dataModels/                   # Data classes and models
│   └── DataClasses.kt
├── navigation/                   # Navigation configuration
│   └── AppNavigation.kt
├── screens/                      # UI screens
│   ├── authentication/          # Login/Signup screens
│   │   ├── TokenManager.kt      #  Secure token management
│   │   └── GoogleSignInManager.kt
│   ├── events/                  # Event-related screens
│   │   ├── mediator/            #  Event Mediator Pattern
│   │   │   ├── EventMediator.kt
│   │   │   ├── EventMediatorImpl.kt
│   │   │   └── EventMediatorProvider.kt
│   │   ├── EventMediatedViewModel.kt
│   │   ├── EventsRepository.kt
│   │   └── EventApiService.kt
│   ├── home/                    # Home screen
│   ├── profile/                 # Profile screens
│   └── common/                  # Shared UI components
├── utils/                       # Utility classes
│   ├── cache/                   # LRU caching system
│   ├── Constants.kt
│   ├── NetworkUtils.kt
│   ├── Result.kt
│   └── ViewModelFactory.kt
└── MainActivity.kt              # Main activity
```

##  Feature Implementation Guide

### **Authentication Flow**
```kotlin
// Google Sign-In integration
class AuthenticationManager {
    suspend fun signInWithGoogle(): Result<UserResponse>
    suspend fun verifyToken(token: String): Result<VerifyResponse>
    suspend fun logout()
}
```

### **Event Management**
```kotlin
// Event operations with Mediator Pattern
class EventsRepository {
    suspend fun getAllEvents(forceRefresh: Boolean = false): Result<List<EventResponse>>
    suspend fun getEventById(eventId: String, forceRefresh: Boolean = false): Result<EventResponse>
}

// Enhanced Event Management with Mediator Pattern
class EventMediatedViewModel(context: Context) : ViewModel(), EventListener {
    private val mediator = EventMediatorProvider.getMediator(context)
    
    fun fetchAllEvents() = viewModelScope.launch { mediator.fetchAllEvents() }
    fun likeEvent(eventId: String) = viewModelScope.launch { mediator.likeEvent(eventId) }
    fun navigateToEventDetail(eventId: String) = mediator.navigateToEventDetail(eventId)
}
```

### **Payment Integration**
```kotlin
// PhonePe payment flow
class PaymentManager {
    fun initiatePayment(eventId: String, amount: Double): PaymentResult
    fun handlePaymentCallback(result: PaymentResult)
}
```

### **Event Mediator Pattern**
```kotlin
// Centralized event coordination and state management
interface EventMediator {
    suspend fun fetchAllEvents(forceRefresh: Boolean = false)
    suspend fun likeEvent(eventId: String): Boolean
    fun navigateToEventDetail(eventId: String)
    fun startEventCreation()
    val eventList: StateFlow<List<EventResponse>>
    val isLoading: StateFlow<Boolean>
}

// Usage in Composables
@Composable
fun ExploreEventsScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: EventMediatedViewModel = viewModel { EventMediatedViewModel(context) }
    
    LaunchedEffect(navController) {
        EventMediatorProvider.setNavController(navController)
    }
    
    val events by viewModel.filteredEvents.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    // UI with mediator-powered actions
    EventsList(
        events = events,
        onEventClick = { event -> viewModel.navigateToEventDetail(event._id) },
        onLikeEvent = { eventId -> viewModel.likeEvent(eventId) }
    )
}
```

### **Caching System**
```kotlin
// LRU cache usage
val cachedData = CacheManager.eventsCache.get(key)
CacheManager.eventsCache.put(key, data)
CacheInvalidator.invalidateByDataType(DataChangeType.EVENT_CREATED)
```

## 🛠️ Development Workflow

### **Code Style**
- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Add KDoc comments for public APIs
- Maximum line length: 120 characters

### **Git Workflow**
```bash
# Create feature branch
git checkout -b feature/new-feature

# Make changes and commit
git add .
git commit -m "feat: add new feature description"

# Push and create PR
git push origin feature/new-feature
```

### **Commit Message Convention**
```
feat: add new feature
fix: resolve bug in payment flow
docs: update README documentation
style: format code according to style guide
refactor: restructure authentication logic
test: add unit tests for event repository
```

### **Testing Strategy**
```kotlin
// Unit tests for repositories
@Test
fun `getAllEvents should return cached data when available`() {
    // Test implementation
}

// UI tests for screens
@Test
fun `login screen should show error for invalid credentials`() {
    // Test implementation
}
```

##  Performance Optimization

### **Caching Strategy**
- **Events**: 30-minute TTL, 100 items max
- **User Profile**: 15-minute TTL, 50 items max
- **Images**: 10MB memory + 50MB disk cache
- **Automatic cleanup**: Every 5 minutes

### **Memory Management**
- Lazy loading for large lists
- Image size optimization
- Proper lifecycle management
- Cache size limits

### **Network Optimization**
- Request deduplication
- Retry mechanisms with exponential backoff
- Connection pooling
- Request/response logging (debug builds only)

##  Configuration

### **Build Variants**
```kotlin
android {
    buildTypes {
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            buildConfigField("String", "API_BASE_URL", "\"https://api-dev.example.com/\"")
        }
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("String", "API_BASE_URL", "\"https://api.example.com/\"")
        }
    }
}
```

### **API Configuration**
```kotlin
// Configure different environments
object Constants {
    object API {
        const val BASE_URL = BuildConfig.API_BASE_URL
        const val TIMEOUT_SECONDS = 30L
    }
    
    object Cache {
        const val EVENTS_TTL_MINUTES = 30L
        const val PROFILE_TTL_MINUTES = 15L
    }
}
```

##  Troubleshooting

### **Common Issues**

**Build Errors**
```bash
# Clean and rebuild
./gradlew clean
./gradlew build

# Clear Android Studio caches
File > Invalidate Caches and Restart
```

**Network Issues**
```kotlin
// Check network connectivity
if (!NetworkUtils.isNetworkAvailable(context)) {
    // Handle offline state
}
```

**Authentication Problems**
```kotlin
// Clear stored tokens
TokenManager.clearToken()
// Re-authenticate user
```

**Payment Integration Issues**
- Verify PhonePe SDK integration
- Check merchant credentials
- Validate API endpoints
- Review callback handling

### **Debugging Tools**
- **Network**: Use OkHttp logging interceptor
- **Cache**: Check cache statistics in logs
- **UI**: Use Layout Inspector
- **Performance**: Use Android Profiler

## 📚 API Documentation

### **Backend Requirements**
The app requires backend APIs for:
- User authentication and profile management
- Event CRUD operations
- Payment processing (PhonePe integration)
- File upload for event images

See [BACKEND_API_SPEC.md](BACKEND_API_SPEC.md) for detailed API specifications.

### **Architecture Documentation**
- **[MEDIATOR_PATTERN_IMPLEMENTATION.md](MEDIATOR_PATTERN_IMPLEMENTATION.md)**: Comprehensive guide to the Event Mediator Pattern implementation
- **[CACHING_IMPLEMENTATION.md](CACHING_IMPLEMENTATION.md)**: LRU caching system documentation

### **Key Endpoints**
```
POST /api/auth/google-signin
GET  /api/events
POST /api/events
GET  /api/events/{id}
POST /api/payment/create-order
GET  /api/payment/order-status/{orderId}
```

##  Deployment

### **Release Build**
```bash
# Generate signed APK
./gradlew assembleRelease

# Generate App Bundle (recommended)
./gradlew bundleRelease
```

### **Play Store Preparation**
1. Update version code and name in `build.gradle.kts`
2. Generate signed release build
3. Test on multiple devices
4. Prepare store listing materials
5. Upload to Play Console

##  Contributing

### **Getting Started**
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

### **Code Review Checklist**
- [ ] Code follows style guidelines
- [ ] All tests pass
- [ ] Documentation updated
- [ ] No memory leaks
- [ ] Proper error handling
- [ ] Accessibility considerations

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

##  Support

For technical support or questions:
- **Email**: support@example.com
- **Documentation**: See project wiki
- **Issue Tracker**: GitHub Issues tab

---

##  Recent Updates

### **v1.1.0** (Latest - January 2026)
- ✅ **Production-Grade Security**: Encrypted storage, secure token management, certificate pinning
- ✅ **Event Mediator Pattern Implementation**: Complete architectural enhancement
- ✅ **Advanced Event Filtering**: Real-time search and category filtering
- ✅ **Enhanced Event Actions**: Like, share, register with optimistic updates
- ✅ **Improved Error Handling**: Centralized error management with proper Exception handling
- ✅ **Code Quality Improvements**: Fixed linter errors and improved type safety
- ✅ **Reactive State Management**: StateFlow-based event state coordination
- ✅ **Enhanced Navigation**: Centralized navigation coordination through mediator
- ✅ **Event Creation Flow**: Coordinated multi-step wizard with draft saving
- ✅ **Listener Pattern**: Component communication for better separation of concerns
- ✅ **Documentation**: Comprehensive mediator pattern implementation guide

### **v1.0.0** (Base)
- ✅ Complete LRU caching system implementation
- ✅ PhonePe payment integration
- ✅ Google Sign-In authentication
- ✅ Multi-step event creation flow
- ✅ Comprehensive form validation
- ✅ Avatar customization system
- ✅ Material Design 3 UI


---
