# Level Editor - Implementation Details

## Architecture Overview

The Level Editor follows a clean MVC-like architecture with three main layers:

### Model Layer (`com.leveleditor.model`)

**EventType.java**
- Enumeration defining the four types of events: ENEMY, FORMATION, BOSS, POWER_UP

**LevelEvent.java**
- Data structure representing a single event in the level
- Contains: time (Y position), x position (0-1 normalized), type, enemyType, formationId
- Includes utility methods for copying events

**LevelData.java**
- Main level container holding all events and level length
- Provides methods to add/remove events and sort them by time
- Uses LibGDX Array for efficient event storage

**Formation.java**
- Reusable enemy formation pattern definition
- Contains: id (unique identifier), relativePositions (Array of Vector2), speed
- Defines spatial arrangement of enemies in a formation

**FormationData.java**
- Container for all formation definitions
- Provides methods to add/remove formations and lookup by ID
- Saved separately as formations.json for reusability across levels

### Editor Layer (`com.leveleditor.editor`)

**TimelineView.java**
- Manages the visual timeline rendering
- Uses OrthographicCamera for zoom and pan functionality
- Renders grid lines for time (horizontal) and position (vertical)
- Handles coordinate transformations between screen and world space
- Renders formation previews for FORMATION events
- Key features:
  - PIXELS_PER_SECOND = 100 for vertical scaling
  - Grid lines at 1 second intervals (time) and 0.1 intervals (position)
  - Zoom range: 0.5x to 3x
  - Pan with right/middle mouse button
  - Formation preview: small circles showing enemy positions relative to formation spawn point

**EventActor.java**
- Visual representation of events as colored circles
- Implements Scene2D Actor for easy integration
- Handles drag and drop functionality
- Color coding:
  - Red: ENEMY
  - Orange: FORMATION
  - Purple: BOSS
  - Green: POWER_UP
- Selection indication with outline
- Triggered state visualization with yellow pulsing outline (in preview mode)

**EditorController.java**
- Central controller managing interactions between model and view
- Handles:
  - Event creation at specified time/position
  - Event selection and deletion
  - Position updates when events are dragged
  - Save/load coordination with LevelSerializer
  - Formation data loading and management
  - Preview mode state management
  - Automatic timeline scrolling during preview
  - Event triggering and highlighting during preview

**LevelSerializer.java**
- JSON serialization/deserialization using LibGDX Json
- Pretty-prints JSON for human readability
- Handles file I/O errors gracefully

**FormationSerializer.java**
- JSON serialization/deserialization for formation definitions
- Saves/loads formations.json separately from level files
- Enables formation reusability across multiple levels

**FormationManagerDialog.java**
- UI dialog for managing formations (Create, Edit, Delete)
- Provides list view of all available formations
- Editor panel for defining formation properties:
  - ID: Unique identifier for the formation
  - Speed: Movement speed multiplier
  - Positions: List of relative enemy positions (x,y coordinates)
- Saves changes directly to formations.json
- Input validation for formation IDs and position data

**EventPropertiesDialog.java**
- UI dialog for editing event-specific properties
- Allows setting enemyType for all event types
- For FORMATION events, provides dropdown to select formation ID
- Displays read-only event information (type, time, position)

**EditorScreen.java**
- Main screen implementing LibGDX Screen interface
- Creates UI using Scene2D widgets
- Toolbar with buttons for:
  - File operations (New, Save, Load)
  - Formation management (Manage Formations)
  - Preview mode (Play, Stop)
  - Event creation (Enemy, Formation, Boss, PowerUp)
  - Event editing (Edit Properties)
  - Event deletion
- Status label for user feedback
- Coordinates input handling between UI and timeline
- Disables edit mode input during preview

### Application Layer

**EditorGame.java**
- Main LibGDX Game class
- Initializes and sets the EditorScreen

**DesktopLauncher.java**
- Desktop launcher using LWJGL3 backend
- Configures window size (1280x720), title, and FPS

## Key Design Decisions

### Coordinate System
- **Y axis = Time**: Vertical position represents seconds, with 100 pixels per second
- **X axis = Position**: Horizontal position normalized to 0.0-1.0 range
- This allows for intuitive timeline editing where events scroll vertically

### Event Dragging
- Events are Scene2D Actors with built-in input listeners
- Drag operations directly modify actor position
- Positions are synced back to model when mouse is released
- This provides smooth, real-time feedback

### Zoom and Pan
- Camera-based approach allows unlimited level length
- Zoom centered on current view
- Pan with right-click for intuitive navigation

### File Format
- JSON format for human readability and easy debugging
- **Level files** stored in assets/levels/ directory (e.g., sample.json)
  - Contains level length and events
  - Events reference formations by ID only (decoupled design)
- **Formation files** stored as assets/formations.json
  - Contains all formation definitions with positions and speed
  - Shared across all levels for reusability
  - Clean separation of concerns
- Example level structure documented in README
- Example formation structure:
```json
{
  "formations": [
    {
      "id": "V",
      "relativePositions": [
        {"x": 0.0, "y": 0.0},
        {"x": -0.1, "y": 0.1},
        {"x": 0.1, "y": 0.1}
      ],
      "speed": 1.0
    }
  ]
}
```

### UI Design
- Scene2D for consistent UI framework
- Custom skin with predefined styles
- Color-coded buttons match event types
- Status label provides immediate feedback
- Dynamic button visibility (Play/Stop toggle based on preview mode state)

### Preview Mode
- Time-based automatic scrolling at 1 second per second real-time
- Visual feedback when events are triggered (yellow pulsing outline)
- Disables edit controls during preview to prevent accidental modifications
- Camera resets to time 0 when preview starts
- All triggered states cleared when preview stops
- No actual gameplay logic - pure visualization mode

## Extension Points

The architecture is designed for easy extension:

1. **New Event Types**: Add to EventType enum and update EventActor color mapping
2. **Event Properties**: Extend LevelEvent class with new fields
3. **New Formation Patterns**: Use Formation Manager UI to create patterns dynamically
4. **Formation Algorithms**: Generate formation positions programmatically instead of manual entry
5. **UI Enhancements**: Add new buttons/controls to EditorScreen
6. **Validation**: Add rules in EditorController before saving
7. **Export Formats**: Implement additional serializers alongside LevelSerializer
8. **Preview Speed Control**: Add UI controls to adjust preview playback speed
9. **Formation Preview Modes**: Enhanced visualization with colors, labels, or animations

## Testing Approach

The application can be tested by:

1. **Build Test**: `./gradlew build` - Verifies compilation
2. **Manual Test**: `./gradlew desktop:run` - Launches the editor
3. **Load Test**: Load sample.json to verify deserialization
4. **Edit Test**: Add/move/delete events to verify all operations
5. **Save Test**: Save and reload to verify persistence
6. **Preview Test**: Click Play to verify automatic scrolling and event highlighting

## Performance Considerations

- ShapeRenderer used for efficient grid and event rendering
- Events sorted only when added or modified, not every frame
- Camera culling for grid lines (only visible range rendered)
- Efficient LibGDX Array for event storage

## Known Limitations

1. No undo/redo functionality (could be added with command pattern)
2. No multi-select (could be added with shift-click)
3. No copy/paste (could be added with clipboard)
4. No snap-to-grid (could be added as optional feature)
5. Fixed event size (could be made configurable)

These limitations are intentional to keep the implementation clean and focused on core functionality.
