# Computer Move After Promotion - Fix

## Problem
When a player promotes a pawn in a computer game, the computer doesn't make its next move.

## Root Cause Analysis

### Issue 1: Difficulty Not Being Sent
The frontend logs showed `difficulty: undefined` being sent to the server. This happened because:
1. For EXISTING games (created before the fix), the `timeControl` field in the database is `null`
2. The difficulty state was being set with a fallback, but the ref wasn't being updated immediately
3. This caused the difficulty to be `undefined` when the move was sent

### Issue 2: Potential Race Condition
The computer move was being triggered asynchronously immediately after the player's move. There might be a race condition where:
1. Player's move is processed
2. Computer turn check happens before the transaction is committed
3. Computer doesn't see the updated game state

## Fixes Applied

### Frontend Changes (`frontend/src/hooks/useChessGame.ts`)

1. **Immediate Ref Update**: When loading difficulty from `timeControl`, immediately update both state and ref:
   ```typescript
   const loadedDifficulty = game.timeControl || 'intermediate';
   setDifficulty(loadedDifficulty);
   difficultyRef.current = loadedDifficulty; // Immediate update
   ```

2. **Triple Fallback for Difficulty**: When sending move, use multiple fallbacks to ensure difficulty is never undefined:
   ```typescript
   const effectiveDifficulty = difficultyRef.current || difficulty || 'intermediate';
   ```

3. **Enhanced Logging**: Added detailed logging to track difficulty values through the flow

### Backend Changes (`backend/src/main/java/com/checkmate/chess/websocket/GameWebSocketHandler.java`)

1. **Added Transaction Delay**: Added a 100ms delay before checking if it's computer's turn to ensure the previous move transaction is committed:
   ```java
   Thread.sleep(100); // Ensure transaction is committed
   ```

2. **Improved Difficulty Handling**: Check for both null and empty string:
   ```java
   final String effectiveDifficulty = difficulty != null && !difficulty.isEmpty() 
       ? difficulty 
       : "intermediate";
   ```

3. **Enhanced Logging**: Added more detailed logging including the WebSocket topic path

## Testing Instructions

### Test 1: New Computer Game with Promotion
1. Create a NEW computer game (this will have difficulty stored in `timeControl`)
2. Play until you can promote a pawn
3. Promote the pawn
4. **Expected**: Computer should make its move within ~500ms
5. Check browser console for logs showing difficulty being sent correctly

### Test 2: Existing Computer Game with Promotion
1. Load an EXISTING computer game (one created before the fix, where `timeControl` is null)
2. Play until you can promote a pawn
3. Promote the pawn
4. **Expected**: Computer should make its move using 'intermediate' difficulty
5. Check browser console - should see: `using difficulty: intermediate`

### Test 3: Backend Logs
Check the backend console for these log messages when promotion happens:
```
Received move for game {id}: from=..., to=..., promotion=q, difficulty=intermediate
Move processed successfully
triggerComputerMoveIfNeeded called for game {id}, difficulty=intermediate
Is computer turn for game {id}: true
Generating computer move for game {id} with difficulty intermediate
Computer move generated: {move}, sending to WebSocket...
Computer move sent successfully to /topic/game/{id}/moves
```

## What to Look For

### Success Indicators
- ✅ Browser console shows difficulty is NOT undefined
- ✅ Backend logs show computer turn is detected as `true`
- ✅ Backend logs show computer move is generated and sent
- ✅ Computer makes a move within ~500ms after promotion
- ✅ No errors in browser or backend console

### Failure Indicators
- ❌ Browser console shows `difficulty: undefined`
- ❌ Backend logs show `Is computer turn: false`
- ❌ Backend logs show error generating computer move
- ❌ Computer doesn't make a move after promotion
- ❌ WebSocket connection errors

## Next Steps if Still Not Working

If the computer still doesn't move after promotion:

1. **Check WebSocket Connection**: Look for WebSocket errors in browser console
2. **Check Game State**: Verify the game is actually a computer game (gameType='COMPUTER')
3. **Check Player Detection**: Verify the computer player username starts with "Computer"
4. **Check FEN State**: Verify the FEN after promotion shows it's the computer's turn
5. **Check Stockfish**: Verify Stockfish service is working (check backend logs for Stockfish errors)

## Files Modified
- `frontend/src/hooks/useChessGame.ts`
- `backend/src/main/java/com/checkmate/chess/websocket/GameWebSocketHandler.java`
