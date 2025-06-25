# Metro Lines and Stations API Implementation Plan

Implementation plan for Metro Lines and Stations API following clean architecture with unified data flow:
Server --> datasource --> repository --> usecase --> UI

## Overview

This plan implements the Metro Lines and Stations API with the following endpoints:
- Metro Lines: List, Get by code, Create/Update
- Stations: List, Get by code, Create/Update, Bulk create

## Architecture Layers

### 1. Domain Layer (core/domain)

#### Domain Models
- `MetroLine` - Core metro line entity
- `Station` - Core station entity  
- `Segment` - Line segment between stations
- `LineStationInfo` - Junction information between lines and stations
- `PageData<T>` - Generic pagination wrapper

#### Repository Interfaces
- `MetroLineRepository` - Metro line data operations
- `StationRepository` - Station data operations

#### Use Cases
- **Metro Line Use Cases:**
  - `GetMetroLinesUseCase` - List metro lines with filtering and pagination
  - `GetMetroLineByCodeUseCase` - Get specific metro line by code
  - `CreateMetroLineUseCase` - Create new metro line
  - `UpdateMetroLineUseCase` - Update existing metro line

- **Station Use Cases:**
  - `GetStationsUseCase` - List stations with filtering and pagination
  - `GetStationByCodeUseCase` - Get specific station by code
  - `CreateStationUseCase` - Create new station
  - `UpdateStationUseCase` - Update existing station
  - `CreateStationListUseCase` - Create multiple stations

### 2. Data Layer (core/data)

#### Network DTOs
- `MetroLineDto` - API response model for metro lines
- `MetroLineRequest` - API request model for metro lines
- `StationDto` - API model for stations
- `SegmentDto` - API model for line segments
- `SegmentRequest` - API request model for segments
- `LineStationInfoDto` - API model for line-station junction
- `PageDto<T>` - API pagination wrapper

#### Data Sources
- `MetroLineRemoteDataSource` - Remote API calls for metro lines
- `StationRemoteDataSource` - Remote API calls for stations

#### API Services
- `MetroLineApiService` - Retrofit interface for metro line endpoints
- `StationApiService` - Retrofit interface for station endpoints

#### Repository Implementations
- `MetroLineRepositoryImpl` - Implements MetroLineRepository
- `StationRepositoryImpl` - Implements StationRepository

### 3. Feature Module Structure

Create a new feature module: `feature/metro-management`

## Implementation Tasks

### Phase 1: Domain Layer Setup

#### 1.1 Domain Models
- [x] Create `MetroLine` domain entity
- [x] Create `Station` domain entity
- [x] Create `Segment` domain entity
- [x] Create `LineStationInfo` domain entity
- [x] Create `PageData<T>` generic pagination model (already existed)

#### 1.2 Repository Interfaces
- [x] Create `MetroLineRepository` interface in domain layer
- [x] Create `StationRepository` interface in domain layer

#### 1.3 Use Cases
- [x] Implement `GetMetroLinesUseCase`
- [x] Implement `GetMetroLineByCodeUseCase`
- [x] Implement `CreateMetroLineUseCase`
- [x] Implement `UpdateMetroLineUseCase`
- [x] Implement `GetStationsUseCase`
- [x] Implement `GetStationByCodeUseCase`
- [x] Implement `CreateStationUseCase`
- [x] Implement `UpdateStationUseCase`
- [x] Implement `CreateStationListUseCase`

### Phase 2: Data Layer Implementation

#### 2.1 Network DTOs
- [x] Create `MetroLineDto` data class
- [x] Create `MetroLineRequestDto` data class
- [x] Create `StationDto` data class
- [x] Create `SegmentDto` data class
- [x] Create `SegmentRequestDto` data class
- [x] Create `LineStationInfoDto` data class
- [x] Create `PageDto<T>` generic data class (already existed)

#### 2.2 API Services
- [x] Create `MetroLineApi` Retrofit interface
- [x] Create `StationApi` Retrofit interface

#### 2.3 Data Sources
- [x] Implement repository pattern with ServerFlow (no separate data sources needed)

#### 2.4 Repository Implementations
- [x] Implement `MetroLineRepositoryImpl`
- [x] Implement `StationRepositoryImpl`

#### 2.5 Dependency Injection
- [x] Updated RetrofitServer with metro API services
- [x] Bind repository implementations in RepositoryModule
- [x] Created mappers for data transformation

### Phase 3: Feature Module Setup

#### 3.1 Module Structure
- [ ] Create `feature/metro-management` module
- [ ] Configure module dependencies
- [ ] Set up proper gradle configuration

#### 3.2 Testing Setup
- [ ] Create unit tests for use cases
- [ ] Create unit tests for repositories
- [ ] Create unit tests for data sources

## Detailed File Structure

```
core/domain/src/main/java/com/vidz/domain/
├── model/
│   ├── MetroLine.kt
│   ├── Station.kt
│   ├── Segment.kt
│   ├── LineStationInfo.kt
│   └── PageData.kt
├── repository/
│   ├── MetroLineRepository.kt
│   └── StationRepository.kt
└── usecase/
    ├── metro/
    │   ├── GetMetroLinesUseCase.kt
    │   ├── GetMetroLineByCodeUseCase.kt
    │   ├── CreateMetroLineUseCase.kt
    │   └── UpdateMetroLineUseCase.kt
    └── station/
        ├── GetStationsUseCase.kt
        ├── GetStationByCodeUseCase.kt
        ├── CreateStationUseCase.kt
        ├── UpdateStationUseCase.kt
        └── CreateStationListUseCase.kt

core/data/src/main/java/com/vidz/data/
├── dto/
│   ├── MetroLineDto.kt
│   ├── MetroLineRequest.kt
│   ├── StationDto.kt
│   ├── SegmentDto.kt
│   ├── SegmentRequest.kt
│   ├── LineStationInfoDto.kt
│   └── PageDto.kt
├── service/
│   ├── MetroLineApiService.kt
│   └── StationApiService.kt
├── datasource/
│   ├── MetroLineRemoteDataSource.kt
│   └── StationRemoteDataSource.kt
├── repository/
│   ├── MetroLineRepositoryImpl.kt
│   └── StationRepositoryImpl.kt
└── di/
    └── MetroApiModule.kt

feature/metro-management/src/main/java/com/vidz/metro/
├── di/
│   └── MetroManagementModule.kt
└── (Future UI implementation)
```

## Implementation Details

### Domain Models Example Structure

```kotlin
// MetroLine domain entity
data class MetroLine(
    val id: String,
    val code: String,
    val name: String,
    val color: String,
    val operatingHours: String,
    val status: String,
    val description: String,
    val segments: List<Segment>
)

// Station domain entity  
data class Station(
    val id: String,
    val code: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val status: String,
    val description: String,
    val lineStationInfos: List<LineStationInfo>
)
```

### Use Case Example Structure

```kotlin
class GetMetroLinesUseCase @Inject constructor(
    private val repository: MetroLineRepository
) {
    suspend operator fun invoke(
        name: String? = null,
        code: String? = null,
        status: String? = null,
        page: Int? = null,
        size: Int? = null
    ): Flow<Result<PageData<MetroLine>>> {
        return repository.getMetroLines(name, code, status, page, size)
    }
}
```

### Repository Interface Example

```kotlin
interface MetroLineRepository {
    suspend fun getMetroLines(
        name: String? = null,
        code: String? = null,
        status: String? = null,
        page: Int? = null,
        size: Int? = null
    ): Flow<Result<PageData<MetroLine>>>
    
    suspend fun getMetroLineByCode(code: String): Flow<Result<MetroLine>>
    suspend fun createMetroLine(request: MetroLine): Flow<Result<MetroLine>>
    suspend fun updateMetroLine(code: String, request: MetroLine): Flow<Result<MetroLine>>
}
```

## Success Criteria

- [x] All domain models properly defined
- [x] Repository interfaces following clean architecture
- [x] Use cases implement single responsibility principle
- [x] Data layer properly separated with DTOs
- [x] Proper error handling with Result wrapper
- [x] Mappers for data transformation between layers
- [x] Dependency injection properly configured
- [x] Follows project coding standards and architecture guidelines
- [ ] Unit tests covering all use cases (future implementation)

## Implementation Progress

### ✅ **Phase 1 & 2 COMPLETED**

**Domain Layer:**
- 4 domain models created (MetroLine, Station, Segment, LineStationInfo)
- 2 repository interfaces implemented
- 9 use cases implemented with proper dependency injection

**Data Layer:**
- 6 network DTOs created
- 2 Retrofit API interfaces implemented
- 2 repository implementations with ServerFlow pattern
- Complete mapper layer for data transformation
- Dependency injection configured

### 📋 **Current Status**
All Metro Lines and Stations API endpoints are now implemented up to the use case layer and ready for UI integration.

### 📁 **Files Created/Modified**

**Domain Layer:**
- core/domain/src/main/java/com/vidz/domain/model/MetroLine.kt ✅
- core/domain/src/main/java/com/vidz/domain/model/Station.kt ✅
- core/domain/src/main/java/com/vidz/domain/model/Segment.kt ✅
- core/domain/src/main/java/com/vidz/domain/model/LineStationInfo.kt ✅
- core/domain/src/main/java/com/vidz/domain/repository/MetroLineRepository.kt ✅
- core/domain/src/main/java/com/vidz/domain/repository/StationRepository.kt ✅
- core/domain/src/main/java/com/vidz/domain/usecase/metro/*.kt ✅ (4 use cases)
- core/domain/src/main/java/com/vidz/domain/usecase/station/*.kt ✅ (5 use cases)

**Data Layer:**
- core/data/src/main/java/com/vidz/data/server/dto/MetroLineDto.kt ✅
- core/data/src/main/java/com/vidz/data/server/dto/MetroLineRequestDto.kt ✅
- core/data/src/main/java/com/vidz/data/server/dto/StationDto.kt ✅
- core/data/src/main/java/com/vidz/data/server/dto/SegmentDto.kt ✅
- core/data/src/main/java/com/vidz/data/server/dto/SegmentRequestDto.kt ✅
- core/data/src/main/java/com/vidz/data/server/dto/LineStationInfoDto.kt ✅
- core/data/src/main/java/com/vidz/data/server/retrofit/api/MetroLineApi.kt ✅
- core/data/src/main/java/com/vidz/data/server/retrofit/api/StationApi.kt ✅
- core/data/src/main/java/com/vidz/data/server/retrofit/RetrofitServer.kt ✅ (updated)
- core/data/src/main/java/com/vidz/data/mapper/MetroMappers.kt ✅
- core/data/src/main/java/com/vidz/data/repository/MetroLineRepositoryImpl.kt ✅
- core/data/src/main/java/com/vidz/data/repository/StationRepositoryImpl.kt ✅
- core/data/src/main/java/com/vidz/data/di/RepositoryModule.kt ✅ (updated)

## Next Steps

After completing the use case layer:
1. Implement ViewModels following BaseViewModel interface
2. Create UI composables for metro management
3. Implement navigation and screen integration
4. Add proper error handling and loading states
5. Implement local caching if needed 