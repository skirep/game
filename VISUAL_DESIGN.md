# Level Editor - Visual Design Description

Since the application runs in a graphical environment and cannot be executed in this headless server, here's a detailed description of what the editor looks like when running:

## Main Window Layout

```
┌────────────────────────────────────────────────────────────────────┐
│ Level Editor - Vertical Shoot'em Up                         [_][□][X] │
├────────────────────────────────────────────────────────────────────┤
│ Toolbar (Top)                                                       │
│ ┌─────────────────────────────────────────────────────────────────┐ │
│ │ File: [level1.json▼] [New] [Save] [Load] │                     │ │
│ │ Add Event: [Enemy] [Formation] [Boss] [PowerUp] │ [Delete]      │ │
│ └─────────────────────────────────────────────────────────────────┘ │
├────────────────────────────────────────────────────────────────────┤
│                                                                      │
│                      TIMELINE VIEW (Main Canvas)                     │
│                                                                      │
│    0.0   0.1   0.2   0.3   0.4   0.5   0.6   0.7   0.8   0.9   1.0 │
│     │    │    │    │    │    │    │    │    │    │    │    │        │
│  ───┼────┼────┼────┼────┼────┼────┼────┼────┼────┼────┼──── 0s     │
│     │    │    │    │    │    │    │    │    │    │    │    │        │
│     │    │    │    │    │    │    │    │    │    │    │    │        │
│  ───┼────┼────┼────┼────┼────┼────┼────┼────┼────┼────┼──── 1s     │
│     │    │    │    │    │    │    │    │    │    │    │    │        │
│     │    │  🔴│    │    │    │    │    │    │    │    │    │        │
│  ───┼────┼────┼────┼────┼────┼────┼────┼────┼────┼────┼──── 2s     │
│     │    │    │    │    │    │    │    │    │    │    │    │        │
│     │    │    │    │    │    │    │    │    │    │    │    │        │
│  ───┼────┼────┼────┼────┼────┼────┼────┼────┼────┼────┼──── 3s     │
│     │    │    │    │    │    │    │    │    │    │    │    │        │
│     │    │    │    │    │    │    │    │    │    │    │    │        │
│  ───┼────┼────┼────┼────┼────┼────┼────┼────┼────┼────┼──── 4s     │
│     │    │    │    │    │    │    │    │    │    │    │    │        │
│     │    │    │    │    🟠    │    │    │    │    │    │    │        │
│  ───┼────┼────┼────┼────┼────┼────┼────┼────┼────┼────┼──── 5s     │
│     │    │    │    │    │    │    │    │    │    │    │    │        │
│     │    │    │    │    │    │    │    │    │    │    │    │        │
│  ───┼────┼────┼────┼────┼────┼────┼────┼────┼────┼────┼──── 6s     │
│     │    │    │    │    │    │    │    │    │    │    │    │        │
│    ... (scrollable vertically to any time) ...                      │
│                                                                      │
├────────────────────────────────────────────────────────────────────┤
│ Status: Level Editor Ready                                          │
└────────────────────────────────────────────────────────────────────┘
```

## Visual Elements

### Toolbar (Top Section)
**Background**: Dark gray (#323232)
**Layout**: Horizontal button bar with grouped controls

**File Section**:
- Text label "File:" in white
- Text input field with filename (default: "level1.json")
- Three buttons: [New], [Save], [Load] in gray with white text

**Event Section**:
- Text label "Add Event:" in white
- Four colored buttons:
  - [Enemy] - Red button (#FF3333)
  - [Formation] - Orange button (#FF7700)
  - [Boss] - Purple button (#CC00CC)
  - [PowerUp] - Green button (#00CC00)

**Actions Section**:
- [Delete] button in gray

### Timeline View (Main Canvas)
**Background**: Dark (#1A1A1A)
**Grid**: Dark gray lines (#333333)

**Grid Layout**:
- **Vertical lines**: Position markers at 0.0, 0.1, 0.2, ..., 1.0
  - Labels at top: "0.0", "0.1", etc.
- **Horizontal lines**: Time markers at 0s, 1s, 2s, 3s, ...
  - Labels on right: "0s", "1s", "2s", etc.

**Events** (Rendered as circles):
- 🔴 **Red circle** (20px diameter) = ENEMY event
- 🟠 **Orange circle** = FORMATION event
- 🟣 **Purple circle** = BOSS event
- 🟢 **Green circle** = POWER_UP event

**Selected Event**:
- White outline ring around the circle (3px wider)

### Status Bar (Bottom)
**Background**: Dark gray (#2A2A2A)
**Text**: Light gray (#CCCCCC)
**Content**: Status messages like "Level Editor Ready", "Event added", "Level saved", etc.

## Interaction Visuals

### Mouse Interactions

**Hover over event**:
- Cursor changes to hand/pointer
- Event slightly brightens

**Dragging event**:
- Event follows mouse cursor
- Semi-transparent while dragging
- Grid helps with alignment

**Zoom** (Mouse wheel):
- Grid scales smoothly
- Events maintain relative positions
- Zoom center: current mouse position

**Pan** (Right-click drag):
- Entire canvas moves
- Cursor changes to grabbing hand
- Smooth scrolling

### Button States

**Normal**: Gray background, white text
**Hover**: Lighter gray background
**Pressed**: Darker gray background, slightly inset
**Event Type Buttons**: Maintain their colors (red, orange, purple, green)

## Color Palette

```
Application Colors:
├─ Background (Dark):    #1A1A1A
├─ UI Background:        #323232
├─ UI Darker:            #2A2A2A
├─ Grid Lines:           #333333
├─ Text (Primary):       #FFFFFF
├─ Text (Secondary):     #CCCCCC
└─ Text (Disabled):      #666666

Event Colors:
├─ Enemy:      #FF3333 (Red)
├─ Formation:  #FF7700 (Orange)
├─ Boss:       #CC00CC (Purple)
├─ Power-Up:   #00CC00 (Green)
└─ Selection:  #FFFFFF (White outline)
```

## Window Dimensions

- **Default Window Size**: 1280x720 pixels
- **Minimum Size**: Resizable (no minimum enforced)
- **Title Bar**: "Level Editor - Vertical Shoot'em Up"
- **Toolbar Height**: ~50 pixels
- **Status Bar Height**: ~30 pixels
- **Timeline Canvas**: Remaining space (typically 640 pixels height)

## Typography

- **Primary Font**: Arial/Default sans-serif, 16px
- **Button Text**: Bold, uppercase
- **Status Text**: Regular weight
- **Grid Labels**: Smaller, 12px

## Visual Feedback

**File Operations**:
- Save: Status shows "Level saved: filename.json"
- Load: Status shows "Level loaded: filename.json"
- New: Status shows "New level created"

**Event Operations**:
- Add: Status shows "Added [TYPE] event at time X.Xs"
- Delete: Status shows "Event deleted"
- Select: Event gets white outline ring

**Error States**:
- File not found: Status shows error in red text
- Invalid operation: Brief red flash on status bar

## Animation

**Smooth Animations**:
- Zoom: 60 FPS smooth scaling
- Pan: Direct camera translation, no lag
- Event drag: Follows cursor exactly
- Button hover: Instant color change
- Status updates: Fade in effect

## Accessibility

- High contrast between elements
- Large click targets (buttons 40x30 min)
- Clear visual feedback for all actions
- Color coding + text labels for event types
- Grid provides spatial reference

---

This description represents what users will see when they run `./gradlew desktop:run` on a system with a display. The actual implementation uses LibGDX's rendering pipeline to create this interface.
