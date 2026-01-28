# Player Info with ELO and Avatars

## Summary
Added player information display with avatars and ELO ratings above and below the chessboard. Computer opponents show quirky avatars based on their difficulty level.

## Features Added

### 1. Player Info Component
Created a new `PlayerInfo` component that displays:
- **Avatar**: Emoji-based avatar (different for humans vs computers)
- **Username**: Player name or "Stockfish (Difficulty)"
- **ELO Rating**: Current rating
- **Active Indicator**: Pulsing green dot when it's their turn

### 2. Computer Avatars by Difficulty
- **Beginner (800 ELO)**: 🤖 Robot
- **Intermediate (1500 ELO)**: 🦾 Cyborg
- **Advanced (2400 ELO)**: 🧠 AI Brain

### 3. Human Player Avatars
- **White**: 👤 Single person
- **Black**: 👥 Multiple people

### 4. Active Turn Indicator
- Green border and pulsing dot when it's the player's turn
- Subtle background highlight
- Smooth transitions

## Backend Changes

### GameStateResponse
Enhanced to include full player information:
```java
public record GameStateResponse(
    // ... existing fields
    PlayerInfo whitePlayer,
    PlayerInfo blackPlayer
)

public record PlayerInfo(
    UUID id,
    String username,
    Integer eloRating,
    Boolean isGuest
)
```

### Computer ELO Assignment
Computer players now get appropriate ELO ratings:
- **Beginner**: 800 ELO
- **Intermediate**: 1500 ELO
- **Advanced**: 2400 ELO

## Frontend Changes

### useChessGame Hook
Added new state and return values:
- `whitePlayerInfo`: White player details
- `blackPlayerInfo`: Black player details
- `gameType`: Type of game (COMPUTER, GUEST, etc.)
- `currentTurn`: Whose turn it is ('white' or 'black')

### GamePage Layout
Updated to show player info:
- **Top**: Opponent player info
- **Middle**: Chessboard
- **Bottom**: Current player info

## Visual Design

### Player Info Card
- **Size**: 250px minimum width, 48px avatar
- **Background**: Semi-transparent with active state highlighting
- **Border**: 2px solid green when active, transparent otherwise
- **Animation**: Pulsing green dot for active player
- **Typography**: 
  - Username: 0.95rem, bold
  - ELO: 0.85rem, gray

### Color Scheme
- **Active**: Green (#4caf50) with 15% opacity background
- **Inactive**: White with 5% opacity background
- **Text**: Light gray (#e0e0e0) for names, darker gray (#9e9e9e) for ELO

## User Experience

### Turn Indication
Players can easily see:
1. Whose turn it is (green border + pulsing dot)
2. Both players' ELO ratings
3. Computer difficulty level
4. Player avatars for quick identification

### Computer Games
When playing against the computer:
- Computer shows as "Stockfish (Difficulty)"
- Appropriate quirky avatar based on strength
- Realistic ELO rating matching the difficulty

### Human Games
When playing against humans:
- Shows actual username
- Shows actual ELO rating
- Simple avatar based on color

## Testing

To test the new feature:

1. **Create a computer game** (any difficulty)
2. **Observe**:
   - Top shows computer with quirky avatar and ELO
   - Bottom shows your username and ELO
   - Active player has green border and pulsing dot
   - Turn indicator switches after each move

3. **Try different difficulties**:
   - Beginner: 🤖 with 800 ELO
   - Intermediate: 🦾 with 1500 ELO
   - Advanced: 🧠 with 2400 ELO

## Files Modified

### Backend
- `backend/src/main/java/com/checkmate/chess/dto/GameStateResponse.java`
- `backend/src/main/java/com/checkmate/chess/service/GameService.java`

### Frontend
- `frontend/src/components/game/PlayerInfo.tsx` (new)
- `frontend/src/hooks/useChessGame.ts`
- `frontend/src/pages/GamePage.tsx`

## Future Enhancements

Possible improvements:
1. **Custom avatars**: Allow users to upload profile pictures
2. **Rating history**: Show rating change after game
3. **Player stats**: Display wins/losses/draws
4. **Flags**: Show country flags for players
5. **Titles**: Display chess titles (GM, IM, etc.)
6. **Clock integration**: Add time remaining next to player info
7. **More avatars**: Different avatars based on rating ranges
