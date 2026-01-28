# Chessboard Move Navigation Fix

## Issue
When navigating through move history using keyboard controls (previous/next move), the chessboard display was getting out of sync with the actual position.

## Root Cause
The `react-chessboard` library's `Chessboard` component was not properly re-rendering when the `fen` prop changed during move navigation. The component uses internal state that wasn't being reset when the position changed programmatically.

## Solution
Added a `key` prop to the `Chessboard` component that changes with the FEN string:

```typescript
<Chessboard
  key={fen}  // Force re-render when FEN changes
  options={{
    position: fen,
    // ... other options
  }}
/>
```

## How It Works
React's `key` prop tells React to treat the component as a completely new instance when the key changes. By using `fen` as the key:
- When the FEN changes (during move navigation), React unmounts the old Chessboard instance
- React creates a new Chessboard instance with the new position
- This ensures the board display is always in sync with the FEN

## Trade-offs
- **Pro**: Guarantees board position is always correct
- **Pro**: Simple, one-line fix
- **Con**: Slightly less efficient (full re-render vs. update)
- **Con**: Loses any internal animation state during navigation

However, the trade-off is acceptable because:
1. Move navigation is not a high-frequency operation
2. Correctness is more important than animation smoothness during navigation
3. The performance impact is negligible on modern browsers

## Testing
To verify the fix:
1. Start a game (any type)
2. Make several moves
3. Use the move navigation controls (previous/next buttons or keyboard)
4. Verify the board position matches the move being displayed
5. Navigate forward and backward multiple times
6. Verify no visual glitches or position mismatches

## Files Modified
- `frontend/src/components/game/ChessBoard.tsx` - Added `key={fen}` to Chessboard component

## Related Issues
This fix also resolves any potential issues with:
- Board position not updating when loading a game
- Board position not updating when replaying moves
- Board position not updating when jumping to a specific move
