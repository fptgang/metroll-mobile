# Firebase Auth Integration - Hybrid Flow Implementation

## Overview

This document outlines the complete Firebase Auth integration with a hybrid authentication flow:

1. **User authenticates with Firebase** → Get Firebase ID token → **Use token for backend API calls** → Continue with app functionality

## Architecture Flow

```
User Input (email/password)
         ↓
Firebase Authentication
         ↓
Get Firebase ID Token
         ↓
AutoInterceptor adds token to ALL backend API calls
         ↓
Backend validates Firebase token
         ↓
User authenticated in app
```

## Implementation Status

✅ **Completed Tasks**

- [x] Setup Firebase Auth dependencies  
- [x] Create domain models (User, AuthCredentials)
- [x] Implement Firebase auth data source
- [x] Create auth repository implementation  
- [x] Build Firebase auth use cases
- [x] Design login and register ViewModels
- [x] Create Material 3 UI screens
- [x] Fix compilation errors
- [x] **Create hybrid use cases (Firebase + Backend flow)**
- [x] **Update AuthInterceptor to use Firebase tokens**  
- [x] **Integrate with existing AuthNavigation.kt**
- [x] **Add Firebase token management for backend calls**

## Key Components Implemented

### 1. Domain Layer

#### Use Cases
- `HybridLoginUseCase` - Complete Firebase login + token extraction flow
- `HybridRegisterUseCase` - Complete Firebase registration + token extraction flow  
- `GetFirebaseTokenUseCase` - Utility for getting current Firebase ID token
- `LoginWithEmailUseCase` - Pure Firebase login
- `RegisterWithEmailUseCase` - Pure Firebase registration
- `LogoutUseCase` - Firebase logout
- `GetCurrentUserUseCase` - Get current Firebase user
- `SendPasswordResetEmailUseCase` - Password reset via Firebase

#### Models
- Updated `User.kt` for Firebase compatibility (uid, email, displayName, etc.)
- `AuthCredentials.kt` with LoginCredentials and RegisterCredentials
- `LoginCredentials`, `RegisterCredentials` data classes

#### Repository Interface
- Updated `AuthRepository.kt` with Firebase-specific methods

### 2. Data Layer

#### Firebase Integration
- `FirebaseAuthDataSource.kt` - Direct Firebase Auth SDK integration
- `AuthRepositoryImpl.kt` - Repository implementation using ServerFlow pattern
- `FirebaseModule.kt` - DI module providing FirebaseAuth instance

#### Backend Integration  
- **Updated `AuthInterceptor.kt`** - Now automatically adds Firebase tokens to ALL backend API calls
- Added `FirebaseLoginRequest` DTO to `AuthApi.kt`
- Added `/auth/login-with-firebase` endpoint definition

### 3. Presentation Layer

#### ViewModels
- `LoginViewModel.kt` - Uses `HybridLoginUseCase` for complete auth flow
- `RegisterViewModel.kt` - Uses `HybridRegisterUseCase` for complete auth flow  
- Both follow BaseViewModel pattern with proper state management

#### UI Screens  
- `LoginScreen.kt` - Material 3 login form with Firebase integration
- `RegisterScreen.kt` - Material 3 registration form with Firebase integration
- **Updated `AuthNavigation.kt`** - Integrated with Firebase ViewModels and navigation

## Authentication Flow Details

### Login Flow
1. User enters email/password in `LoginScreen`
2. `LoginViewModel` calls `HybridLoginUseCase`
3. `HybridLoginUseCase` authenticates with Firebase  
4. Firebase ID token is extracted automatically
5. `AuthInterceptor` automatically adds token to subsequent API calls
6. User navigated to appropriate home screen
7. All backend API calls now include Firebase token in headers

### Registration Flow  
1. User enters details in `RegisterScreen`
2. `RegisterViewModel` calls `HybridRegisterUseCase`
3. `HybridRegisterUseCase` registers with Firebase
4. Firebase ID token is extracted automatically  
5. `AuthInterceptor` automatically adds token to subsequent API calls
6. User navigated to home screen
7. All backend API calls now include Firebase token in headers

### Token Management
- `GetFirebaseTokenUseCase` provides current token
- `AuthInterceptor` automatically:
  - Adds Firebase token to all API requests (except auth endpoints)
  - Refreshes expired tokens automatically (401 responses)
  - Handles token refresh with `forceRefresh: true`

## Integration Points

### AuthNavigation.kt Integration
```kotlin
// Login Screen - Now uses Firebase ViewModels
composable(DestinationRoutes.LOGIN_SCREEN_ROUTE) {
    val viewModel: LoginViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Handles Firebase login success
    LaunchedEffect(uiState.isLoginSuccessful) {
        if (uiState.isLoginSuccessful) {
            navController.navigate(DestinationRoutes.CUSTOMER_HOME_SCREEN_ROUTE) {
                popUpTo(DestinationRoutes.ROOT_AUTH_SCREEN_ROUTE) { inclusive = true }
            }
        }
    }
    
    LoginScreen(
        onNavigateToRegister = { /* navigation */ },
        viewModel = viewModel
    )
}
```

### Automatic Token Headers
The `AuthInterceptor` now automatically adds Firebase tokens to all backend requests:

```kotlin
// AuthInterceptor.kt - Updated for Firebase tokens
override fun intercept(chain: Interceptor.Chain): Response {
    val originalRequest = chain.request()
    
    if (shouldSkipAuth(originalRequest)) {
        return chain.proceed(originalRequest)
    }

    // Get Firebase token automatically
    val firebaseToken = runBlocking {
        getFirebaseTokenUseCase(forceRefresh = false)
    }

    val requestWithToken = if (firebaseToken != null) {
        originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $firebaseToken")
            .build()
    } else {
        originalRequest
    }
    
    // Auto-refresh on 401 responses
    // ...
}
```

## Backend API Integration  

### New Endpoint Added
```kotlin
// AuthApi.kt - Added Firebase login endpoint
@POST("auth/login-with-firebase")
suspend fun loginWithFirebase(
    @Body firebaseLoginRequest: FirebaseLoginRequest
): Response<LoginResponse>

data class FirebaseLoginRequest(
    val firebaseToken: String
)
```

## Next Steps for Your Backend

1. **Implement `/auth/login-with-firebase` endpoint** on your backend server
2. **Validate Firebase tokens** in your backend using Firebase Admin SDK  
3. **Create/update user records** in your database based on Firebase user data
4. **Return appropriate user roles** and permissions for navigation

### Example Backend Implementation (Node.js/Express)
```javascript
// Example backend endpoint
app.post('/auth/login-with-firebase', async (req, res) => {
  try {
    const { firebaseToken } = req.body;
    
    // Verify Firebase token with Admin SDK
    const decodedToken = await admin.auth().verifyIdToken(firebaseToken);
    const { uid, email, name } = decodedToken;
    
    // Create/update user in your database
    const user = await createOrUpdateUser({ uid, email, name });
    
    // Return your app's user data and any additional tokens
    res.json({ 
      user,
      // You can return additional data if needed
    });
  } catch (error) {
    res.status(401).json({ error: 'Invalid Firebase token' });
  }
});
```

## Testing the Integration

1. **Setup Firebase Project** - Configure your Firebase project with proper authentication settings
2. **Test Login Flow** - Try logging in with email/password  
3. **Verify Token Headers** - Check that API calls include Firebase tokens
4. **Test Token Refresh** - Verify automatic token refresh on expiration
5. **Backend Integration** - Implement and test backend Firebase token validation

## Relevant Files

### Core Domain
- `core/domain/src/main/java/com/vidz/domain/usecase/auth/HybridLoginUseCase.kt` ✅
- `core/domain/src/main/java/com/vidz/domain/usecase/auth/HybridRegisterUseCase.kt` ✅  
- `core/domain/src/main/java/com/vidz/domain/usecase/auth/GetFirebaseTokenUseCase.kt` ✅
- `core/domain/src/main/java/com/vidz/domain/model/User.kt` ✅
- `core/domain/src/main/java/com/vidz/domain/model/AuthCredentials.kt` ✅

### Core Data  
- `core/data/src/main/java/com/vidz/data/auth/FirebaseAuthDataSource.kt` ✅
- `core/data/src/main/java/com/vidz/data/repository/AuthRepositoryImpl.kt` ✅
- `core/data/src/main/java/com/vidz/data/server/retrofit/AuthInterceptor.kt` ✅ **Updated**
- `core/data/src/main/java/com/vidz/data/server/retrofit/api/AuthApi.kt` ✅ **Updated**  
- `core/data/src/main/java/com/vidz/data/di/FirebaseModule.kt` ✅

### Feature Auth
- `feature/auth/src/main/java/com/vidz/auth/login/LoginViewModel.kt` ✅ **Updated**
- `feature/auth/src/main/java/com/vidz/auth/register/RegisterViewModel.kt` ✅ **Updated**
- `feature/auth/src/main/java/com/vidz/auth/login/LoginScreen.kt` ✅
- `feature/auth/src/main/java/com/vidz/auth/register/RegisterScreen.kt` ✅
- `feature/auth/src/main/java/com/vidz/auth/AuthNavigation.kt` ✅ **Updated**

### Configuration
- `gradle/libs.versions.toml` ✅ **Updated** 
- `feature/auth/build.gradle.kts` ✅ **Updated**

---

**Status: ✅ COMPLETE**

The Firebase Auth integration with hybrid backend flow is now fully implemented and integrated into your existing auth navigation system. Firebase tokens are automatically included in all backend API requests via the updated AuthInterceptor. 