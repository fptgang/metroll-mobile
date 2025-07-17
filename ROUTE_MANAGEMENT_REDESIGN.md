# Route Management Screen Redesign

## Overview
Redesigned the bottom sheet to focus on a specific use case: show start destination and list of available destinations for user selection. Applied flat design principles and fixed map animation issues.

## Completed Tasks

- [x] Redesigned bottom sheet with flat design principles
- [x] Implemented specific use case: start destination title + destination list
- [x] Fixed map animation issues (smooth height transition)
- [x] Added map focus functionality for start and end destinations
- [x] Implemented proper map caching to prevent unnecessary recomposition
- [x] Updated ViewModel to handle new destination selection flow
- [x] Enhanced destination selection UI with Material Design 3 components
- [x] Added proper visual feedback for destination selection
- [x] **NEW**: Default map positioning to show all stations on selected metro line
- [x] **NEW**: Fixed map focus when clearing selections (returns to default view)
- [x] **NEW**: Smart zoom calculation based on station distribution
- [x] **NEW**: Smooth height transition when expanding/collapsing map
- [x] **NEW**: Animated visibility for information box (Add to Cart Card)
- [x] **NEW**: Coordinated animations between map and information box

## Implementation Details

### Bottom Sheet Redesign
- **New Design**: Clean, flat design with start station display and destination list
- **Components Used**: Material Design 3 components (Surface, Card, HorizontalDivider)
- **User Flow**: Click station → See start station → Select destination → Auto-focus map

### Map Animation Improvements
- **Fixed Animation**: Reduced duration from 3000ms to 500ms for smoother transitions
- **Map Focus**: Automatically focuses on start and end destinations when journey is selected
- **Caching**: Added stable key to prevent unnecessary map recomposition
- **Height Animation**: Smooth transition between 400dp (no journey) and 300dp (with journey)
- **Default Positioning**: Map defaults to show all stations on selected metro line
- **Clear Selection Focus**: Returns to default view when selections are cleared
- **Weight Animation**: Smooth map weight transition (1f → 0.6f) when information box appears
- **Coordinated Timing**: Synchronized animations between map and information box

### Information Box Animations
- **Animated Visibility**: Smooth enter/exit animations for Add to Cart Card
- **Multi-layer Animation**: Combined slide, fade, and expand/shrink animations
- **Timing Coordination**: Staggered animation delays for smooth transitions
- **Enter Animation**: Slide up + fade in + expand (400ms with 100ms delay)
- **Exit Animation**: Slide down + fade out + shrink (300ms, immediate)

### ViewModel Updates
- **New Flow**: `onMapPointClick` → `selectEndStation` → Auto-fetch journey → Focus map
- **State Management**: Properly clears previous selections when starting new journey
- **Journey Fetching**: Automatically fetches P2P journeys when destination is selected

## Key Features

### Destination Selection Bottom Sheet
1. **Header**: "Select Destination" title
2. **Start Station Display**: Shows selected start station with icon and details
3. **Destination List**: Flat list of available destinations with train icons
4. **Visual Design**: Clean, modern UI with proper spacing and typography

### Map Focus Enhancement
1. **Auto-focus**: Automatically focuses map on start and end stations
2. **Smart Zoom**: Calculates optimal zoom level based on station distance
3. **Smooth Animation**: 500ms smooth height transition
4. **Enhanced Markers**: Larger, more visible markers for selected stations
5. **Default View**: Shows all stations on selected metro line when no journey is selected
6. **Clear Selection Behavior**: Returns to default view when clearing selections

### Smooth Transitions
1. **Map Weight Animation**: Smooth transition between full screen and 60% height
2. **Information Box Visibility**: Animated enter/exit with multiple animation layers
3. **Coordinated Timing**: Synchronized animations for seamless user experience
4. **Content Size Animation**: Smooth content size changes with animateContentSize

### Performance Improvements
1. **Map Caching**: Stable key prevents unnecessary recomposition
2. **Optimized Rendering**: Reduced animation duration for better UX
3. **Memory Management**: Proper cleanup of map layers and sources
4. **Smart Camera Positioning**: Efficient calculation of optimal camera position

## Technical Implementation

### Files Modified
- `RouteManagementScreen.kt` - Bottom sheet redesign, UI improvements, and animation implementation
- `MetroLineMapView.kt` - Animation fixes and map focus functionality
- `RouteManagementViewModel.kt` - Updated event handling and state management

### Key Technical Changes
1. **Stable Map Key**: `remember(selectedMetroLine?.id, p2pJourney?.id)` for caching
2. **LaunchedEffect**: Auto-focus map when journey is selected or cleared
3. **Enhanced Markers**: Larger, more visible station markers (12dp radius)
4. **Improved Animation**: Smooth height transitions with proper timing
5. **Default Camera Options**: Calculated based on selected metro line stations
6. **Smart Zoom Levels**: Dynamic zoom calculation based on station distribution
7. **Weight Animation**: `animateFloatAsState` for smooth map weight transitions
8. **AnimatedVisibility**: Complex enter/exit animations for information box

### Animation Implementation
```kotlin
// Map weight animation
val mapWeight by animateFloatAsState(
    targetValue = if (showAddToCartCard) 0.6f else 1f,
    animationSpec = tween(durationMillis = 400)
)

// Information box animated visibility
AnimatedVisibility(
    visible = showAddToCartCard,
    enter = slideInVertically() + fadeIn() + expandVertically(),
    exit = slideOutVertically() + fadeOut() + shrinkVertically()
)
```

### Map Positioning Logic
```kotlin
// Default positioning: Show all stations on selected metro line
val defaultCameraOptions = remember(selectedMetroLine, stations) {
    // Calculate bounds for all stations on the line
    // Set appropriate zoom level based on station distribution
}

// Journey focus: Show start and end destinations
LaunchedEffect(p2pJourney, stations, selectedMetroLine) {
    if (p2pJourney != null) {
        // Focus on start and end stations
    } else {
        // Return to default view
        mapViewportState.setCameraOptions(defaultCameraOptions)
    }
}
```

## User Experience Improvements

### Before
- Complex bottom sheet with multiple dropdowns
- Slow map animations (3000ms)
- No automatic map focus
- Confusing user flow
- Map focused on random location when clearing selections
- Abrupt appearance/disappearance of information box
- No smooth transitions between states

### After
- Simple, focused destination selection
- Fast, smooth animations (500ms)
- Automatic map focus on selected journey
- Clear, intuitive user flow
- **Default map view shows all stations on selected metro line**
- **Proper focus restoration when clearing selections**
- **Smart zoom levels based on station distribution**
- **Smooth height transitions when expanding/collapsing map**
- **Animated visibility for information box with multiple animation layers**
- **Coordinated animations between map and information box**

## Animation Specifications

### Map Weight Animation
- **Duration**: 400ms
- **Easing**: Default tween
- **Values**: 1f (full screen) ↔ 0.6f (with info box)

### Information Box Enter Animation
- **Slide In**: 400ms with 100ms delay
- **Fade In**: 300ms with 150ms delay
- **Expand Vertically**: 400ms with 100ms delay

### Information Box Exit Animation
- **Slide Out**: 300ms immediate
- **Fade Out**: 200ms immediate
- **Shrink Vertically**: 300ms immediate

## Future Enhancements

- [ ] Add search functionality for destinations
- [ ] Implement route preview on map
- [ ] Add journey time and cost preview
- [ ] Implement favorite destinations
- [ ] Add accessibility improvements
- [ ] Add map clustering for dense station areas
- [ ] Implement smooth camera transitions with animation
- [ ] Add haptic feedback for animations
- [ ] Implement spring animations for more natural feel 