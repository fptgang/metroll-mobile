# Route Management Screen Implementation

Implementation of a route management screen with metro line selection and MapBox map visualization for displaying metro lines and their segments.

## Completed Tasks

- [x] Updated build.gradle.kts to include MapBox dependencies
- [x] Added core.domain dependency for accessing domain models
- [x] Created GetMetroLinesUseCase for fetching metro lines with default parameters (page=0, size=10)
- [x] Updated RouteManagementViewModel with proper state management:
  - [x] Added metro lines state
  - [x] Added selected metro line state
  - [x] Implemented event handling for loading and selecting lines
  - [x] Added proper error handling and loading states
- [x] Created MetroLineSelector reusable component in common/base/components
  - [x] Material 3 dropdown design
  - [x] Color indicator for each metro line
  - [x] Support for hex colors and named colors (RED, BLUE, etc.)
- [x] Created MetroLineMapView component for MapBox integration
  - [x] MapBox map with line drawing functionality
  - [x] Automatic camera positioning based on segment coordinates
  - [x] Line color parsing and rendering
  - [x] Placeholder states for no selection or no data
- [x] Created DI module for route-management feature
- [x] Implemented complete RouteManagementScreen with:
  - [x] Material 3 design with proper theming
  - [x] Metro line selector at the top
  - [x] Selected line information display
  - [x] MapBox map showing the selected line
  - [x] Loading states and error handling
  - [x] Refresh functionality with FAB
  - [x] Snackbar for error messages

## Implementation Details

### Data Flow Architecture

The implementation follows the clean architecture pattern:

```
MetroLineRepository --> GetMetroLinesUseCase --> RouteManagementViewModel --> RouteManagementScreen
                                                              |
                                                              +--> MetroLineSelector
                                                              +--> MetroLineMapView
```

### Key Features

1. **Metro Line Selection**: Dropdown selector showing all available metro lines with color indicators
2. **MapBox Integration**: Interactive map displaying selected metro line segments
3. **Real-time Updates**: Map updates automatically when a different line is selected
4. **Error Handling**: Proper error states and snackbar notifications
5. **Loading States**: Loading indicators while fetching data
6. **Material 3 Design**: Consistent with app design system

### Technical Components

1. **GetMetroLinesUseCase**: Fetches metro lines with pagination (page=0, size=10)
2. **MetroLineSelector**: Reusable dropdown component with color indicators
3. **MetroLineMapView**: MapBox component that draws line segments from station coordinates
4. **RouteManagementViewModel**: State management with proper event handling

### MapBox Features

- Draws lines between station coordinates for each segment
- Supports both hex colors (#FF0000) and named colors (RED, BLUE, etc.)
- Automatic camera positioning to center on the selected line
- Line styling with rounded caps and joins

## Relevant Files

- ✅ feature/route-management/build.gradle.kts - Added MapBox dependencies
- ✅ feature/route-management/src/main/java/com/vidz/routemanagement/domain/usecase/GetMetroLinesUseCase.kt - Use case implementation
- ✅ feature/route-management/src/main/java/com/vidz/routemanagement/management/RouteManagementViewModel.kt - Updated ViewModel
- ✅ feature/route-management/src/main/java/com/vidz/routemanagement/management/RouteManagementScreen.kt - Complete screen implementation
- ✅ feature/route-management/src/main/java/com/vidz/routemanagement/presentation/components/MetroLineMapView.kt - MapBox map component
- ✅ feature/route-management/src/main/java/com/vidz/routemanagement/di/RouteManagementModule.kt - DI module
- ✅ common/base/src/main/java/com/vidz/base/components/MetroLineSelector.kt - Reusable selector component

## Usage

The RouteManagementScreen can be integrated into the app navigation. It will:

1. Load metro lines on initialization
2. Display them in a dropdown selector with color indicators
3. Show the selected line information
4. Draw the selected line's segments on the MapBox map
5. Allow refreshing data with the FAB button

The screen follows Material 3 design guidelines and integrates seamlessly with the existing app architecture. 