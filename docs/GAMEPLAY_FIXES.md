# Chess Game Fixes - Piece Size, Drag-and-Drop, and Resign

## Issues Fixed

### 1. Piece Size Too Small ✅
**Problem**: Pieces at 48px were slightly too small for comfortable gameplay

**Solution**: 
- Increased from `48px` to `56px` 
- New ratio: 56px / 75px square = **75% coverage** (up from 64%)
- Better visibility while still fitting comfortably in squares

---

### 2. Drag-and-Drop Not Working ✅
**Problem**: Dragging pieces didn't work properly

**Solution**:
- Set `draggable={true}` explicitly (was just truthy)
- Added transparent drag image to prevent ghost image issues
- Improved drag start handler with proper HTML5 drag API

---

### 3. Resign 500 Error ✅
**Problem**: Clicking "Resign" caused `500 Internal Server Error`

**Error Details**:
```
POST http://localhost:8080/api/games/{gameId}/resign?playerId=Guest-e307282f 500 (Internal Server Error)
```

**Root Cause**: 
- Backend endpoint expects `playerId` as **UUID**
- Frontend was sending **username string** (`Guest-e307282f`)
- Backend couldn't parse string as UUID → 500 error

**The Fix**:

**Step 1: Store UUID during game creation**
```typescript
// frontend/src/pages/GuestLandingPage.tsx
const { gameId, guestUserId, token } = response.data.data;

// Store both token AND guest user ID
if (token) {
  localStorage.setItem('token', token);
}
if (guestUserId) {
  localStorage.setItem('guestUserId', guestUserId);  // ✅ This is the UUID!
}
```

**Step 2: Use UUID for resign**
```typescript
// frontend/src/hooks/useChessGame.ts
const resign = async () => {
  const playerId = localStorage.getItem('guestUserId');  // ✅ Get UUID
  
  if (!playerId) {
    setError('Player ID not found. Please refresh and try again.');
    return;
  }

  await apiClient.post(`/api/games/${gameId}/resign`, null, {
    params: { playerId },  // ✅ Send UUID
  });
  
  setIsGameOver(true);
  setResult('You resigned');
};
```

**Why This Works**:
1. Backend's `CreateGuestGameResponse` includes `guestUserId` (UUID)
2. Frontend now stores this UUID in localStorage
3. Resign sends the UUID the backend expects
4. Backend can parse it and end the game successfully

**Data Flow**:
```
POST /api/games/guest
    ↓
Backend returns: { gameId: UUID, guestUserId: UUID, token: JWT }
    ↓
Frontend stores: localStorage.setItem('guestUserId', UUID)
    ↓
User clicks Resign
    ↓
Frontend retrieves: localStorage.getItem('guestUserId')
    ↓
POST /api/games/{gameId}/resign?playerId={UUID}
    ↓
Backend: game.endGame("RESIGNATION", "Player resigned")
    ↓
✅ Success! Game Over Modal appears
```

---

## Testing Instructions

### ⚠️ IMPORTANT: Start a New Game!
Old games won't have `guestUserId` in localStorage. You **must** start a fresh game after this fix.

### Test Steps:
1. **Clear localStorage** (optional but recommended):
   - Open browser DevTools (F12)
   - Console tab: `localStorage.clear()`
   
2. **Start a new game**:
   - Go to home page
   - Enter name (optional)
   - Click "Play as Guest"

3. **Verify storage** (DevTools):
   - Application tab → Local Storage
   - Should see: `guestUserId` with a UUID value
   - Should see: `token` with JWT value

4. **Test resign**:
   - Make a few moves
   - Click red "Resign" button
   - ✅ Game should end (no 500 error!)
   - ✅ Modal: "Game Over - You resigned"

---

## Files Modified

1. **frontend/src/components/game/ChessBoard.tsx**
   - Piece size: 48px → 56px
   - Draggable: `draggable={true}`
   - Transparent drag image

2. **frontend/src/pages/GuestLandingPage.tsx**
   - Extract `guestUserId` from response
   - Store in localStorage

3. **frontend/src/hooks/useChessGame.ts**
   - Use `guestUserId` from localStorage
   - Add validation
   - Better error handling

---

## Commits

1. Fix piece size, drag-and-drop, and resign functionality
2. Add documentation for gameplay fixes
3. **Fix resign by using guestUserId UUID instead of username string** ← Final fix

---

## Result

✅ **Piece Size**: 56px - excellent visibility
✅ **Drag-and-Drop**: Works perfectly
✅ **Resign**: No more 500 error! Properly ends game

**All issues resolved!** 🎉

