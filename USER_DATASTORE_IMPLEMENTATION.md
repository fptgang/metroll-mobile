# User Local DataStore Implementation

## Overview

This document describes the implementation of a user local datastore using DataStore Proto to store user information after login. The system provides real-time observation of local user data changes through a dedicated usecase.

## Architecture Components

### 1. Proto Schema Definition
- **File**: `core/datastore/src/main/proto/user_preferences.proto`
- **Purpose**: Defines the protobuf schema for user data storage
- **Messages**: 
  - `UserData`: Contains user account information
  - `UserPreferences`: Wrapper with login status

### 2. DataStore Proto Implementation
- **Class**: `UserDataStoreProto`
- **Location**: `core/datastore/src/main/java/com/vidz/datastore/UserDataStoreProto.kt`
- **Functionality**:
  - Stores and retrieves user account data
  - Manages login status
  - Provides reactive data streams

### 3. Repository Layer
- **Interface**: `UserLocalRepository`
- **Implementation**: `UserLocalRepositoryImpl`
- **Purpose**: Abstracts local user data operations
- **Location**: 
  - Interface: `core/domain/src/main/java/com/vidz/domain/repository/UserLocalRepository.kt`
  - Implementation: `core/datastore/src/main/java/com/vidz/datastore/repository/UserLocalRepositoryImpl.kt`

### 4. Use Case
- **Class**: `ObserveLocalAccountInfoUseCase`
- **Location**: `core/domain/src/main/java/com/vidz/domain/usecase/account/ObserveLocalAccountInfoUseCase.kt`
- **Purpose**: Provides reactive observation of local account information changes

### 5. Updated Components
- **UserRepositoryImpl**: Modified to use `UserDataStoreProto` instead of preferences
- **LoginViewModel**: Enhanced to observe local account changes

## Data Flow

```
Login Success → HybridLoginUseCase → UserRepository.saveUser() 
              ↓
UserRepositoryImpl → UserDataStoreProto.saveUser() 
              ↓
Proto DataStore (local storage)
              ↓
ObserveLocalAccountInfoUseCase → LoginViewModel (observes changes)
```

## Key Features

1. **Proto DataStore**: Uses efficient binary serialization instead of key-value preferences
2. **Reactive Streams**: Real-time observation of user data changes through Flow
3. **Type Safety**: Strongly typed user data with protobuf schema validation
4. **Clean Architecture**: Proper separation of concerns with repository pattern
5. **Dependency Injection**: Full Hilt integration for all components

## Usage

### Observing Local Account Information

#### Example in CustomerHomeViewModel:
```kotlin
@HiltViewModel
class CustomerHomeViewModel @Inject constructor(
    private val observeLocalAccountInfoUseCase: ObserveLocalAccountInfoUseCase
) : BaseViewModel<CustomerHomeEvent, CustomerHomeViewState, CustomerHomeViewModelState>() {
    
    init {
        observeLocalAccountInfo()
    }

    private fun observeLocalAccountInfo() {
        viewModelScope.launch {
            observeLocalAccountInfoUseCase().collect { account ->
                viewModelState.value = viewModelState.value.copy(
                    localAccount = account,
                    customerName = account?.fullName ?: "Guest User",
                    isLoggedIn = account != null
                )
            }
        }
    }
}
```

#### Example in StaffHomeViewModel:
```kotlin
@HiltViewModel
class StaffHomeViewModel @Inject constructor(
    private val observeLocalAccountInfoUseCase: ObserveLocalAccountInfoUseCase
) : BaseViewModel<StaffHomeEvent, StaffHomeViewState, StaffHomeViewModelState>() {
    
    private fun observeLocalAccountInfo() {
        viewModelScope.launch {
            observeLocalAccountInfoUseCase().collect { account ->
                viewModelState.value = viewModelState.value.copy(
                    localAccount = account,
                    staffName = account?.fullName ?: "Staff Member",
                    isLoggedIn = account != null
                )
            }
        }
    }
}
```

### Saving User Data (handled automatically by login flow)
The user data is automatically saved when `HybridLoginUseCase` completes successfully, as it calls `userRepository.saveUser(account)`.

## Configuration

### Build Dependencies
The following dependencies are required in `core/datastore/build.gradle.kts`:

```kotlin
dependencies {
    implementation(libs.androidx.datastore.core)
    implementation(libs.protobuf.kotlin.lite)
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.21.12"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") { option("lite") }
                create("kotlin") { option("lite") }
            }
        }
    }
}
```

## Benefits

1. **Performance**: Proto serialization is faster and more efficient than JSON/XML
2. **Type Safety**: Compile-time validation of data structure
3. **Versioning**: Proto schema evolution support for future updates
4. **Memory Efficient**: Binary format uses less memory than text formats
5. **Reactive**: Built-in Flow support for real-time data observation

## Files Modified/Created

### Created Files:
- `core/datastore/src/main/proto/user_preferences.proto`
- `core/datastore/src/main/java/com/vidz/datastore/UserDataStoreProto.kt`
- `core/domain/src/main/java/com/vidz/domain/repository/UserLocalRepository.kt`
- `core/datastore/src/main/java/com/vidz/datastore/repository/UserLocalRepositoryImpl.kt`
- `core/datastore/src/main/java/com/vidz/datastore/di/UserLocalDataModule.kt`
- `core/domain/src/main/java/com/vidz/domain/usecase/account/ObserveLocalAccountInfoUseCase.kt`

### Modified Files:
- `core/datastore/build.gradle.kts` - Added protobuf plugin and dependencies
- `core/domain/build.gradle.kts` - Added coroutines dependency
- `core/data/src/main/java/com/vidz/data/repository/UserRepositoryImpl.kt` - Updated to use proto datastore
- `feature/auth/src/main/java/com/vidz/auth/login/LoginViewModel.kt` - Added local account observation
- `feature/home/src/main/java/com/vidz/home/customerhome/CustomerHomeScreen.kt` - Integrated local account observation
- `feature/home/src/main/java/com/vidz/home/staffhome/StaffHomeScreen.kt` - Integrated local account observation

### Integration with Home Screens:
- `feature/home/src/main/java/com/vidz/home/customerhome/CustomerHomeViewModel.kt` - ViewModel with local account observation
- `feature/home/src/main/java/com/vidz/home/staffhome/StaffHomeViewModel.kt` - ViewModel with local account observation

## Real-time Data Updates

The home screens now automatically display:

### Customer Home Screen:
- Real user name instead of "John Doe"
- User email address when available
- Login status dependent messaging
- Account information updates in real-time

### Staff Home Screen:
- Real staff name instead of "Staff Member"
- User email and role information
- Login status dependent messaging
- Account role display (ADMIN, STAFF, CUSTOMER)

## Data Flow in Action

```
User Logs In → HybridLoginUseCase → UserRepository.saveUser() 
             ↓
UserRepositoryImpl → UserDataStoreProto.saveUser()
             ↓
Proto DataStore (persisted locally)
             ↓
ObserveLocalAccountInfoUseCase → CustomerHomeViewModel/StaffHomeViewModel
             ↓
UI Updates Automatically (CustomerHomeScreen/StaffHomeScreen)
```

When a user logs in, their information is immediately saved to the proto datastore and all screens observing the `ObserveLocalAccountInfoUseCase` will automatically update to show the real user information. 
