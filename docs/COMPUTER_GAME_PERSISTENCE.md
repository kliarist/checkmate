# Computer Game Persistence Implementation

## Overview
Computer games are now fully persisted to the database and use UUID-based URLs, allowing them to appear in game history and be accessed via shareable links.

## Changes Made

### Backend (Already Implemented)
The backend was already correctly persisting computer games:
- `GameService.createComputerGame()` saves the game to the database
- Returns a `CreateComputerGameResponse` with the `gameId`
- Computer player is created with username format: `"Computer-{uuid}"`
- Game type is set to `"COMPUTER"` for identification

### Frontend Changes

#### 1. ComputerGamePage.tsx
**Before**: Game was managed in local state, URL stayed at `/computer`
**After**: Simplified to only handle game creation, then navigates to `/game/{gameId}`

```typescript
// After creating game, navigate to game page with difficulty parameter
navigate(`/game/${response.gameId}?difficulty=${selectedDifficulty}`);
```

**Benefits**:
- Cleaner component (removed unused game state)
- Game has a persistent URL with UUID
- Can bookmark/share computer game links
- Games appear in history with proper URLs

#### 2. GamePage.tsx
**Before**: Only handled multiplayer games
**After**: Now handles both multiplayer and computer games

```typescript
// Extract difficulty from URL query parameters
const [searchParams] = useSearchParams();
const difficulty = searchParams.get('difficulty') || undefined;

// Pass difficulty to useChessGame hook
const { ... } = useChessGame(id!, difficulty);
```

**Benefits**:
- Single page handles all game types
- Computer difficulty preserved in URL
- Consistent game experience

#### 3. useChessGame.ts
**Before**: Only checked `guestUserId` for player color
**After**: Checks both `userId` and `guestUserId`

```typescript
const guestUserId = localStorage.getItem('guestUserId');
const userId = localStorage.getItem('userId');

// Check both guest and regular user IDs to determine player color
const currentUserId = userId || guestUserId;
if (currentUserId) {
  if (game.whitePlayerId === currentUserId) {
    setPlayerColor('white');
  } else if (game.blackPlayerId === currentUserId) {
    setPlayerColor('black');
  }
}
```

**Benefits**:
- Works for both guest and logged-in users
- Correctly identifies player color in computer games
- Supports game history for logged-in users

## User Flow

### Creating a Computer Game
1. User navigates to `/computer`
2. Selects difficulty (beginner/intermediate/advanced)
3. Selects color (white/black)
4. Clicks "Start Game"
5. Game is created and persisted to database
6. User is redirected to `/game/{gameId}?difficulty={difficulty}`

### Playing the Game
1. GamePage loads the game from database using gameId
2. Extracts difficulty from URL query parameter
3. useChessGame hook manages game state and WebSocket connection
4. Computer moves are triggered automatically via WebSocket
5. All moves are persisted to the database

### Game History
1. Computer games appear in user's game history (if logged in)
2. Each game has a unique URL: `/game/{gameId}?difficulty={difficulty}`
3. Users can revisit completed games
4. Games can be shared via URL

## Database Schema
Computer games use the existing `games` table:
- `id`: UUID (primary key)
- `white_player_id`: UUID (references users table)
- `black_player_id`: UUID (references users table, computer user)
- `game_type`: "COMPUTER"
- `current_fen`: Current board position
- `status`: "IN_PROGRESS", "COMPLETED", etc.
- `result`: Game outcome
- All other standard game fields

Computer users are stored in the `users` table:
- `username`: "Computer-{8-char-uuid}"
- `is_guest`: true
- Other fields as per guest users

## Testing

### Manual Testing Steps
1. **Create Computer Game**:
   - Log in to the application
   - Navigate to "Play vs Computer"
   - Select difficulty and color
   - Click "Start Game"
   - Verify URL changes to `/game/{gameId}?difficulty={difficulty}`

2. **Play Game**:
   - Make a move
   - Verify computer responds automatically
   - Complete the game

3. **Check History**:
   - Navigate to Profile page
   - Verify computer game appears in game history
   - Click on the game
   - Verify it loads correctly with the same URL

4. **Share URL**:
   - Copy the game URL
   - Open in a new tab/window
   - Verify game loads correctly

### Expected Behavior
- ✅ Computer games are persisted to database
- ✅ Each game has a unique UUID in the URL
- ✅ Games appear in user's game history
- ✅ Computer responds automatically to player moves
- ✅ Difficulty level is preserved in URL
- ✅ Player color is correctly identified
- ✅ Games can be revisited via URL

## Files Modified
1. `frontend/src/pages/ComputerGamePage.tsx` - Simplified, navigates to game page
2. `frontend/src/pages/GamePage.tsx` - Added difficulty parameter support
3. `frontend/src/hooks/useChessGame.ts` - Added userId check for player color

## Backward Compatibility
- ✅ Existing multiplayer games work unchanged
- ✅ Guest games work unchanged
- ✅ Ranked games work unchanged
- ✅ Private games work unchanged
- ✅ Only computer games affected by changes

## Future Enhancements
- Add "New Game" button on game end to quickly start another computer game
- Add difficulty selector in game history to filter computer games
- Add statistics for computer games (win rate by difficulty)
- Add option to change difficulty mid-game (start new game with same color)
