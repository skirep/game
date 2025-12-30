# Level Editor - Vertical Shoot'em Up

A complete LibGDX Desktop application for creating and editing levels for a vertical scrolling shoot'em up game.

## Features

- **Visual Timeline Editor**: Vertical scrolling timeline where Y axis = TIME (seconds) and X axis = horizontal position (0.0 - 1.0 normalized)
- **Zoom and Pan**: Navigate the infinite vertical canvas with mouse wheel zoom and right-click pan
- **Event Management**: Add, drag, select, and delete events (Enemy, Formation, Boss, Power-Up)
- **Save/Load**: JSON-based level serialization to assets/levels directory
- **Clean Architecture**: Modular design with separated model, view, and controller

## Tech Stack

- Java
- LibGDX 1.12.1 (latest stable)
- Desktop (LWJGL3) backend
- Scene2D UI
- OrthographicCamera
- ShapeRenderer
- LibGDX Json

## Project Structure

```
editor/
 ├─ core/
 │   ├─ editor/
 │   │   ├─ EditorScreen.java        - Main editor screen with UI
 │   │   ├─ TimelineView.java        - Visual timeline with grid
 │   │   ├─ EventActor.java          - Draggable event representation
 │   │   ├─ EditorController.java    - Event management controller
 │   │   └─ LevelSerializer.java     - JSON save/load functionality
 │   ├─ model/
 │   │   ├─ LevelData.java           - Level data structure
 │   │   ├─ LevelEvent.java          - Event data structure
 │   │   └─ EventType.java           - Event type enumeration
 │   └─ EditorGame.java               - Main game class
 ├─ desktop/
 │   └─ DesktopLauncher.java          - Desktop launcher
 └─ assets/
     ├─ levels/                        - Saved level JSON files
     └─ uiskin.*                       - UI skin files

```

## Building and Running

### Prerequisites
- Java 8 or higher
- Gradle (included via wrapper)

### Build
```bash
./gradlew build
```

### Run
```bash
./gradlew desktop:run
```

## Usage

### Controls

- **Mouse Wheel**: Zoom in/out
- **Right Mouse Button**: Pan the timeline
- **Left Click**: Select an event
- **Drag Event**: Move event to new position/time
- **Delete/Backspace**: Delete selected event
- **Escape**: Deselect all

### UI Buttons

- **New**: Create a new empty level
- **Save**: Save current level to JSON file (specify filename in text field)
- **Load**: Load level from JSON file
- **Play**: Start preview mode (automatically scrolls timeline and highlights events)
- **Stop**: Stop preview mode and return to edit mode
- **Enemy** (Red): Add enemy spawn event at center of view
- **Formation** (Orange): Add formation event at center of view
- **Boss** (Purple): Add boss event at center of view
- **PowerUp** (Green): Add power-up event at center of view
- **Delete**: Delete currently selected event

### Preview Mode

- **Play Button**: Starts preview mode which:
  - Resets timeline to time 0
  - Automatically scrolls the camera vertically based on elapsed time
  - Highlights events with a yellow outline when they are triggered
  - Disables manual camera control and event editing
- **Stop Button**: Returns to edit mode with full manual control restored

### Timeline

- Horizontal grid lines represent time (1 second intervals)
- Vertical grid lines represent horizontal position (0.1 intervals)
- Events are represented as colored circles:
  - Red: Enemy
  - Orange: Formation
  - Purple: Boss
  - Green: Power-Up

## Data Model

### LevelData
```java
class LevelData {
    float levelLength;           // Total length in seconds
    Array<LevelEvent> events;    // All events
}
```

### LevelEvent
```java
class LevelEvent {
    float time;          // Time in seconds (Y axis)
    EventType type;      // ENEMY, FORMATION, BOSS, POWER_UP
    float x;             // Horizontal position (0.0 - 1.0, X axis)
    String enemyType;    // Type of enemy (e.g., "basic", "fast", "tank")
    String formationId;  // Formation pattern (e.g., "V", "line", "circle")
}
```

## Level Files

Levels are saved as JSON files in the `assets/levels/` directory. Example:

```json
{
  "levelLength": 120,
  "events": [
    {
      "time": 2.0,
      "type": "ENEMY",
      "x": 0.3,
      "enemyType": "basic",
      "formationId": "none"
    }
  ]
}
```

## License

All code and comments are in English as per project requirements.